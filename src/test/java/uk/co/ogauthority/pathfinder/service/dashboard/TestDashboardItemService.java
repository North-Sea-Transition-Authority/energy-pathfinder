package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.DashboardItemService;

public class TestDashboardItemService implements DashboardItemService {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public DashboardProjectItemView getDashboardProjectItemView(DashboardProjectItem dashboardProjectItem) {
    return null;
  }

  @Override
  public String getTemplatePath() {
    return null;
  }

  @Override
  public Map<String, Object> getTemplateModel(DashboardProjectItem dashboardProjectItem) {
    return null;
  }
}
