package uk.co.ogauthority.pathfinder.controller.project.workplanupcomingtender;

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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanTenderSetupForm;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.OrganisationGroupIdWrapper;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanTenderCompletionService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanTenderRoutingService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanTenderSetupService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderModelService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderSummaryService;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@AllowProjectContributorAccess
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-upcoming-tenders")
public class ForwardWorkPlanUpcomingTenderController {

  public static final String PAGE_NAME = "Upcoming tenders";
  public static final String PAGE_NAME_SINGULAR = "Upcoming tender";
  public static final String REMOVE_PAGE_NAME = "Remove upcoming tender";

  private final ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService;
  private final ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;
  private final ControllerHelperService controllerHelperService;
  private final ForwardWorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService;
  private final ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService;
  private final ForwardWorkPlanTenderRoutingService forwardWorkPlanTenderRoutingService;
  private final ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService;
  private final ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  @Autowired
  public ForwardWorkPlanUpcomingTenderController(
      ForwardWorkPlanUpcomingTenderService workPlanUpcomingTenderService,
      ForwardWorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService,
      ControllerHelperService controllerHelperService,
      ForwardWorkPlanUpcomingTenderModelService workPlanUpcomingTenderModelService,
      ForwardWorkPlanTenderSetupService forwardWorkPlanTenderSetupService,
      ForwardWorkPlanTenderRoutingService forwardWorkPlanTenderRoutingService,
      ForwardWorkPlanTenderCompletionService forwardWorkPlanTenderCompletionService,
      ProjectSectionItemOwnershipService projectSectionItemOwnershipService) {
    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
    this.workPlanUpcomingTenderSummaryService = workPlanUpcomingTenderSummaryService;
    this.controllerHelperService = controllerHelperService;
    this.workPlanUpcomingTenderModelService = workPlanUpcomingTenderModelService;
    this.forwardWorkPlanTenderSetupService = forwardWorkPlanTenderSetupService;
    this.forwardWorkPlanTenderRoutingService = forwardWorkPlanTenderRoutingService;
    this.forwardWorkPlanTenderCompletionService = forwardWorkPlanTenderCompletionService;
    this.projectSectionItemOwnershipService = projectSectionItemOwnershipService;
  }

  @GetMapping("/setup")
  public ModelAndView getUpcomingTenderSetup(@PathVariable("projectId") Integer projectId,
                                             ProjectContext projectContext,
                                             AuthenticatedUserAccount userAccount) {
    return forwardWorkPlanTenderRoutingService.getUpcomingTenderSetupRoute(projectContext.getProjectDetails());
  }

  @PostMapping("/setup")
  public ModelAndView saveUpcomingTenderSetup(@PathVariable("projectId") Integer projectId,
                                              @Valid @ModelAttribute("form") ForwardWorkPlanTenderSetupForm form,
                                              BindingResult bindingResult,
                                              ProjectContext projectContext,
                                              AuthenticatedUserAccount userAccount) {

    final var projectDetail = projectContext.getProjectDetails();

    bindingResult = forwardWorkPlanTenderSetupService.validate(form, bindingResult, ValidationType.FULL);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        workPlanUpcomingTenderModelService.getUpcomingTenderSetupModelAndView(projectDetail, form),
        form,
        () -> {
          final var forwardWorkPlanTenderSetup = forwardWorkPlanTenderSetupService.saveForwardWorkPlanTenderSetup(
              form,
              projectDetail
          );

          return forwardWorkPlanTenderRoutingService.getPostSaveUpcomingTenderSetupRoute(
              forwardWorkPlanTenderSetup,
              projectDetail
          );
        }
    );
  }

  @GetMapping("/summary")
  public ModelAndView viewUpcomingTenders(@PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext) {
    return forwardWorkPlanTenderRoutingService.getViewUpcomingTendersRoute(projectContext.getProjectDetails());
  }

  @PostMapping("/summary")
  public ModelAndView saveUpcomingTenders(@PathVariable("projectId") Integer projectId,
                                          @Valid @ModelAttribute("form") ForwardWorkPlanTenderCompletionForm form,
                                          BindingResult bindingResult,
                                          ProjectContext projectContext) {

    final var projectDetail = projectContext.getProjectDetails();

    bindingResult = forwardWorkPlanTenderCompletionService.validate(form, bindingResult, ValidationType.FULL);

    var tenderViews = workPlanUpcomingTenderSummaryService.getValidatedSummaryViews(
        projectDetail
    );

    var validationResult = workPlanUpcomingTenderSummaryService.validateViews(tenderViews);

    if (validationResult.equals(ValidationResult.INVALID) || bindingResult.hasErrors()) {
      return workPlanUpcomingTenderModelService.getViewUpcomingTendersModelAndView(
          projectDetail,
          tenderViews,
          validationResult,
          form,
          bindingResult
      );
    } else {
      final var forwardWorkPlanTenderSetup = forwardWorkPlanTenderCompletionService
          .saveForwardWorkPlanTenderCompletionForm(form, projectDetail);

      return forwardWorkPlanTenderRoutingService.getPostSaveUpcomingTendersRoute(
          forwardWorkPlanTenderSetup,
          projectDetail
      );
    }
  }

  @GetMapping("/upcoming-tender")
  public ModelAndView addUpcomingTender(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    return forwardWorkPlanTenderRoutingService.getAddUpcomingTenderRoute(projectContext.getProjectDetails());
  }

  @PostMapping("/upcoming-tender")
  public ModelAndView saveUpcomingTender(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext,
                                         @Valid @ModelAttribute("form") ForwardWorkPlanUpcomingTenderForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType,
                                         AuthenticatedUserAccount userAccount) {
    bindingResult = workPlanUpcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var tender = workPlanUpcomingTenderService.createUpcomingTender(
              projectContext.getProjectDetails(),
              form,
              userAccount
          );

          AuditService.audit(
              AuditEvent.WORK_PLAN_UPCOMING_TENDER_UPDATED,
              String.format(
                  AuditEvent.WORK_PLAN_UPCOMING_TENDER_UPDATED.getMessage(),
                  tender.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }

  @GetMapping("/upcoming-tender/{upcomingTenderId}/edit")
  public ModelAndView editUpcomingTender(@PathVariable("projectId") Integer projectId,
                                         @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                         ProjectContext projectContext) {
    var upcomingTender = workPlanUpcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    return workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(
        projectContext.getProjectDetails(),
        workPlanUpcomingTenderService.getForm(upcomingTender)
    );
  }

  @PostMapping("/upcoming-tender/{upcomingTenderId}/edit")
  public ModelAndView updateUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                           ProjectContext projectContext,
                                           @Valid @ModelAttribute("form") ForwardWorkPlanUpcomingTenderForm form,
                                           BindingResult bindingResult,
                                           ValidationType validationType) {
    var upcomingTender = workPlanUpcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    bindingResult = workPlanUpcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        workPlanUpcomingTenderModelService.getUpcomingTenderFormModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var tender = workPlanUpcomingTenderService.updateUpcomingTender(
              upcomingTender,
              form
          );

          AuditService.audit(
              AuditEvent.WORK_PLAN_UPCOMING_TENDER_UPDATED,
              String.format(
                  AuditEvent.WORK_PLAN_UPCOMING_TENDER_UPDATED.getMessage(),
                  tender.getId(),
                  projectContext.getProjectDetails().getId()
              )
          );

          return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }

  @GetMapping("/upcoming-tender/{upcomingTenderId}/remove/{displayOrder}")
  public ModelAndView removeUpcomingTenderConfirm(@PathVariable("projectId") Integer projectId,
                                                  @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                                  @PathVariable("displayOrder") Integer displayOrder,
                                                  ProjectContext projectContext) {
    var upcomingTender = workPlanUpcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    var tenderView = workPlanUpcomingTenderSummaryService.getUpcomingTenderView(upcomingTender,displayOrder);
    return workPlanUpcomingTenderModelService.getRemoveUpcomingTenderConfirmModelAndView(projectId, tenderView);
  }

  @PostMapping("/upcoming-tender/{upcomingTenderId}/remove/{displayOrder}")
  public ModelAndView removeUpcomingTender(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("upcomingTenderId") Integer upcomingTenderId,
                                           @PathVariable("displayOrder") Integer displayOrder,
                                           ProjectContext projectContext) {
    var upcomingTender = workPlanUpcomingTenderService.getOrError(upcomingTenderId, projectContext.getProjectDetails());
    checkIfUserHasAccessToTender(upcomingTender);
    workPlanUpcomingTenderService.delete(upcomingTender);

    AuditService.audit(
        AuditEvent.WORK_PLAN_UPCOMING_TENDER_REMOVED,
        String.format(
            AuditEvent.WORK_PLAN_UPCOMING_TENDER_REMOVED.getMessage(),
            upcomingTenderId,
            projectContext.getProjectDetails().getId()
        )
    );
    return ReverseRouter.redirect(on(ForwardWorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null));
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
}
