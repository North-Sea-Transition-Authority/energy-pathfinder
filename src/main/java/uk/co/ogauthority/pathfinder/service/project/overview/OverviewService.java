package uk.co.ogauthority.pathfinder.service.project.overview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@Service
public class OverviewService {

  private final ProjectSummaryViewService projectSummaryViewService;

  @Autowired
  public OverviewService(
      ProjectSummaryViewService projectSummaryViewService) {
    this.projectSummaryViewService = projectSummaryViewService;
  }

  public ModelAndView getModelAndView(int projectId, ProjectDetail projectDetail) {
    return new ModelAndView("project/overview/projectOverview")
        .addObject(
            "projectSummaryView",
            projectSummaryViewService.getProjectSummaryView(projectDetail)
        )
        .addObject("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId));
  }
}
