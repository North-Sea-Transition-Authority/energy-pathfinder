package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites;

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
import uk.co.ogauthority.pathfinder.controller.file.PathfinderFileUploadController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.rest.CollaborationOpportunityRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunityFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/collaboration-opportunities")
public class CollaborationOpportunitiesController extends PathfinderFileUploadController {

  public static final String PAGE_NAME = "Collaboration opportunities";
  public static final String PAGE_NAME_SINGULAR = "Collaboration opportunity";
  public static final String REMOVE_PAGE_NAME = "Remove collaboration opportunity";

  private final BreadcrumbService breadcrumbService;
  private final ControllerHelperService controllerHelperService;
  private final CollaborationOpportunitiesService collaborationOpportunitiesService;
  private final CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService;


  @Autowired
  public CollaborationOpportunitiesController(BreadcrumbService breadcrumbService,
                                              ControllerHelperService controllerHelperService,
                                              CollaborationOpportunitiesService collaborationOpportunitiesService,
                                              CollaborationOpportunitiesSummaryService collaborationOpportunitiesSummaryService,
                                              ProjectDetailFileService projectDetailFileService) {
    super(projectDetailFileService);
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
    this.collaborationOpportunitiesSummaryService = collaborationOpportunitiesSummaryService;
  }

  @GetMapping
  public ModelAndView viewCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return getViewCollaborationOpportunitiesModelAndView(
        projectId,
        collaborationOpportunitiesSummaryService.getSummaryViews(projectContext.getProjectDetails()),
        ValidationResult.NOT_VALIDATED,
        projectContext
      );
  }

  @PostMapping
  public ModelAndView saveCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    var views = collaborationOpportunitiesSummaryService.getValidatedSummaryViews(
        projectContext.getProjectDetails()
    );

    var validationResult = collaborationOpportunitiesSummaryService.validateViews(views);

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
        new CollaborationOpportunityForm()
    );
  }


  @PostMapping("/collaboration-opportunity")
  public ModelAndView saveCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @Valid @ModelAttribute("form") CollaborationOpportunityForm form,
                                                   BindingResult bindingResult,
                                                   ValidationType validationType,
                                                   ProjectContext projectContext) {
    bindingResult = collaborationOpportunitiesService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var collabOp = collaborationOpportunitiesService.createCollaborationOpportunity(
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

          return ReverseRouter.redirect(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView editCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @PathVariable("opportunityId") Integer opportunityId,
                                                   ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);
    return getCollaborationOpportunityModelAndView(
        projectContext.getProjectDetails(),
        collaborationOpportunitiesService.getForm(opportunity)
    );
  }


  @PostMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView updateCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @Valid @ModelAttribute("form") CollaborationOpportunityForm form,
                                                     BindingResult bindingResult,
                                                     ValidationType validationType,
                                                     ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);
    bindingResult = collaborationOpportunitiesService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var collabOp = collaborationOpportunitiesService.updateCollaborationOpportunity(
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

          return ReverseRouter.redirect(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunityConfirm(@PathVariable("projectId") Integer projectId,
                                                            @PathVariable("opportunityId") Integer opportunityId,
                                                            @PathVariable("displayOrder") Integer displayOrder,
                                                            ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);

    var modelAndView = new ModelAndView("project/collaborationopportunities/removeCollaborationOpportunity")
        .addObject("view", collaborationOpportunitiesSummaryService.getView(opportunity, displayOrder))
        .addObject("cancelUrl", ReverseRouter.route(
              on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null))
        );
    breadcrumbService.fromCollaborationOpportunities(projectId, modelAndView, REMOVE_PAGE_NAME);
    return modelAndView;
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @PathVariable("displayOrder") Integer displayOrder,
                                                     ProjectContext projectContext) {
    var opportunity = collaborationOpportunitiesService.getOrError(opportunityId);
    collaborationOpportunitiesService.delete(opportunity);
    AuditService.audit(
        AuditEvent.COLLABORATION_OPPORTUNITY_REMOVED,
        String.format(
            AuditEvent.COLLABORATION_OPPORTUNITY_REMOVED.getMessage(),
            opportunityId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(CollaborationOpportunitiesController.class).viewCollaborationOpportunities(projectId, null));
  }

  @PostMapping("/collaboration-opportunity/files/upload")
  @ResponseBody
  public FileUploadResult handleUpload(@PathVariable("projectId") Integer projectId,
                                       @RequestParam("file") MultipartFile file,
                                       ProjectContext projectContext) {

    return projectDetailFileService.processInitialUpload(
        file,
        projectContext.getProjectDetails(),
        CollaborationOpportunityFileLinkService.FILE_PURPOSE,
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
    return collaborationOpportunitiesService.deleteCollaborationOpportunityFile(
        fileId,
        projectContext.getProjectDetails(),
        projectContext.getUserAccount()
    );
  }

  private ModelAndView getViewCollaborationOpportunitiesModelAndView(
      Integer projectId,
      List<CollaborationOpportunityView> views,
      ValidationResult validationResult,
      ProjectContext projectContext
  ) {
    var modelAndView = new ModelAndView("project/collaborationopportunities/collaborationOpportunitiesFormSummary")
        .addObject(
            "addCollaborationOpportunityUrl",
            ReverseRouter.route(on(CollaborationOpportunitiesController.class).addCollaborationOpportunity(projectId, null))
        )
        .addObject("opportunityViews", views)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary",
          validationResult.equals(ValidationResult.INVALID)
            ? collaborationOpportunitiesSummaryService.getErrors(views)
            : null
        )
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId));
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getCollaborationOpportunityModelAndView(ProjectDetail projectDetail, CollaborationOpportunityForm form) {
    var modelAndView = createModelAndView(
        "project/collaborationopportunities/collaborationOpportunityForm",
        projectDetail,
        CollaborationOpportunityFileLinkService.FILE_PURPOSE,
        form
    )
        .addObject(
            "collaborationFunctionRestUrl",
            SearchSelectorService.route(on(CollaborationOpportunityRestController.class).searchFunctions(null))
        )
        .addObject("form", form)
        .addObject("preselectedCollaboration", collaborationOpportunitiesService.getPreSelectedCollaborationFunction(form));

    breadcrumbService.fromCollaborationOpportunities(
        projectDetail.getProject().getId(),
        modelAndView,
        PAGE_NAME_SINGULAR
    );
    return modelAndView;
  }
}
