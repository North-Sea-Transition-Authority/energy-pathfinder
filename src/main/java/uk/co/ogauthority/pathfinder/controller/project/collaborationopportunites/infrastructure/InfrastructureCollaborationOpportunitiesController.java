package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.controller.file.FileDownloadService;
import uk.co.ogauthority.pathfinder.controller.file.PathfinderFileUploadController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.rest.InfrastructureCollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure.InfrastructureCollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.AccessService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/infrastructure/collaboration-opportunities")
public class InfrastructureCollaborationOpportunitiesController extends PathfinderFileUploadController {

  public static final String PAGE_NAME = "Collaboration opportunities";
  public static final String PAGE_NAME_SINGULAR = "Collaboration opportunity";
  public static final String REMOVE_PAGE_NAME = "Remove collaboration opportunity";

  private final BreadcrumbService breadcrumbService;
  private final ControllerHelperService controllerHelperService;
  private final InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;
  private final InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService;
  private final AccessService accessService;


  @Autowired
  public InfrastructureCollaborationOpportunitiesController(
      BreadcrumbService breadcrumbService,
      ControllerHelperService controllerHelperService,
      InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService,
      InfrastructureCollaborationOpportunitiesSummaryService infrastructureCollaborationOpportunitiesSummaryService,
      ProjectDetailFileService projectDetailFileService,
      FileDownloadService fileDownloadService,
      AccessService accessService) {
    super(projectDetailFileService, fileDownloadService);
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.infrastructureCollaborationOpportunitiesService = infrastructureCollaborationOpportunitiesService;
    this.infrastructureCollaborationOpportunitiesSummaryService = infrastructureCollaborationOpportunitiesSummaryService;
    this.accessService = accessService;
  }

  @GetMapping
  public ModelAndView viewCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return getViewCollaborationOpportunitiesModelAndView(
        projectId,
        infrastructureCollaborationOpportunitiesSummaryService.getSummaryViews(projectContext.getProjectDetails()),
        ValidationResult.NOT_VALIDATED,
        projectContext
      );
  }

  @PostMapping
  public ModelAndView saveCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    var views = infrastructureCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(
        projectContext.getProjectDetails()
    );

    var validationResult = infrastructureCollaborationOpportunitiesSummaryService.validateViews(views);

    if (validationResult.equals(ValidationResult.INVALID)) {
      return getViewCollaborationOpportunitiesModelAndView(
          projectId,
          views,
          validationResult,
          projectContext
      );
    }

    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }


  @GetMapping("/collaboration-opportunity")
  public ModelAndView addCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                  ProjectContext projectContext) {
    return getCollaborationOpportunityModelAndView(
        projectContext.getProjectDetails(),
        new InfrastructureCollaborationOpportunityForm()
    );
  }


  @PostMapping("/collaboration-opportunity")
  public ModelAndView saveCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @Valid @ModelAttribute("form") InfrastructureCollaborationOpportunityForm form,
                                                   BindingResult bindingResult,
                                                   ValidationType validationType,
                                                   ProjectContext projectContext) {
    bindingResult = infrastructureCollaborationOpportunitiesService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var collabOp = infrastructureCollaborationOpportunitiesService.createCollaborationOpportunity(
              projectContext.getProjectDetails(),
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.COLLABORATION_OPPORTUNITY_UPDATED,
              String.format(
                  AuditEvent.COLLABORATION_OPPORTUNITY_UPDATED.getMessage(),
                  collabOp.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(InfrastructureCollaborationOpportunitiesController.class)
              .viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView editCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @PathVariable("opportunityId") Integer opportunityId,
                                                   ProjectContext projectContext) {
    var opportunity = infrastructureCollaborationOpportunitiesService.getOrError(opportunityId);
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    return getCollaborationOpportunityModelAndView(
        projectContext.getProjectDetails(),
        infrastructureCollaborationOpportunitiesService.getForm(opportunity)
    );
  }


  @PostMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView updateCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @Valid @ModelAttribute("form") InfrastructureCollaborationOpportunityForm form,
                                                     BindingResult bindingResult,
                                                     ValidationType validationType,
                                                     ProjectContext projectContext) {
    var opportunity = infrastructureCollaborationOpportunitiesService.getOrError(opportunityId);
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    bindingResult = infrastructureCollaborationOpportunitiesService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var collabOp = infrastructureCollaborationOpportunitiesService.updateCollaborationOpportunity(
              opportunity,
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.COLLABORATION_OPPORTUNITY_UPDATED,
              String.format(
                  AuditEvent.COLLABORATION_OPPORTUNITY_UPDATED.getMessage(),
                  collabOp.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(InfrastructureCollaborationOpportunitiesController.class)
              .viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunityConfirm(@PathVariable("projectId") Integer projectId,
                                                            @PathVariable("opportunityId") Integer opportunityId,
                                                            @PathVariable("displayOrder") Integer displayOrder,
                                                            ProjectContext projectContext) {
    var opportunity = infrastructureCollaborationOpportunitiesService.getOrError(opportunityId);
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    var modelAndView = new ModelAndView(
        "project/collaborationopportunities/infrastructure/removeInfrastructureCollaborationOpportunity"
    )
        .addObject("view", infrastructureCollaborationOpportunitiesSummaryService.getView(opportunity, displayOrder))
        .addObject("cancelUrl", ReverseRouter.route(
              on(InfrastructureCollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null))
        );
    breadcrumbService.fromInfrastructureCollaborationOpportunities(projectId, modelAndView, REMOVE_PAGE_NAME);
    return modelAndView;
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @PathVariable("displayOrder") Integer displayOrder,
                                                     ProjectContext projectContext) {
    var opportunity = infrastructureCollaborationOpportunitiesService.getOrError(opportunityId);
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    infrastructureCollaborationOpportunitiesService.delete(opportunity);
    AuditService.audit(
        AuditEvent.COLLABORATION_OPPORTUNITY_REMOVED,
        String.format(
            AuditEvent.COLLABORATION_OPPORTUNITY_REMOVED.getMessage(),
            opportunityId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(InfrastructureCollaborationOpportunitiesController.class)
        .viewCollaborationOpportunities(projectId, null));
  }

  @PostMapping("/collaboration-opportunity/files/upload")
  @ResponseBody
  public FileUploadResult handleUpload(@PathVariable("projectId") Integer projectId,
                                       @RequestParam("file") MultipartFile file,
                                       ProjectContext projectContext) {

    return projectDetailFileService.processInitialUpload(
        file,
        projectContext.getProjectDetails(),
        InfrastructureCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        projectContext.getUserAccount()
    );

  }

  @GetMapping("{projectVersion}/collaboration-opportunity/files/download/{fileId}")
  @ProjectStatusCheck(status = {ProjectStatus.DRAFT, ProjectStatus.QA, ProjectStatus.PUBLISHED, ProjectStatus.ARCHIVED})
  @ResponseBody
  public ResponseEntity<Resource> handleDownload(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("projectVersion") Integer projectVersion,
                                                 @PathVariable("fileId") String fileId,
                                                 ProjectContext projectContext) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailVersionAndFileId(
        projectContext.getProjectDetails().getProject(),
        projectVersion,
        fileId
    );
    return serveFile(file);
  }

  @PostMapping("/collaboration-opportunity/files/delete/{fileId}")
  @ResponseBody
  public FileDeleteResult handleDelete(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("fileId") String fileId,
                                       ProjectContext projectContext) {
    return infrastructureCollaborationOpportunitiesService.deleteCollaborationOpportunityFile(
        fileId,
        projectContext.getProjectDetails(),
        projectContext.getUserAccount()
    );
  }

  private ModelAndView getViewCollaborationOpportunitiesModelAndView(
      Integer projectId,
      List<InfrastructureCollaborationOpportunityView> views,
      ValidationResult validationResult,
      ProjectContext projectContext
  ) {
    var modelAndView = new ModelAndView(
        "project/collaborationopportunities/infrastructure/infrastructureCollaborationOpportunitiesFormSummary"
    )
        .addObject(
            "addCollaborationOpportunityUrl",
            ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class).addCollaborationOpportunity(projectId, null))
        )
        .addObject("opportunityViews", views)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary",
          validationResult.equals(ValidationResult.INVALID)
            ? infrastructureCollaborationOpportunitiesSummaryService.getErrors(views)
            : null
        )
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId));
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getCollaborationOpportunityModelAndView(ProjectDetail projectDetail,
                                                               InfrastructureCollaborationOpportunityForm form) {
    var modelAndView = createModelAndView(
        "project/collaborationopportunities/infrastructure/infrastructureCollaborationOpportunityForm",
        projectDetail,
        InfrastructureCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        form
    )
        .addObject(
            "collaborationFunctionRestUrl",
            SearchSelectorService.route(on(InfrastructureCollaborationOpportunityRestController.class).searchFunctions(null))
        )
        .addObject("form", form)
        .addObject("preselectedCollaboration", infrastructureCollaborationOpportunitiesService.getPreSelectedCollaborationFunction(form));

    breadcrumbService.fromInfrastructureCollaborationOpportunities(
        projectDetail.getProject().getId(),
        modelAndView,
        PAGE_NAME_SINGULAR
    );
    return modelAndView;
  }

  private void checkIfUserHasAccessToCollaborationOpportunity(InfrastructureCollaborationOpportunity opportunity) {
    if (!accessService.canCurrentUserAccessProjectSectionInfo(
        opportunity.getProjectDetail(),
        new OrganisationGroupIdWrapper(opportunity.getAddedByOrganisationGroup())
    )) {
      throw new AccessDeniedException(
          String.format(
              "User does not have access to the InfrastructureCollaborationOpportunity with id: %d",
              opportunity.getId())
      );
    }
  }
}
