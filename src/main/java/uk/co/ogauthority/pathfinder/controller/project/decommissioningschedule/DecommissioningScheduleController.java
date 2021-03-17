package uk.co.ogauthority.pathfinder.controller.project.decommissioningschedule;

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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.decommissioningschedule.DecommissioningScheduleService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
@RequestMapping("/project/{projectId}/decommissioning-schedule")
public class DecommissioningScheduleController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Decommissioning schedule";

  private final DecommissioningScheduleService decommissioningScheduleService;

  @Autowired
  public DecommissioningScheduleController(
      BreadcrumbService breadcrumbService,
      ControllerHelperService controllerHelperService,
      DecommissioningScheduleService decommissioningScheduleService) {
    super(breadcrumbService, controllerHelperService);
    this.decommissioningScheduleService = decommissioningScheduleService;
  }

  @GetMapping
  public ModelAndView getDecommissioningSchedule(@PathVariable("projectId") Integer projectId,
                                                 ProjectContext projectContext) {
    return decommissioningScheduleService.getDecommissioningScheduleModelAndView(
        projectId,
        decommissioningScheduleService.getForm(projectContext.getProjectDetails())
    );
  }

  @PostMapping
  public ModelAndView saveDecommissioningSchedule(@PathVariable("projectId") Integer projectId,
                                                  @Valid @ModelAttribute("form") DecommissioningScheduleForm form,
                                                  BindingResult bindingResult,
                                                  ValidationType validationType,
                                                  ProjectContext projectContext) {
    bindingResult = decommissioningScheduleService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        decommissioningScheduleService.getDecommissioningScheduleModelAndView(projectId, form),
        form,
        () -> {
          decommissioningScheduleService.createOrUpdate(projectContext.getProjectDetails(), form);

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        });
  }
}
