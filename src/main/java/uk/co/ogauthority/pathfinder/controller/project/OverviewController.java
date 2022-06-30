package uk.co.ogauthority.pathfinder.controller.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.controller.project.annotation.AllowProjectContributorAccess;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.overview.OverviewService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContext;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;

@ProjectStatusCheck(status = ProjectStatus.DRAFT)
@ProjectFormPagePermissionCheck(permissions = ProjectPermission.EDIT)
@ProjectTypeCheck(types = {ProjectType.INFRASTRUCTURE, ProjectType.FORWARD_WORK_PLAN})
@AllowProjectContributorAccess
@Controller
public class OverviewController {

  public static final String PAGE_NAME = "Overview";

  private final OverviewService overviewService;

  @Autowired
  public OverviewController(OverviewService overviewService) {
    this.overviewService = overviewService;
  }

  @GetMapping("/project/{projectId}/overview")
  public ModelAndView getOverview(@PathVariable("projectId") Integer projectId,
                                  ProjectContext projectContext) {
    return overviewService.getModelAndView(projectId, projectContext.getProjectDetails());
  }
}
