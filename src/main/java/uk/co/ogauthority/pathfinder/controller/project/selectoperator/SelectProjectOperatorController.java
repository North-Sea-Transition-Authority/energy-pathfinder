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
import uk.co.ogauthority.pathfinder.controller.rest.OrganisationGroupRestController;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.SelectOperatorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

/**
 * A controller for users who are in multiple teams to use to select which team a project is for prior to creation.
 */
@Controller
@RequestMapping("/project-operator-select")
public class SelectProjectOperatorController {
  private static final String PRIMARY_BUTTON_TEXT = "Start project";
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
    return getSelectOperatorModelAndView(new SelectOperatorForm());
  }

  @PostMapping
  public ModelAndView startProject(AuthenticatedUserAccount user,
                                   @ModelAttribute("form") SelectOperatorForm form,
                                   BindingResult bindingResult) {
    bindingResult = selectOperatorService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSelectOperatorModelAndView(form),
        form,
        () -> {
          var projectDetail = startProjectService.startProject(
              user,
              selectOperatorService.getOrganisationGroupOrError(Integer.valueOf(form.getOrganisationGroup()))
          );

          return ReverseRouter.redirect(
              on(TaskListController.class).viewTaskList(projectDetail.getProject().getId(), null)
          );
        });
  }

  //TODO can this go in a util to be shared with the other controller?? Take cancel url, button text and the form as params?
  private ModelAndView getSelectOperatorModelAndView(SelectOperatorForm form) {
    return new ModelAndView("project/selectoperator/selectOperator")
        .addObject("form", form)
        .addObject("preselectedOperator", selectOperatorService.getPreSelectedOrgGroup(form))
        .addObject("primaryButtonText", PRIMARY_BUTTON_TEXT)
        .addObject("cancelUrl", ReverseRouter.route(on(StartProjectController.class).startPage(null)))
        .addObject("operatorsRestUrl", SearchSelectorService.route(on(OrganisationGroupRestController.class)
            .searchFields(null, null))
        );
  }
}
