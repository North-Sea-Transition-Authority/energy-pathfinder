package uk.co.ogauthority.pathfinder.controller.projectupdate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContext;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;

@Controller
@ProjectStatusCheck(status = {ProjectStatus.QA, ProjectStatus.PUBLISHED})
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.PROVIDE_UPDATE})
@RequestMapping("/project/{projectId}/project-update")
public class OperatorUpdateController {

  private final OperatorProjectUpdateService operatorProjectUpdateService;
  private final ProjectUpdateService projectUpdateService;

  @Autowired
  public OperatorUpdateController(
      OperatorProjectUpdateService operatorProjectUpdateService,
      ProjectUpdateService projectUpdateService) {
    this.operatorProjectUpdateService = operatorProjectUpdateService;
    this.projectUpdateService = projectUpdateService;
  }

  @GetMapping
  public ModelAndView startPage(@PathVariable("projectId") Integer projectId,
                                ProjectUpdateContext projectUpdateContext) {
    return operatorProjectUpdateService.getProjectUpdateModelAndView(projectId);
  }

  @PostMapping
  public ModelAndView startUpdate(@PathVariable("projectId") Integer projectId,
                                  ProjectUpdateContext projectUpdateContext,
                                  AuthenticatedUserAccount user) {
    projectUpdateService.startUpdate(projectUpdateContext.getProjectDetails(), user, ProjectUpdateType.OPERATOR_INITIATED);
    return ReverseRouter.redirect(on(TaskListController.class).viewTaskList(projectId, null));
  }
}
