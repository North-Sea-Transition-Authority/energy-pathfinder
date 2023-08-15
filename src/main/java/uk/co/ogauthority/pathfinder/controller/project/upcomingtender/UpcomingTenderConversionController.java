package uk.co.ogauthority.pathfinder.controller.project.upcomingtender;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.co.ogauthority.pathfinder.controller.project.awardedcontract.infrastructure.InfrastructureAwardedContractController;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.InvalidUpcomingTenderException;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
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
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderConversionService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.util.notificationbanner.NotificationBannerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/upcoming-tenders/upcoming-tender/{upcomingTenderId}/convert/{displayOrder}")
public class UpcomingTenderConversionController extends ProjectFormPageController {

  public static final String CONVERT_PAGE_NAME = "Convert upcoming tender to awarded contract";
  public static final String BANNER_TITLE = "Success";
  public static final String BANNER_HEADING = "The upcoming tender has been converted to an awarded contract";
  public static final String BANNER_LINK_TEXT = "View awarded contracts";

  private final UpcomingTenderService upcomingTenderService;
  private final UpcomingTenderSummaryService upcomingTenderSummaryService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;
  private final UpcomingTenderConversionService conversionService;

  @Autowired
  public UpcomingTenderConversionController(BreadcrumbService breadcrumbService,
                                            ControllerHelperService controllerHelperService,
                                            UpcomingTenderService upcomingTenderService,
                                            UpcomingTenderSummaryService upcomingTenderSummaryService,
                                            ProjectSectionItemOwnershipService projectSectionItemOwnershipService,
                                            UpcomingTenderConversionService conversionService) {
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
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
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
    var upcomingTender = upcomingTenderService.getOrError(upcomingTenderId);
    checkIfUserHasAccessToTender(upcomingTender);
    checkIfUpcomingTenderIsValid(upcomingTender);
    bindingResult = conversionService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getConvertUpcomingTenderConfirmModelAndView(upcomingTender, displayOrder, projectId, form),
        form,
        () -> {
          conversionService.convertUpcomingTenderToAwardedContract(upcomingTender, form);
          AuditService.audit(
              AuditEvent.UPCOMING_TENDER_CONVERTED,
              String.format(
                  AuditEvent.UPCOMING_TENDER_CONVERTED.getMessage(),
                  upcomingTenderId,
                  projectContext.getProjectDetails().getId()
              )
          );
          addSuccessBanner(redirectAttributes, projectId);
          return ReverseRouter.redirect(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }

  private ModelAndView getConvertUpcomingTenderConfirmModelAndView(UpcomingTender upcomingTender,
                                                                   Integer displayOrder,
                                                                   Integer projectId,
                                                                   UpcomingTenderConversionForm form) {
    var modelAndView = new ModelAndView("project/upcomingtender/convertUpcomingTender")
        .addObject("form", form)
        .addObject("view", upcomingTenderSummaryService.getValidatedUpcomingTenderView(upcomingTender, displayOrder))
        .addObject("cancelUrl", ReverseRouter.route(on(UpcomingTendersController.class).viewUpcomingTenders(projectId, null)));
    breadcrumbService.fromUpcomingTenders(projectId, modelAndView, CONVERT_PAGE_NAME);
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

  private void checkIfUpcomingTenderIsValid(UpcomingTender upcomingTender) {
    if(!upcomingTenderService.isValid(upcomingTender, ValidationType.FULL)) {
      throw new InvalidUpcomingTenderException(
          String.format(
              "Upcoming tender with id: %d is not in a valid state for conversion",
              upcomingTender.getId()
          )
      );
    }
  }

  private void addSuccessBanner(RedirectAttributes redirectAttributes, Integer projectId) {
    NotificationBannerUtils.successBannerWithLink(
        new NotificationBannerTitle(BANNER_TITLE),
        new NotificationBannerHeading(BANNER_HEADING),
        new NotificationBannerLink(
            ReverseRouter.route(on(InfrastructureAwardedContractController.class).viewAwardedContracts(projectId, null)),
            BANNER_LINK_TEXT),
        redirectAttributes
    );
  }
}
