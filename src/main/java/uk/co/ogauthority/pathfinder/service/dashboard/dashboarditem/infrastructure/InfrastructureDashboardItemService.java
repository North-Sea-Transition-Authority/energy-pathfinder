package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure.InfrastructureProjectDashboardItemViewUtil;
import uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.DashboardItemService;

@Service
public class InfrastructureDashboardItemService implements DashboardItemService {

  protected static final String TEMPLATE_PATH = "workarea/infrastructure/infrastructureDashboardItem.ftl";

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public DashboardProjectItemView getDashboardProjectItemView(DashboardProjectItem dashboardProjectItem) {
    return InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);
  }

  @Override
  public String getTemplatePath() {
    return TEMPLATE_PATH;
  }

  @Override
  public Map<String, Object> getTemplateModel(DashboardProjectItem dashboardProjectItem) {
    final var templateModel = new LinkedHashMap<String, Object>();
    templateModel.put("infrastructureDashboardItem", getDashboardProjectItemView(dashboardProjectItem));
    return templateModel;
  }
}
