package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import java.util.List;
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
import uk.co.ogauthority.pathfinder.controller.rest.TenderFunctionRestController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ContractBand;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.upcomingtender.UpcomingTenderView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/upcoming-tenders")
public class UpcomingTendersController extends PathfinderFileUploadController {

  public static final String PAGE_NAME = "Upcoming tenders";
  public static final String PAGE_NAME_SINGULAR = "Upcoming tender";
  public static final String REMOVE_PAGE_NAME = "Remove upcoming tender";

  private final BreadcrumbService breadcrumbService;
  private final ControllerHelperService controllerHelperService;
  private final UpcomingTenderService upcomingTenderService;
  private final UpcomingTenderSummaryService upcomingTenderSummaryService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;


  @Autowired
  public UpcomingTendersController(BreadcrumbService breadcrumbService,
                                   ControllerHelperService controllerHelperService,
                                   UpcomingTenderService upcomingTenderService,
                                   UpcomingTenderSummaryService upcomingTenderSummaryService,
                                   ProjectDetailFileService projectDetailFileService,
                                   FileDownloadService fileDownloadService,
                                   ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    super(projectDetailFileService, fileDownloadService);
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.upcomingTenderService = upcomingTenderService;
    this.upcomingTenderSummaryService = upcomingTenderSummaryService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  @GetMapping
  public ModelAndView viewUpcomingTenders(@PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext) {
    return getViewUpcomingTendersModelAndView(
        projectId,
        projectContext,
        upcomingTenderSummaryService.getSummaryViews(projectContext.getProjectDetails()),
        ValidationResult.NOT_VALIDATED
    );
  }

  @PostMapping
  public ModelAndView saveUpcomingTenders(@PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext) {
    var tenderViews = upcomingTenderSummaryService.getValidatedSummaryViews(
        projectContext.getProjectDetails()
    );

    var validationResult = upcomingTenderSummaryService.validateViews(tenderViews);

    if (validationResult.equals(ValidationResult.INVALID)) {
      return getViewUpcomingTendersModelAndView(
          projectId,
          projectContext,
          tenderViews,
          validationResult
      );
    }

    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  @GetMapping("/upcoming-tender")
  public ModelAndView addUpcomingTender(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    return getUpcomingTenderModelAndView(projectContext.getProjectDetails(), new UpcomingTenderForm());
  }


  @PostMapping("/upcoming-tender")
  public ModelAndView saveUpcomingTender(@PathVariable("projectId") Integer projectId,
                                        @Valid @ModelAttribute("form") UpcomingTenderForm form,
                                        BindingResult bindingResult,
                                        ValidationType validationType,
                                        ProjectContext projectContext) {
    bindingResult = upcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getUpcomingTenderModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var tender = upcomingTenderService.createUpcomingTender(
              projectContext.getProjectDetails(),
              form,
              projectContext.getUserAccount()
          );

          AuditService.audit(
              AuditEvent.UPCOMING_TENDER_UPDATED,
              String.format(
                  AuditEvent.UPCOMING_TENDER_UPDATED.getMessage(),
                  tender.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }

  @GetMapping("/upcoming-tender/{upcomingTenderId}/edit")
  public ModelAndView editUpcomingTender(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                         ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(
        upcomingTenderId,
        projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    return getUpcomingTenderModelAndView(
        projectContext.getProjectDetails(),
        upcomingTenderService.getForm(upcomingTender)
    );
  }

  @PostMapping("/upcoming-tender/{upcomingTenderId}/edit")
  public ModelAndView updateUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                           @Valid @ModelAttribute("form") UpcomingTenderForm form,
                                           BindingResult bindingResult,
                                           ValidationType validationType,
                                           ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    bindingResult = upcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getUpcomingTenderModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var tender = upcomingTenderService.updateUpcomingTender(upcomingTender, form, projectContext.getUserAccount());
          AuditService.audit(
              AuditEvent.UPCOMING_TENDER_UPDATED,
              String.format(
                  AuditEvent.UPCOMING_TENDER_UPDATED.getMessage(),
                  tender.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );
          return ReverseRouter.redirect(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }

  @GetMapping("/upcoming-tender/{upcomingTenderId}/remove/{displayOrder}")
  public ModelAndView removeUpcomingTenderConfirm(@PathVariable("projectId") Integer projectId,
                                                  @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                                  @PathVariable("displayOrder") Integer displayOrder,
                                                  ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    var modelAndView = new ModelAndView("project/upcomingtender/removeUpcomingTender")
          .addObject("view", upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, displayOrder))
          .addObject("cancelUrl", ReverseRouter.route(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null)));
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, REMOVE_PAGE_NAME);
    return modelAndView;
  }

  @PostMapping("/upcoming-tender/{upcomingTenderId}/remove/{displayOrder}")
  public ModelAndView removeUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                           @PathVariable("displayOrder") Integer displayOrder,
                                           ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    upcomingTenderService.delete(upcomingTender);
    AuditService.audit(
        AuditEvent.UPCOMING_TENDER_REMOVED,
        String.format(
            AuditEvent.UPCOMING_TENDER_REMOVED.getMessage(),
            upcomingTenderId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
  }

  @PostMapping("/upcoming-tender/files/upload")
  @ResponseBody
  public FileUploadResult handleUpload(@PathVariable("projectId") Integer projectId,
                                       @RequestParam("file") MultipartFile file,
                                       ProjectContext projectContext) {

    return projectDetailFileService.processInitialUpload(
        file,
        projectContext.getProjectDetails(),
        UpcomingTenderFileLinkService.FILE_PURPOSE,
        projectContext.getUserAccount()
    );

  }

  @GetMapping("{projectVersion}/upcoming-tender/files/download/{fileId}")
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
          "User with id %s cannot access upcoming tender file with id %s on project detail with id %s",
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

  @PostMapping("/upcoming-tender/files/delete/{fileId}")
  @ResponseBody
  public FileDeleteResult handleDelete(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("fileId") String fileId,
                                       ProjectContext projectContext) {
    return upcomingTenderService.deleteUpcomingTenderFile(
        fileId,
        projectContext.getProjectDetails(),
        projectContext.getUserAccount()
    );
  }

  private ModelAndView getViewUpcomingTendersModelAndView(
      Integer projectId,
      ProjectContext projectContext,
      List<UpcomingTenderView> tenderViews,
      ValidationResult validationResult
  ) {
    var modelAndView = new ModelAndView("project/upcomingtender/upcomingTenderFormSummary")
        .addObject("addTenderUrl", ReverseRouter.route(on(UpcomingTendersController.class).addUpcomingTender(projectId, null)))
        .addObject("tenderViews", tenderViews)
        .addObject("isValid", validationResult.equals(ValidationResult.VALID))
        .addObject("errorSummary",
            validationResult.equals(ValidationResult.INVALID)
              ? upcomingTenderSummaryService.getErrors(tenderViews)
              : null
        )
        .addObject("backToTaskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId));
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }

  private ModelAndView getUpcomingTenderModelAndView(ProjectDetail projectDetail, UpcomingTenderForm form) {
    var modelAndView = createModelAndView(
        "project/upcomingtender/upcomingTender",
        projectDetail,
        UpcomingTenderFileLinkService.FILE_PURPOSE,
        form
    )
        .addObject("tenderRestUrl", SearchSelectorService.route(
            on(TenderFunctionRestController.class).searchTenderFunctions(null)
        ))
        .addObject("form", form)
        .addObject("preSelectedFunction", upcomingTenderService.getPreSelectedFunction(form))
        .addObject("contractBands", ContractBand.getAllAsMap(ProjectType.INFRASTRUCTURE));
    breadcrumbService.fromUpcomingTenders(projectDetail.getProject().getId(), modelAndView, PAGE_NAME_SINGULAR);
    return modelAndView;
  }

  private void checkIfUserHasAccessToTender(UpcomingTender upcomingTender) {
    if (!projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        upcomingTender.getProjectDetail(),
        new OrganisationGroupIdWrapper(upcomingTender.getAddedByOrganisationGroup())
    )) {
      throw new AccessDeniedException(
          String.format(
              "User does not have access to the UpcomingTender with id: %d",
              upcomingTender.getId())
      );
    }
  }
}
