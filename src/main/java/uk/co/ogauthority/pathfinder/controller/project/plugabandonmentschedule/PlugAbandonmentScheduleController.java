package uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule.PlugAbandonmentScheduleService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/wells")
public class PlugAbandonmentScheduleController extends ProjectFormPageController {

  public static final String TASK_LIST_NAME = "Wells";
  public static final String SUMMARY_PAGE_NAME = "Wells to be decommissioned";
  public static final String FORM_PAGE_NAME = "Plug and abandonment schedule";

  private final PlugAbandonmentScheduleService plugAbandonmentScheduleService;

  @Autowired
  public PlugAbandonmentScheduleController(BreadcrumbService breadcrumbService,
                                           ControllerHelperService controllerHelperService,
                                           PlugAbandonmentScheduleService plugAbandonmentScheduleService) {
    super(breadcrumbService, controllerHelperService);
    this.plugAbandonmentScheduleService = plugAbandonmentScheduleService;
  }

  @GetMapping
  public ModelAndView viewPlugAbandonmentSchedules(@PathVariable("projectId") Integer projectId,
                                                   ProjectContext projectContext) {
    return plugAbandonmentScheduleService.getPlugAbandonmentScheduleSummaryModelAndView(projectId);
  }

  @GetMapping("/plug-abandonment-schedule")
  public ModelAndView addPlugAbandonmentSchedule(@PathVariable("projectId") Integer projectId,
                                                 ProjectContext projectContext) {
    return plugAbandonmentScheduleService.getPlugAbandonmentScheduleModelAndView(
        projectId,
        new PlugAbandonmentScheduleForm(),
        Collections.emptyList()
    );
  }

  @PostMapping("/plug-abandonment-schedule")
  public ModelAndView createPlugAbandonmentSchedule(@PathVariable("projectId") Integer projectId,
                                                    @Valid @ModelAttribute("form") PlugAbandonmentScheduleForm form,
                                                    BindingResult bindingResult,
                                                    ValidationType validationType,
                                                    ProjectContext projectContext) {
    bindingResult = plugAbandonmentScheduleService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        plugAbandonmentScheduleService.getPlugAbandonmentScheduleModelAndView(projectId, form),
        form,
        () -> {
          plugAbandonmentScheduleService.createPlugAbandonmentSchedule(form, projectContext.getProjectDetails());
          return getPlugAbandonmentSchedulesSummaryRedirect(projectId);
        }
    );
  }

  @GetMapping("/plug-abandonment-schedule/{plugAbandonmentScheduleId}/edit")
  public ModelAndView getPlugAbandonmentSchedule(@PathVariable("projectId") Integer projectId,
                                                 @PathVariable("plugAbandonmentScheduleId") Integer plugAbandonmentScheduleId,
                                                 ProjectContext projectContext) {
    var plugAbandonmentSchedule = plugAbandonmentScheduleService.getPlugAbandonmentScheduleOrError(
        plugAbandonmentScheduleId,
        projectContext.getProjectDetails()
    );
    var form = plugAbandonmentScheduleService.getForm(plugAbandonmentSchedule);
    return plugAbandonmentScheduleService.getPlugAbandonmentScheduleModelAndView(
        projectId,
        form,
        plugAbandonmentSchedule
    );
  }

  @PostMapping("/plug-abandonment-schedule/{plugAbandonmentScheduleId}/edit")
  public ModelAndView updatePlugAbandonmentSchedule(@PathVariable("projectId") Integer projectId,
                                                    @PathVariable("plugAbandonmentScheduleId") Integer plugAbandonmentScheduleId,
                                                    @Valid @ModelAttribute("form") PlugAbandonmentScheduleForm form,
                                                    BindingResult bindingResult,
                                                    ValidationType validationType,
                                                    ProjectContext projectContext) {
    bindingResult = plugAbandonmentScheduleService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        plugAbandonmentScheduleService.getPlugAbandonmentScheduleModelAndView(projectId, form),
        form,
        () -> {
          plugAbandonmentScheduleService.updatePlugAbandonmentSchedule(
              plugAbandonmentScheduleId,
              projectContext.getProjectDetails(),
              form
          );
          return getPlugAbandonmentSchedulesSummaryRedirect(projectId);
        }
    );
  }

  private ModelAndView getPlugAbandonmentSchedulesSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(
        projectId,
        null
    ));
  }
}
