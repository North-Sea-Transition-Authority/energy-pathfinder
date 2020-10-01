package uk.co.ogauthority.pathfinder.controller.project.selectoperator;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.ogauthority.pathfinder.controller.project.ProjectFormPageController;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.TopNavigationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.navigation.BreadcrumbService;
import uk.co.ogauthority.pathfinder.service.project.SelectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/operator")
public class ChangeProjectOperatorController extends ProjectFormPageController {

  public static final String PAGE_NAME = "Project operator";
  public static final String PRIMARY_BUTTON_TEXT = "Save and continue";

  private final SelectOperatorService selectOperatorService;

  @Autowired
  public ChangeProjectOperatorController(
      SelectOperatorService selectOperatorService,
      ControllerHelperService controllerHelperService,
      BreadcrumbService breadcrumbService) {
    super(breadcrumbService, controllerHelperService);
    this.selectOperatorService = selectOperatorService;
  }


  @GetMapping
  public ModelAndView changeOperator(AuthenticatedUserAccount user,
                                     @PathVariable("projectId") Integer projectId,
                                     ProjectContext projectContext
  ) {
    return getSelectOperatorModelAndView(
        selectOperatorService.getForm(projectContext.getProjectDetails()),
        projectId
    );
  }


  @PostMapping
  public ModelAndView saveProjectOperator(AuthenticatedUserAccount user,
                                          @PathVariable("projectId") Integer projectId,
                                          ProjectContext projectContext,
                                          @ModelAttribute("form") ProjectOperatorForm form,
                                          BindingResult bindingResult) {
    bindingResult = selectOperatorService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSelectOperatorModelAndView(form, projectId),
        form,
        () -> {
          var portalOrganisationGroup = selectOperatorService.getOrganisationGroupOrError(
              user,
              Integer.parseInt(form.getOrganisationGroup())
          );
          selectOperatorService.updateProjectOperator(projectContext.getProjectDetails(), portalOrganisationGroup);

          return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
        });
  }

  private ModelAndView getSelectOperatorModelAndView(ProjectOperatorForm form, Integer projectId) {
    var modelAndView =  selectOperatorService.getSelectOperatorModelAndView(
        form,
        ReverseRouter.route(on(TaskListController.class).viewTaskList(projectId, null)),
        PRIMARY_BUTTON_TEXT,
        TopNavigationType.BREADCRUMBS
    );
    breadcrumbService.fromTaskList(projectId, modelAndView, PAGE_NAME);
    return modelAndView;
  }
}
