package uk.co.ogauthority.pathfinder.service.projectassessment;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectassessment.ProjectAssessment;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentFormValidator;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentValidationHint;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectassessment.ProjectAssessmentRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.projectpublishing.ProjectPublishingService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ProjectAssessmentService {

  private final ProjectAssessmentRepository projectAssessmentRepository;
  private final ValidationService validationService;
  private final ProjectAssessmentFormValidator projectAssessmentFormValidator;
  private final BreadcrumbService breadcrumbService;
  private final RegulatorUpdateRequestService regulatorUpdateRequestService;
  private final ProjectPublishingService projectPublishingService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;

  @Autowired
  public ProjectAssessmentService(ProjectAssessmentRepository projectAssessmentRepository,
                                  ValidationService validationService,
                                  ProjectAssessmentFormValidator projectAssessmentFormValidator,
                                  BreadcrumbService breadcrumbService,
                                  RegulatorUpdateRequestService regulatorUpdateRequestService,
                                  ProjectPublishingService projectPublishingService,
                                  ProjectHeaderSummaryService projectHeaderSummaryService) {
    this.projectAssessmentRepository = projectAssessmentRepository;
    this.validationService = validationService;
    this.projectAssessmentFormValidator = projectAssessmentFormValidator;
    this.breadcrumbService = breadcrumbService;
    this.regulatorUpdateRequestService = regulatorUpdateRequestService;
    this.projectPublishingService = projectPublishingService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
  }

  @Transactional
  public ProjectAssessment createProjectAssessment(ProjectDetail projectDetail,
                                                   AuthenticatedUserAccount assessor,
                                                   ProjectAssessmentForm form) {
    var projectAssessment = new ProjectAssessment();
    projectAssessment.setProjectDetail(projectDetail);
    projectAssessment.setReadyToBePublished(form.getReadyToBePublished());
    projectAssessment.setUpdateRequired(form.getUpdateRequired());
    projectAssessment.setAssessedInstant(Instant.now());
    projectAssessment.setAssessorWuaId(assessor.getWuaId());
    projectAssessmentRepository.save(projectAssessment);
    if (Boolean.TRUE.equals(projectAssessment.getReadyToBePublished())) {
      projectPublishingService.publishProject(projectDetail, assessor);
    }
    return projectAssessment;
  }

  public Optional<ProjectAssessment> getProjectAssessment(ProjectDetail projectDetail) {
    return projectAssessmentRepository.findByProjectDetail(projectDetail);
  }

  public boolean hasProjectBeenAssessed(ProjectDetail projectDetail) {
    return getProjectAssessment(projectDetail).isPresent();
  }

  public BindingResult validate(ProjectAssessmentForm form, BindingResult bindingResult, ProjectDetail projectDetail) {
    projectAssessmentFormValidator.validate(
        form,
        bindingResult,
        new ProjectAssessmentValidationHint(regulatorUpdateRequestService.canRequestUpdate(projectDetail))
    );
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ModelAndView getProjectAssessmentModelAndView(ProjectDetail projectDetail,
                                                       AuthenticatedUserAccount user,
                                                       ProjectAssessmentForm form) {
    var projectId = projectDetail.getProject().getId();
    var modelAndView = new ModelAndView("projectassessment/projectAssessment")
        .addObject("pageName", ProjectAssessmentController.PAGE_NAME)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("canRequestUpdate", regulatorUpdateRequestService.canRequestUpdate(projectDetail))
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class).getProject(projectId, null, null, null)));

    breadcrumbService.fromManageProject(projectDetail, modelAndView, ProjectAssessmentController.PAGE_NAME);

    return modelAndView;
  }
}
