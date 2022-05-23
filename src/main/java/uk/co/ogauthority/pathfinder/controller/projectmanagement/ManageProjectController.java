package uk.co.ogauthority.pathfinder.controller.projectmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.projectmanagement.ProjectManagementForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectmanagement.ProjectManagementViewService;

@Controller
@ProjectStatusCheck(
    status = { ProjectStatus.QA, ProjectStatus.PUBLISHED, ProjectStatus.ARCHIVED },
    projectDetailVersionType = ProjectDetailVersionType.LATEST_SUBMITTED_VERSION
)
@ProjectFormPagePermissionCheck(permissions = ProjectPermission.VIEW)
@ProjectTypeCheck(types = { ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN })
@AllowProjectContributorAccess
@RequestMapping("/project/{projectId}/manage")
public class ManageProjectController {

  private final ProjectManagementViewService projectManagementViewService;

  @Autowired
  public ManageProjectController(ProjectManagementViewService projectManagementViewService) {
    this.projectManagementViewService = projectManagementViewService;
  }

  @GetMapping
  public ModelAndView getProject(@PathVariable Integer projectId,
                                 @RequestParam(required = false) Integer version,
                                 ProjectContext projectContext,
                                 AuthenticatedUserAccount user) {
    return projectManagementViewService.getProjectManagementModelAndView(
        projectContext.getProjectDetails(),
        version,
        user
    );
  }

  @PostMapping
  public ModelAndView updateProjectVersion(@PathVariable Integer projectId,
                                           ProjectManagementForm form,
                                           ProjectContext projectContext,
                                           AuthenticatedUserAccount user) {
    return ReverseRouter.redirect(on(ManageProjectController.class).getProject(projectId, form.getVersion(), null, null));
  }
}
