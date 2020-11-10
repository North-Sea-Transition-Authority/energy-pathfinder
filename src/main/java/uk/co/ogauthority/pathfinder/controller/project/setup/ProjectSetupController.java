package uk.co.ogauthority.pathfinder.controller.project.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;

@Controller
@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck
@RequestMapping("/project/{projectId}/project-setup")
public class ProjectSetupController {

  private final ProjectSetupService projectSetupService;

  @Autowired
  public ProjectSetupController(ProjectSetupService projectSetupService) {
    this.projectSetupService = projectSetupService;
  }

  @GetMapping
  public ModelAndView getProjectSetup(@PathVariable("projectId") Integer projectId,
                                      ProjectContext projectContext) {
    return null;
  }
}
