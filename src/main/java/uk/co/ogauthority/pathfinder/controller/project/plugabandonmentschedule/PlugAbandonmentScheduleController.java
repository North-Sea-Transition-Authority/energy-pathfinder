package uk.co.ogauthority.pathfinder.controller.project.plugabandonmentschedule;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.plugabandonmentschedule.PlugAbandonmentSchedule;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule.PlugAbandonmentScheduleForm;
import uk.co.ogauthority.pathfinder.model.view.wellbore.WellboreView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule.PlugAbandonmentScheduleService;
import uk.co.ogauthority.pathfinder.service.project.plugabandonmentschedule.PlugAbandonmentWellService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/wells")
public class PlugAbandonmentScheduleController extends ProjectFormPageController {

  public static final String TASK_LIST_NAME = "Wells";
  public static final String SUMMARY_PAGE_NAME = "Wells to be decommissioned";
  public static final String FORM_PAGE_NAME = "Plug abandonment schedule";

  private final PlugAbandonmentScheduleService plugAbandonmentScheduleService;
  private final PlugAbandonmentWellService plugAbandonmentWellService;

  @Autowired
  public PlugAbandonmentScheduleController(BreadcrumbService breadcrumbService,
                                           ControllerHelperService controllerHelperService,
                                           PlugAbandonmentScheduleService plugAbandonmentScheduleService,
                                           PlugAbandonmentWellService plugAbandonmentWellService) {
    super(breadcrumbService, controllerHelperService);
    this.plugAbandonmentScheduleService = plugAbandonmentScheduleService;
    this.plugAbandonmentWellService = plugAbandonmentWellService;
  }

  @GetMapping
  public ModelAndView viewPlugAbandonmentSchedules(@PathVariable("projectId") Integer projectId,
                                                   ProjectContext projectContext) {
    return getPlugAbandonmentScheduleSummaryModelAndView(projectId);
  }

  @GetMapping("/plug-abandonment-schedule")
  public ModelAndView addPlugAbandonmentSchedule(@PathVariable("projectId") Integer projectId,
                                                 ProjectContext projectContext) {
    return getPlugAbandonmentScheduleModelAndView(projectId, new PlugAbandonmentScheduleForm(), Collections.emptyList());
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
        getPlugAbandonmentScheduleModelAndView(projectId, form),
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
    return getPlugAbandonmentScheduleModelAndView(projectId, form, plugAbandonmentSchedule);
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
        getPlugAbandonmentScheduleModelAndView(projectId, form),
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

  private ModelAndView getPlugAbandonmentScheduleSummaryModelAndView(Integer projectId) {
    var modelAndView = new ModelAndView("project/plugabandonmentschedule/plugAbandonmentScheduleSummary")
        .addObject("pageName", SUMMARY_PAGE_NAME)
        .addObject("addPlugAbandonmentScheduleUrl",
            ReverseRouter.route(on(PlugAbandonmentScheduleController.class).addPlugAbandonmentSchedule(projectId, null))
        )
        .addObject("projectSetupUrl", ControllerUtils.getProjectSetupUrl(projectId));

    breadcrumbService.fromTaskList(projectId, modelAndView, TASK_LIST_NAME);

    return modelAndView;
  }


  private ModelAndView getPlugAbandonmentScheduleModelAndView(Integer projectId,
                                                              PlugAbandonmentScheduleForm form) {
    return getPlugAbandonmentScheduleModelAndView(
        projectId,
        form,
        plugAbandonmentWellService.getWellboreViewsFromForm(form)
    );
  }

  private ModelAndView getPlugAbandonmentScheduleModelAndView(Integer projectId,
                                                              PlugAbandonmentScheduleForm form,
                                                              PlugAbandonmentSchedule plugAbandonmentSchedule) {
    return getPlugAbandonmentScheduleModelAndView(
        projectId,
        form,
        plugAbandonmentWellService.getWellboreViews(plugAbandonmentSchedule)
    );
  }

  private ModelAndView getPlugAbandonmentScheduleModelAndView(Integer projectId,
                                                              PlugAbandonmentScheduleForm form,
                                                              List<WellboreView> wellboreViews) {
    var modelAndView = new ModelAndView("project/plugabandonmentschedule/plugAbandonmentSchedule")
        .addObject("form", form)
        .addObject("pageName", FORM_PAGE_NAME)
        .addObject("alreadyAddedWells", wellboreViews)
        .addObject("wellsRestUrl", plugAbandonmentScheduleService.getWellboreRestUrl());

    breadcrumbService.fromPlugAbandonmentSchedule(projectId, modelAndView, FORM_PAGE_NAME);

    return modelAndView;
  }

  private ModelAndView getPlugAbandonmentSchedulesSummaryRedirect(Integer projectId) {
    return ReverseRouter.redirect(on(PlugAbandonmentScheduleController.class).viewPlugAbandonmentSchedules(
        projectId,
        null
    ));
  }
}
