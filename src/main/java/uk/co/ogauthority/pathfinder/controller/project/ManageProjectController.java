package uk.co.ogauthority.pathfinder.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.management.ProjectManagementViewService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@Controller
@ProjectStatusCheck(status = {ProjectStatus.QA, ProjectStatus.PUBLISHED})
@ProjectFormPagePermissionCheck(permissions = {ProjectPermission.VIEW})
@RequestMapping("/project/{projectId}/manage")
public class ManageProjectController {

  private final ProjectManagementViewService projectManagementViewService;

  @Autowired
  public ManageProjectController(ProjectManagementViewService projectManagementViewService) {
    this.projectManagementViewService = projectManagementViewService;
  }

  @GetMapping
  public ModelAndView getProject(@PathVariable Integer projectId,
                                 ProjectContext projectContext,
                                 AuthenticatedUserAccount user) {
    return projectManagementViewService.getProjectManagementModelAndView(
        projectContext.getProjectDetails(),
        user
    );
  }
}
