package uk.co.ogauthority.pathfinder.service.project;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.submission.SubmitProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.service.email.RegulatorEmailService;
import uk.co.ogauthority.pathfinder.service.project.cleanup.ProjectCleanUpService;
import uk.co.ogauthority.pathfinder.service.project.submission.ProjectSubmissionSummaryViewService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class SubmitProjectService {

  public static final String PROJECT_SUBMIT_CONFIRMATION_TEMPLATE_PATH = "project/summary/submitConfirmation";

  private final ProjectDetailsRepository projectDetailsRepository;
  private final ProjectCleanUpService projectCleanUpService;
  private final ProjectSummaryViewService projectSummaryViewService;
  private final ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService;
  private final List<ProjectFormSectionService> projectFormSectionServices;
  private final RegulatorEmailService regulatorEmailService;
  private final RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Autowired
  public SubmitProjectService(ProjectDetailsRepository projectDetailsRepository,
                              ProjectCleanUpService projectCleanUpService,
                              ProjectSummaryViewService projectSummaryViewService,
                              ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService,
                              List<ProjectFormSectionService> projectFormSectionServices,
                              RegulatorEmailService regulatorEmailService,
                              RegulatorUpdateRequestService regulatorUpdateRequestService) {
    this.projectDetailsRepository = projectDetailsRepository;
    this.projectCleanUpService = projectCleanUpService;
    this.projectSummaryViewService = projectSummaryViewService;
    this.projectSubmissionSummaryViewService = projectSubmissionSummaryViewService;
    this.projectFormSectionServices = projectFormSectionServices;
    this.regulatorEmailService = regulatorEmailService;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
  }

  @Transactional
  public void submitProject(ProjectDetail projectDetail, AuthenticatedUserAccount user) {

    projectCleanUpService.removeProjectSectionDataIfNotRelevant(projectDetail);

    projectDetail.setStatus(ProjectStatus.QA);
    projectDetail.setSubmittedByWua(user.getWuaId());
    projectDetail.setSubmittedInstant(Instant.now());
    var updatedDetail = projectDetailsRepository.save(projectDetail);
    sendProjectUpdateSubmittedEmail(updatedDetail);
  }

  public boolean isProjectValid(ProjectDetail projectDetail) {
    return projectFormSectionServices
        .stream()
        .filter(projectFormSectionService -> projectFormSectionService.isTaskValidForProjectDetail(projectDetail))
        .allMatch(projectFormSectionService -> projectFormSectionService.isComplete(projectDetail));
  }

  public ModelAndView getProjectSubmitSummaryModelAndView(ProjectDetail projectDetail) {
    return getProjectSubmitSummaryModelAndView(projectDetail, isProjectValid(projectDetail));
  }

  public ModelAndView getProjectSubmitSummaryModelAndView(ProjectDetail projectDetail, boolean projectValid) {

    final var projectId = projectDetail.getProject().getId();

    var modelAndView = new ModelAndView("project/summary/reviewAndSubmit");
    var projectSummaryView = projectSummaryViewService.getProjectSummaryView(projectDetail);

    modelAndView
        .addObject("isUpdate", !projectDetail.isFirstVersion())
        .addObject("isProjectValid", projectValid)
        .addObject("projectSummaryView", projectSummaryView)
        .addObject("submitProjectUrl",
            ReverseRouter.route(on(SubmitProjectController.class).submitProject(projectId, null, Optional.empty()))
        )
        .addObject("updateRequestReason", regulatorUpdateRequestService.getUpdateRequestReason(
            projectDetail.getProject(),
            projectDetail.getVersion() - 1))
        .addObject("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    return modelAndView;
  }

  public ModelAndView getProjectSubmitConfirmationModelAndView(ProjectDetail projectDetail) {
    var modelAndView = new ModelAndView(PROJECT_SUBMIT_CONFIRMATION_TEMPLATE_PATH);

    var projectSubmissionSummaryView = projectSubmissionSummaryViewService
        .getProjectSubmissionSummaryView(projectDetail);

    modelAndView
        .addObject("isUpdate", !projectDetail.isFirstVersion())
        .addObject("projectSubmissionSummaryView", projectSubmissionSummaryView)
        .addObject("workAreaUrl", ControllerUtils.getWorkAreaUrl())
        .addObject("feedbackUrl", ControllerUtils.getFeedbackUrl(projectDetail.getId()));

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    return modelAndView;
  }

  private void sendProjectUpdateSubmittedEmail(ProjectDetail detail) {
    if (!detail.isFirstVersion()) {
      regulatorEmailService.sendUpdateSubmitConfirmationEmail(detail);
    }
  }
}
