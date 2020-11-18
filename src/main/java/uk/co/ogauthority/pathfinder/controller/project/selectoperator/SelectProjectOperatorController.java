package uk.co.ogauthority.pathfinder.controller.project.selectoperator;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.model.enums.TopNavigationType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;

/**
 * A controller for users who are in multiple teams to use to select which team a project is for prior to creation.
 */
@Controller
@RequestMapping("/project-operator-select")
public class SelectProjectOperatorController {

  public static final String PAGE_NAME = "Project operator";
  public static final String PRIMARY_BUTTON_TEXT = "Save and continue";

  private final StartProjectService startProjectService;
  private final SelectOperatorService selectOperatorService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public SelectProjectOperatorController(
      StartProjectService startProjectService,
      SelectOperatorService selectOperatorService,
      ControllerHelperService controllerHelperService) {
    this.startProjectService = startProjectService;
    this.selectOperatorService = selectOperatorService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView selectOperator(AuthenticatedUserAccount user) {
    return getSelectOperatorModelAndView(new ProjectOperatorForm());
  }

  @PostMapping
  public ModelAndView startProject(AuthenticatedUserAccount user,
                                   @ModelAttribute("form") ProjectOperatorForm form,
                                   BindingResult bindingResult) {
    bindingResult = selectOperatorService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSelectOperatorModelAndView(form),
        form,
        () -> {
          var projectDetail = startProjectService.startProject(
              user,
              selectOperatorService.getOrganisationGroupOrError(user, Integer.valueOf(form.getOrganisationGroup()))
          );

          return ReverseRouter.redirect(
              on(TaskListController.class).viewTaskList(projectDetail.getProject().getId(), null)
          );
        });
  }

  private ModelAndView getSelectOperatorModelAndView(ProjectOperatorForm form) {
    return selectOperatorService.getSelectOperatorModelAndView(
        form,
        ReverseRouter.route(on(StartProjectController.class).startPage(null)),
        PRIMARY_BUTTON_TEXT,
        TopNavigationType.BACKLINK
    );
  }
}
