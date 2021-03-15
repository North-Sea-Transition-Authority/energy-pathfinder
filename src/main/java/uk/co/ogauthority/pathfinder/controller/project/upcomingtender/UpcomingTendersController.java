package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.controller.rest.TenderFunctionRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
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
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderFileLinkService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
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


  @Autowired
  public UpcomingTendersController(BreadcrumbService breadcrumbService,
                                   ControllerHelperService controllerHelperService,
                                   UpcomingTenderService upcomingTenderService,
                                   UpcomingTenderSummaryService upcomingTenderSummaryService,
                                   ProjectDetailFileService projectDetailFileService) {
    super(projectDetailFileService);
    this.breadcrumbService = breadcrumbService;
    this.controllerHelperService = controllerHelperService;
    this.upcomingTenderService = upcomingTenderService;
    this.upcomingTenderSummaryService = upcomingTenderSummaryService;
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
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
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
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
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
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);

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
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
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

  @GetMapping("/upcoming-tender/files/download/{fileId}")
  @ResponseBody
  public ResponseEntity<Resource> handleDownload(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("fileId") String fileId,
                                                 ProjectContext projectContext) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(
        projectContext.getProjectDetails(),
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
        .addObject("contractBands", ContractBand.getAllAsMap());
    breadcrumbService.fromUpcomingTenders(projectDetail.getProject().getId(), modelAndView, PAGE_NAME_SINGULAR);
    return modelAndView;
  }
}
