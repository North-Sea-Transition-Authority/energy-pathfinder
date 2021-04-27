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
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender.WorkPlanUpcomingTenderForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.audit.AuditService;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.WorkPlanUpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.project.workplanupcomingtender.WorkPlanUpcomingTenderSummaryService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.FORWARD_WORK_PLAN)
@RequestMapping("/project/{projectId}/work-plan-upcoming-tenders")
public class WorkPlanUpcomingTenderController {

  public static final String PAGE_NAME = "Upcoming tenders";
  public static final String PAGE_NAME_SINGULAR = "Upcoming tender";

  private final WorkPlanUpcomingTenderService workPlanUpcomingTenderService;
  private final WorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public WorkPlanUpcomingTenderController(
      WorkPlanUpcomingTenderService workPlanUpcomingTenderService,
      WorkPlanUpcomingTenderSummaryService workPlanUpcomingTenderSummaryService,
      ControllerHelperService controllerHelperService) {

    this.workPlanUpcomingTenderService = workPlanUpcomingTenderService;
    this.workPlanUpcomingTenderSummaryService = workPlanUpcomingTenderSummaryService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView viewUpcomingTenders(@PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext) {
    return workPlanUpcomingTenderService.getUpcomingTendersModelAndView(
        projectId,
        workPlanUpcomingTenderSummaryService.getSummaryViews(projectContext.getProjectDetails()));
  }

  @GetMapping("/upcoming-tender")
  public ModelAndView addUpcomingTender(@PathVariable("projectId") Integer projectId,
                                        ProjectContext projectContext) {
    return workPlanUpcomingTenderService.getUpcomingTenderFormModelAndView(
        projectContext.getProjectDetails(),
        new WorkPlanUpcomingTenderForm()
    );
  }

  @PostMapping("/upcoming-tender")
  public ModelAndView saveUpcomingTender(@PathVariable("projectId") Integer projectId,
                                         ProjectContext projectContext,
                                         @Valid @ModelAttribute("form") WorkPlanUpcomingTenderForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    bindingResult = workPlanUpcomingTenderService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        workPlanUpcomingTenderService.getUpcomingTenderFormModelAndView(projectContext.getProjectDetails(), form),
        form,
        () -> {
          var tender = workPlanUpcomingTenderService.createUpcomingTender(
              projectContext.getProjectDetails(),
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

          return ReverseRouter.redirect(on(WorkPlanUpcomingTenderController.class).viewUpcomingTenders(projectId, null));
        }
    );
  }
}
