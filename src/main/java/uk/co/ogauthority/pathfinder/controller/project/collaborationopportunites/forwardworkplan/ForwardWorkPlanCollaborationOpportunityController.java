package uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.controller.file.FileDownloadService;
import uk.co.ogauthority.pathfinder.controller.file.PathfinderFileUploadController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationCompletionService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunitiesSummaryService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityModelService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationRoutingService;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationSetupService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/forward-work-plan/collaboration-opportunities")
public class ForwardWorkPlanCollaborationOpportunityController extends PathfinderFileUploadController {

  private final ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService;
  private final ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;
  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;
  private final ForwardWorkPlanCollaborationRoutingService forwardWorkPlanCollaborationRoutingService;
  private final ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService;
  private final ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService;
  private final ForwardWorkPlanCollaborationCompletionService forwardWorkPlanCollaborationCompletionService;
  private final ControllerHelperService controllerHelperService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityController(
      ForwardWorkPlanCollaborationOpportunityModelService forwardWorkPlanCollaborationOpportunityModelService,
      ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService,
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService,
      ForwardWorkPlanCollaborationRoutingService forwardWorkPlanCollaborationRoutingService,
      ForwardWorkPlanCollaborationSetupService forwardWorkPlanCollaborationSetupService,
      ForwardWorkPlanCollaborationOpportunitiesSummaryService forwardWorkPlanCollaborationOpportunitiesSummaryService,
      ForwardWorkPlanCollaborationCompletionService forwardWorkPlanCollaborationCompletionService,
      ProjectDetailFileService projectDetailFileService,
      ControllerHelperService controllerHelperService,
      FileDownloadService fileDownloadService,
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    super(projectDetailFileService, fileDownloadService);
    this.forwardWorkPlanCollaborationOpportunityModelService = forwardWorkPlanCollaborationOpportunityModelService;
    this.forwardWorkPlanCollaborationOpportunityFileLinkService = forwardWorkPlanCollaborationOpportunityFileLinkService;
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
    this.forwardWorkPlanCollaborationRoutingService = forwardWorkPlanCollaborationRoutingService;
    this.forwardWorkPlanCollaborationSetupService = forwardWorkPlanCollaborationSetupService;
    this.forwardWorkPlanCollaborationOpportunitiesSummaryService = forwardWorkPlanCollaborationOpportunitiesSummaryService;
    this.forwardWorkPlanCollaborationCompletionService = forwardWorkPlanCollaborationCompletionService;
    this.controllerHelperService = controllerHelperService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  @GetMapping("/setup")
  public ModelAndView getCollaborationOpportunitySetup(@PathVariable("projectId") Integer projectId,
                                                       ProjectContext projectContext,
                                                       AuthenticatedUserAccount userAccount) {
    return forwardWorkPlanCollaborationRoutingService.getCollaborationOpportunitySetupRoute(
        projectContext.getProjectDetails()
    );
  }

  @PostMapping("/setup")
  public ModelAndView saveCollaborationSetup(@PathVariable("projectId") Integer projectId,
                                             @Valid @ModelAttribute("form") ForwardWorkPlanCollaborationSetupForm form,
                                             BindingResult bindingResult,
                                             ProjectContext projectContext,
                                             AuthenticatedUserAccount userAccount) {

    final var projectDetail = projectContext.getProjectDetails();

    bindingResult = forwardWorkPlanCollaborationSetupService.validate(form, bindingResult, ValidationType.FULL);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        forwardWorkPlanCollaborationOpportunityModelService.getCollaborationSetupModelAndView(projectDetail, form),
        form,
        () -> {
          final var forwardWorkPlanCollaborationSetup = forwardWorkPlanCollaborationSetupService
              .saveForwardWorkPlanCollaborationSetup(form, projectDetail);

          return forwardWorkPlanCollaborationRoutingService.getPostSaveUpcomingCollaborationsSetupRoute(
              forwardWorkPlanCollaborationSetup,
              projectDetail
          );
        }
    );
  }

  @GetMapping("/summary")
  public ModelAndView viewCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     ProjectContext projectContext) {
    return forwardWorkPlanCollaborationRoutingService.getViewCollaborationsRoute(projectContext.getProjectDetails());
  }

  @PostMapping("/summary")
  public ModelAndView saveCollaborationOpportunities(@PathVariable("projectId") Integer projectId,
                                                     @Valid @ModelAttribute("form") ForwardWorkPlanCollaborationCompletionForm form,
                                                     BindingResult bindingResult,
                                                     ProjectContext projectContext) {

    final var projectDetail = projectContext.getProjectDetails();

    final var summaryViews = forwardWorkPlanCollaborationOpportunitiesSummaryService.getValidatedSummaryViews(
        projectDetail
    );

    final var validationResult = forwardWorkPlanCollaborationOpportunitiesSummaryService.validateViews(
        summaryViews
    );

    bindingResult = forwardWorkPlanCollaborationCompletionService.validate(form, bindingResult, ValidationType.FULL);

    if (validationResult.equals(ValidationResult.INVALID) || bindingResult.hasErrors()) {
      return forwardWorkPlanCollaborationOpportunityModelService.getViewCollaborationOpportunitiesModelAndView(
          projectDetail,
          validationResult,
          summaryViews,
          form,
          bindingResult
      );
    } else {

      final var forwardWorkPlanCollaborationSetup = forwardWorkPlanCollaborationCompletionService.saveCollaborationCompletionForm(
          form,
          projectDetail
      );

      return forwardWorkPlanCollaborationRoutingService.getPostSaveCollaborationsRoute(
          forwardWorkPlanCollaborationSetup,
          projectDetail
      );
    }
  }

  @GetMapping("/collaboration-opportunity")
  public ModelAndView addCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                  ProjectContext projectContext) {
    final var form = new ForwardWorkPlanCollaborationOpportunityForm();
    final var projectDetail = projectContext.getProjectDetails();
    return forwardWorkPlanCollaborationRoutingService.getAddCollaborationOpportunityRoute(
        getFileUploadModelAndView(form, projectDetail),
        form,
        projectDetail
    );
  }

  @PostMapping("/collaboration-opportunity")
  public ModelAndView saveCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @Valid @ModelAttribute("form") ForwardWorkPlanCollaborationOpportunityForm form,
                                                   BindingResult bindingResult,
                                                   ValidationType validationType,
                                                   ProjectContext projectContext) {
    bindingResult = forwardWorkPlanCollaborationOpportunityService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(form, projectContext.getProjectDetails()),
        form,
        () -> {
          var collaborationOpportunity = forwardWorkPlanCollaborationOpportunityService.createCollaborationOpportunity(
              projectContext.getProjectDetails(),
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.WORK_PLAN_COLLABORATION_UPDATED,
              String.format(
                  AuditEvent.WORK_PLAN_COLLABORATION_UPDATED.getMessage(),
                  collaborationOpportunity.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
              .viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView editCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                   @PathVariable("opportunityId") Integer opportunityId,
                                                   ProjectContext projectContext) {
    var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId, projectContext.getProjectDetails());
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    return getCollaborationOpportunityModelAndView(
        (ForwardWorkPlanCollaborationOpportunityForm) forwardWorkPlanCollaborationOpportunityService.getForm(opportunity),
        projectContext.getProjectDetails()
    );
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/edit")
  public ModelAndView updateCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @Valid @ModelAttribute("form") ForwardWorkPlanCollaborationOpportunityForm form,
                                                     BindingResult bindingResult,
                                                     ValidationType validationType,
                                                     ProjectContext projectContext) {
    var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId, projectContext.getProjectDetails());
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    bindingResult = forwardWorkPlanCollaborationOpportunityService.validate(form, bindingResult, validationType);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCollaborationOpportunityModelAndView(form, projectContext.getProjectDetails()),
        form,
        () -> {
          var collaboration = forwardWorkPlanCollaborationOpportunityService.updateCollaborationOpportunity(
              opportunity,
              form,
              projectContext.getUserAccount()
          );
          AuditService.audit(
              AuditEvent.WORK_PLAN_COLLABORATION_UPDATED,
              String.format(
                  AuditEvent.WORK_PLAN_COLLABORATION_UPDATED.getMessage(),
                  collaboration.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
              .viewCollaborationOpportunities(projectId, null));
        }
    );
  }

  @GetMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunityConfirm(@PathVariable("projectId") Integer projectId,
                                                            @PathVariable("opportunityId") Integer opportunityId,
                                                            @PathVariable("displayOrder") Integer displayOrder,
                                                            ProjectContext projectContext) {
    var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId, projectContext.getProjectDetails());
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    return forwardWorkPlanCollaborationOpportunityModelService.getRemoveCollaborationOpportunityConfirmationModelAndView(
        projectId,
        opportunity,
        displayOrder
    );
  }

  @PostMapping("/collaboration-opportunity/{opportunityId}/remove/{displayOrder}")
  public ModelAndView removeCollaborationOpportunity(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("opportunityId") Integer opportunityId,
                                                     @PathVariable("displayOrder") Integer displayOrder,
                                                     ProjectContext projectContext) {
    final var opportunity = forwardWorkPlanCollaborationOpportunityService.getOrError(opportunityId, projectContext.getProjectDetails());
    checkIfUserHasAccessToCollaborationOpportunity(opportunity);
    forwardWorkPlanCollaborationOpportunityService.delete(opportunity);

    AuditService.audit(
        AuditEvent.WORK_PLAN_COLLABORATION_REMOVED,
        String.format(
            AuditEvent.WORK_PLAN_COLLABORATION_REMOVED.getMessage(),
            opportunityId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(ForwardWorkPlanCollaborationOpportunityController.class)
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
        ForwardWorkPlanCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        projectContext.getUserAccount()
    );

  }

  @GetMapping("{projectVersion}/collaboration-opportunity/files/download/{fileId}")
  @ProjectStatusCheck(status = {ProjectStatus.DRAFT, ProjectStatus.QA, ProjectStatus.PUBLISHED, ProjectStatus.ARCHIVED})
  @ProjectFormPagePermissionCheck(permissions = ProjectPermission.VIEW)
  @ResponseBody
  public ResponseEntity<Resource> handleDownload(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("projectVersion") Integer projectVersion,
                                                 @PathVariable("fileId") String fileId,
                                                 ProjectContext projectContext) {
    if (!projectDetailFileService.canAccessFiles(
        projectContext.getProjectDetails(),
        projectContext.getUserAccount().getLinkedPerson()
    )) {
      throw new AccessDeniedException(String.format(
          "User with id %s cannot access work plan collaboration file with id %s on project detail with id %s",
          projectContext.getUserAccount().getWuaId(),
          fileId,
          projectContext.getProjectDetails().getId()
      ));
    }
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
    return forwardWorkPlanCollaborationOpportunityFileLinkService.deleteCollaborationOpportunityFile(
        fileId,
        projectContext.getProjectDetails(),
        projectContext.getUserAccount()
    );
  }

  private ModelAndView getCollaborationOpportunityModelAndView(ForwardWorkPlanCollaborationOpportunityForm form,
                                                               ProjectDetail projectDetail) {

    final var modelAndView = getFileUploadModelAndView(form, projectDetail);
    return forwardWorkPlanCollaborationOpportunityModelService.getCollaborationOpportunityModelAndView(
        modelAndView,
        form,
        projectDetail.getProject().getId()
    );
  }

  private ModelAndView getFileUploadModelAndView(ForwardWorkPlanCollaborationOpportunityForm form,
                                                 ProjectDetail projectDetail) {
    return createModelAndView(
        ForwardWorkPlanCollaborationOpportunityModelService.FORM_TEMPLATE_PATH,
        projectDetail,
        ForwardWorkPlanCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        form
    );
  }

  private void checkIfUserHasAccessToCollaborationOpportunity(ForwardWorkPlanCollaborationOpportunity opportunity) {
    if (!projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        opportunity.getProjectDetail(),
        new OrganisationGroupIdWrapper(opportunity.getAddedByOrganisationGroup())
    )) {
      throw new AccessDeniedException(
          String.format(
              "User does not have access to the ForwardWorkPlanCollaborationOpportunity with id: %d",
              opportunity.getId())
      );
    }
  }
}
