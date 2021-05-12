package uk.co.ogauthority.pathfinder.service.projectarchive;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectarchive.ProjectArchiveDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectarchive.ArchiveProjectForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.projectarchive.ProjectArchiveDetailRepository;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectTypeModelUtil;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectHeaderSummaryService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ArchiveProjectService {

  public static final String ARCHIVE_PROJECT_TEMPLATE_PATH = "projectarchive/archive";

  private final ProjectArchiveDetailRepository projectArchiveDetailRepository;
  private final ProjectUpdateService projectUpdateService;
  private final CancelDraftProjectVersionService cancelDraftProjectVersionService;
  private final ProjectHeaderSummaryService projectHeaderSummaryService;
  private final ValidationService validationService;
  private final BreadcrumbService breadcrumbService;

  @Autowired
  public ArchiveProjectService(
      ProjectArchiveDetailRepository projectArchiveDetailRepository,
      ProjectUpdateService projectUpdateService,
      CancelDraftProjectVersionService cancelDraftProjectVersionService,
      ProjectHeaderSummaryService projectHeaderSummaryService,
      ValidationService validationService,
      BreadcrumbService breadcrumbService) {
    this.projectArchiveDetailRepository = projectArchiveDetailRepository;
    this.projectUpdateService = projectUpdateService;
    this.cancelDraftProjectVersionService = cancelDraftProjectVersionService;
    this.projectHeaderSummaryService = projectHeaderSummaryService;
    this.validationService = validationService;
    this.breadcrumbService = breadcrumbService;
  }

  @Transactional
  public ProjectArchiveDetail archiveProject(ProjectDetail latestSubmittedProjectDetail,
                                             AuthenticatedUserAccount user,
                                             ArchiveProjectForm form) {
    cancelDraftProjectVersionService.cancelDraftIfExists(latestSubmittedProjectDetail.getProject().getId());

    var newProjectDetail = projectUpdateService.createNewProjectVersion(latestSubmittedProjectDetail, ProjectStatus.ARCHIVED, user);
    var projectArchiveDetail = new ProjectArchiveDetail();
    projectArchiveDetail.setProjectDetail(newProjectDetail);
    projectArchiveDetail.setArchiveReason(form.getArchiveReason());
    return projectArchiveDetailRepository.save(projectArchiveDetail);
  }

  public ProjectArchiveDetail getProjectArchiveDetailOrError(ProjectDetail projectDetail) {
    return projectArchiveDetailRepository.findByProjectDetail(projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Unable to find ProjectArchiveDetail for ProjectDetail with id %d", projectDetail.getId())
        ));
  }

  public BindingResult validate(ArchiveProjectForm form, BindingResult bindingResult) {
    return validationService.validate(form, bindingResult, ValidationType.FULL);
  }

  public ModelAndView getArchiveProjectModelAndView(ProjectDetail projectDetail, AuthenticatedUserAccount user, ArchiveProjectForm form) {

    var projectId = projectDetail.getProject().getId();

    final var pageHeading = String.format(
        "%s %s",
        ArchiveProjectController.ARCHIVE_PROJECT_PAGE_NAME_PREFIX,
        projectDetail.getProjectType().getLowercaseDisplayName()
    );

    var modelAndView = new ModelAndView(ARCHIVE_PROJECT_TEMPLATE_PATH)
        .addObject("projectHeaderHtml", projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, user))
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ManageProjectController.class)
            .getProject(projectId, null, null, null))
        )
        .addObject("pageHeading", pageHeading);

    ProjectTypeModelUtil.addProjectTypeDisplayNameAttributesToModel(modelAndView, projectDetail);

    breadcrumbService.fromManageProject(
        projectDetail,
        modelAndView,
        pageHeading
    );
    return modelAndView;
  }
}
