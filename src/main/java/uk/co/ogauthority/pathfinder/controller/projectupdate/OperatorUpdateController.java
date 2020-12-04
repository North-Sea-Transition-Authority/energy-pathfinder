package uk.co.ogauthority.pathfinder.controller.projectupdate;

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
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;

@Controller
@ProjectStatusCheck(status = {ProjectStatus.QA, ProjectStatus.PUBLISHED})
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.PROVIDE_UPDATE})
@RequestMapping("/project/{projectId}")
public class OperatorUpdateController {

  public static final String NO_UPDATE_REQUIRED_PAGE_NAME = "No changes required";

  private final OperatorProjectUpdateService operatorProjectUpdateService;
  private final ProjectUpdateService projectUpdateService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public OperatorUpdateController(OperatorProjectUpdateService operatorProjectUpdateService,
                                  ProjectUpdateService projectUpdateService,
                                  ControllerHelperService controllerHelperService) {
    this.operatorProjectUpdateService = operatorProjectUpdateService;
    this.projectUpdateService = projectUpdateService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping("/start-update")
  public ModelAndView startPage(@PathVariable("projectId") Integer projectId,
                                ProjectUpdateContext projectUpdateContext) {
    return operatorProjectUpdateService.getProjectUpdateModelAndView(projectId);
  }

  @PostMapping("/start-update")
  public ModelAndView startUpdate(@PathVariable("projectId") Integer projectId,
                                  ProjectUpdateContext projectUpdateContext,
                                  AuthenticatedUserAccount user) {
    projectUpdateService.startUpdate(projectUpdateContext.getProjectDetails(), user, ProjectUpdateType.OPERATOR_INITIATED);
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }

  @GetMapping("/no-update-required")
  public ModelAndView provideNoUpdateConfirmation(@PathVariable("projectId") Integer projectId,
                                                  ProjectUpdateContext projectUpdateContext) {
    return operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(projectId, new ProvideNoUpdateForm());
  }

  @PostMapping("/no-update-required")
  public ModelAndView provideNoUpdate(@PathVariable("projectId") Integer projectId,
                                      @Valid @ModelAttribute("form") ProvideNoUpdateForm form,
                                      BindingResult bindingResult,
                                      ProjectUpdateContext projectUpdateContext,
                                      AuthenticatedUserAccount user) {
    bindingResult = operatorProjectUpdateService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(projectId, form),
        form,
        () -> {
          projectUpdateService.createNoUpdateNotification(projectUpdateContext.getProjectDetails(), user, form.getReasonNoUpdateRequired());
          return ReverseRouter.redirect(on(ManageProjectController.class).getProject(projectId, null, null, null));
        }
    );
  }
}
