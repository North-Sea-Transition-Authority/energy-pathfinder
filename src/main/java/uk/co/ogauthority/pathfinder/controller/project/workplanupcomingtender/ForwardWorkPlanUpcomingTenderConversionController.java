package uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSummaryController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderConversionForm;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerHeading;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerLink;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerTitle;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderConversionService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.util.notificationbanner.NotificationBannerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-upcoming-tenders/upcoming-tender/{upcomingTenderId}/convert/{displayOrder}")
public class ForwardWorkPlanUpcomingTenderConversionController extends ProjectFormPageController {

  public static final String CONVERT_PAGE_NAME = "Convert upcoming tender to awarded contract";
  public static final String BANNER_TITLE = "Success";
  public static final String BANNER_HEADING = "The upcoming tender has been converted to an awarded contract";
  public static final String BANNER_LINK_TEXT = "View awarded contracts";

  private final ForwardWorkPlanUpcomingTenderService upcomingTenderService;
  private final ForwardWorkPlanUpcomingTenderSummaryService upcomingTenderSummaryService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  private final ForwardWorkPlanUpcomingTenderConversionService conversionService;

  public ForwardWorkPlanUpcomingTenderConversionController(BreadcrumbService breadcrumbService,
                                                           ControllerHelperService controllerHelperService,
                                                           ForwardWorkPlanUpcomingTenderService upcomingTenderService,
                                                           ForwardWorkPlanUpcomingTenderSummaryService upcomingTenderSummaryService,
                                                           ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                                           ForwardWorkPlanUpcomingTenderConversionService conversionService) {
    super(breadcrumbService, controllerHelperService);
    this.upcomingTenderService = upcomingTenderService;
    this.upcomingTenderSummaryService = upcomingTenderSummaryService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
    this.conversionService = conversionService;
  }

  @GetMapping
  public ModelAndView convertUpcomingTenderConfirm(@PathVariable("projectId") Integer projectId,
                                                   @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                                   @PathVariable("displayOrder") Integer displayOrder,
                                                   ProjectContext projectContext) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    return getConvertUpcomingTenderConfirmModelAndView(upcomingTender, displayOrder, projectId, new UpcomingTenderConversionForm());
  }

  @PostMapping
  public ModelAndView convertUpcomingTender(@PathVariable("projectId") Integer projectId,
                                            @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                            @PathVariable("displayOrder") Integer displayOrder,
                                            @Valid @ModelAttribute("form") UpcomingTenderConversionForm form,
                                            BindingResult bindingResult,
                                            ProjectContext projectContext,
                                            RedirectAttributes redirectAttributes) {
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    bindingResult = conversionService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getConvertUpcomingTenderConfirmModelAndView(upcomingTender, displayOrder, projectId, form),
        form,
        () -> {
          conversionService.convertUpcomingTenderToAwardedContract(upcomingTender, form);
          AuditService.audit(
              AuditEvent.WORK_PLAN_UPCOMING_TENDER_CONVERTED,
              String.format(
                  AuditEvent.WORK_PLAN_UPCOMING_TENDER_CONVERTED.getMessage(),
                  upcomingTenderId,
                  projectContext.getProjectDetails().getId()
              )
          );
          addSuccessBanner(redirectAttributes, projectId);
          return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }

  private ModelAndView getConvertUpcomingTenderConfirmModelAndView(ForwardWorkPlanUpcomingTender upcomingTender,
                                                                   Integer displayOrder,
                                                                   Integer projectId,
                                                                   UpcomingTenderConversionForm form) {
    var modelAndView = new ModelAndView("project/workplanupcomingtender/convertForwardWorkPlanUpcomingTender")
        .addObject("form", form)
        .addObject("view", upcomingTenderSummaryService.getUpcomingTenderView(upcomingTender, displayOrder))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null)));
    breadcrumbService.fromWorkPlanUpcomingTenders(projectId, modelAndView, CONVERT_PAGE_NAME);
    return modelAndView;
  }

  private void checkIfUserHasAccessToTender(ForwardWorkPlanUpcomingTender upcomingTender) {
    if (!projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(
        upcomingTender.getProjectDetail(),
        new OrganisationGroupIdWrapper(upcomingTender.getAddedByOrganisationGroup())
    )) {
      throw new AccessDeniedException(
          String.format(
              "User does not have access to the ForwardWorkPlanUpcomingTender with id: %d",
              upcomingTender.getId())
      );
    }
  }

  private void addSuccessBanner(RedirectAttributes redirectAttributes, Integer projectId) {
    NotificationBannerUtils.successBannerWithLink(
        new NotificationBannerTitle(BANNER_TITLE),
        new NotificationBannerHeading(BANNER_HEADING),
        new NotificationBannerLink(
            ReverseRouter.route(on(ForwardWorkPlanAwardedContractSummaryController.class).viewAwardedContracts(projectId, null)),
            BANNER_LINK_TEXT),
        redirectAttributes
    );
  }
}
