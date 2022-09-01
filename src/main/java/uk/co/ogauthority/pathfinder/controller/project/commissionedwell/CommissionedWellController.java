package uk.co.ogauthority.pathfinder.controller.project.commissionedwell;

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
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.commissionedwell.CommissionedWellSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellModelService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleSummaryService;
import uk.co.ogauthority.pathfinder.service.project.commissionedwell.CommissionedWellScheduleValidationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/commissioning/wells")
public class CommissionedWellController  {

  public static final String TASK_LIST_NAME = "Wells";
  public static final String SUMMARY_PAGE_NAME = "Wells to be commissioned";
  public static final String FORM_PAGE_NAME = "Well commissioning schedule";
  public static final String REMOVE_PAGE_NAME = "Remove well commissioning schedule";

  private final CommissionedWellModelService commissionedWellModelService;
  private final CommissionedWellScheduleService commissionedWellScheduleService;
  private final CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;
  private final CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public CommissionedWellController(
      CommissionedWellModelService commissionedWellModelService,
      CommissionedWellScheduleService commissionedWellScheduleService,
      CommissionedWellScheduleValidationService commissionedWellScheduleValidationService,
      CommissionedWellScheduleSummaryService commissionedWellScheduleSummaryService,
      ControllerHelperService controllerHelperService
  ) {
    this.commissionedWellModelService = commissionedWellModelService;
    this.commissionedWellScheduleService = commissionedWellScheduleService;
    this.commissionedWellScheduleValidationService = commissionedWellScheduleValidationService;
    this.commissionedWellScheduleSummaryService = commissionedWellScheduleSummaryService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView viewWellsToCommission(@PathVariable("projectId") Integer projectId,
                                            ProjectContext projectContext) {

    var projectDetail = projectContext.getProjectDetails();
    return commissionedWellModelService.getViewCommissionedWellsModelAndView(
        projectDetail,
        commissionedWellScheduleSummaryService.getCommissionedWellScheduleViews(projectDetail),
        ValidationResult.NOT_VALIDATED
    );
  }

  @PostMapping
  public ModelAndView completeWellsToCommission(@PathVariable("projectId") Integer projectId,
                                                ProjectContext projectContext) {

    var projectDetail = projectContext.getProjectDetails();

    var validatedCommissionedWellScheduleViews = commissionedWellScheduleSummaryService.getValidatedCommissionedWellScheduleViews(
        projectDetail
    );

    var validationResult = commissionedWellScheduleSummaryService.determineViewValidationResult(validatedCommissionedWellScheduleViews);

    return validationResult.equals(ValidationResult.VALID)
        ? ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null))
        : commissionedWellModelService.getViewCommissionedWellsModelAndView(
            projectDetail,
            validatedCommissionedWellScheduleViews,
            validationResult
        );
  }

  @GetMapping("/commissioning-schedule")
  public ModelAndView addCommissioningSchedule(@PathVariable("projectId") Integer projectId,
                                               ProjectContext projectContext) {
    return commissionedWellModelService.getCommissionedWellModelAndView(
        projectContext.getProjectDetails(),
        new CommissionedWellForm()
    );
  }

  @PostMapping("/commissioning-schedule")
  public ModelAndView createCommissioningSchedule(@PathVariable("projectId") Integer projectId,
                                                  @Valid @ModelAttribute("form") CommissionedWellForm form,
                                                  BindingResult bindingResult,
                                                  ValidationType validationType,
                                                  ProjectContext projectContext) {

    bindingResult = commissionedWellScheduleValidationService.validate(form, bindingResult, validationType);

    var projectDetail = projectContext.getProjectDetails();

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        commissionedWellModelService.getCommissionedWellModelAndView(
            projectDetail,
            form
        ),
        form,
        () -> {

          commissionedWellScheduleService.createCommissionWellSchedule(form, projectDetail);

          return getCommissionedWellScheduleSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/commissioning-schedule/{commissionedWellScheduleId}/edit")
  public ModelAndView getCommissionedWellSchedule(@PathVariable("projectId") Integer projectId,
                                                  @PathVariable("commissionedWellScheduleId") Integer commissionedWellScheduleId,
                                                  ProjectContext projectContext) {

    var projectDetail = projectContext.getProjectDetails();

    var commissionedWellSchedule = getCommissionedWellScheduleOrError(commissionedWellScheduleId);

    var commissionedWellsForSchedule = commissionedWellScheduleService.getCommissionedWellsForSchedule(commissionedWellSchedule);

    var commissionedWellScheduleForm = commissionedWellScheduleService.getForm(
        commissionedWellSchedule,
        commissionedWellsForSchedule
    );

    return commissionedWellModelService.getCommissionedWellModelAndView(
        projectDetail,
        commissionedWellScheduleForm
    );
  }

  @PostMapping("/commissioning-schedule/{commissionedWellScheduleId}/edit")
  public ModelAndView updateCommissionedWellSchedule(@PathVariable("projectId") Integer projectId,
                                                     @PathVariable("commissionedWellScheduleId") Integer commissionedWellScheduleId,
                                                     @Valid @ModelAttribute("form") CommissionedWellForm commissionedWellForm,
                                                     BindingResult bindingResult,
                                                     ValidationType validationType,
                                                     ProjectContext projectContext) {
    var commissionedWellSchedule = getCommissionedWellScheduleOrError(commissionedWellScheduleId);

    bindingResult = commissionedWellScheduleValidationService.validate(commissionedWellForm, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        commissionedWellModelService.getCommissionedWellModelAndView(projectContext.getProjectDetails(), commissionedWellForm),
        commissionedWellForm,
        () -> {

          commissionedWellScheduleService.updateCommissionedWellSchedule(commissionedWellSchedule, commissionedWellForm);

          var auditEvent = AuditEvent.COMMISSIONED_WELL_SCHEDULE_UPDATED;

          AuditService.audit(
              auditEvent,
              String.format(
                  auditEvent.getMessage(),
                  commissionedWellScheduleId,
                  projectContext.getProjectDetails().getId()
              )
          );

          return getCommissionedWellScheduleSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/commissioning-schedule/{commissionedWellScheduleId}/remove/{displayOrder}")
  public ModelAndView removeCommissionedWellScheduleConfirmation(
      @PathVariable("projectId") Integer projectId,
      @PathVariable("commissionedWellScheduleId") Integer commissionedWellScheduleId,
      @PathVariable("displayOrder") Integer displayOrder,
      ProjectContext projectContext
  ) {
    var commissionedWellScheduleView = commissionedWellScheduleSummaryService.getCommissionedWellScheduleView(
        getCommissionedWellScheduleOrError(commissionedWellScheduleId),
        displayOrder
    );

    return commissionedWellModelService.getRemoveCommissionedWellScheduleModelAndView(
        projectId,
        commissionedWellScheduleView
    );
  }

  @PostMapping("/commissioning-schedule/{commissionedWellScheduleId}/remove/{displayOrder}")
  public ModelAndView removeCommissionedWellSchedule(
      @PathVariable("projectId") Integer projectId,
      @PathVariable("commissionedWellScheduleId") Integer commissionedWellScheduleId,
      @PathVariable("displayOrder") Integer displayOrder,
      ProjectContext projectContext
  ) {
    var commissionedWellSchedule = getCommissionedWellScheduleOrError(commissionedWellScheduleId);
    commissionedWellScheduleService.deleteCommissionedWellSchedule(commissionedWellSchedule);

    var auditEvent = AuditEvent.COMMISSIONED_WELL_SCHEDULE_REMOVED;

    AuditService.audit(
        auditEvent,
        String.format(
            auditEvent.getMessage(),
            commissionedWellScheduleId,
            projectContext.getProjectDetails().getId()
        )
    );

    return getCommissionedWellScheduleSummaryRedirect(projectId);
  }

  private CommissionedWellSchedule getCommissionedWellScheduleOrError(int commissionedWellScheduleId) {
    return commissionedWellScheduleService.getCommissionedWellSchedule(commissionedWellScheduleId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Could not find CommissionedWellSchedule with ID %d", commissionedWellScheduleId)
        ));
  }

  private ModelAndView getCommissionedWellScheduleSummaryRedirect(int projectId) {
    return ReverseRouter.redirect(on(CommissionedWellController.class).viewWellsToCommission(projectId, null));
  }
}
