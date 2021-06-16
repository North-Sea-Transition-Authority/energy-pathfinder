package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.forwardworkplan;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.model.view.dashboard.infrastructure.InfrastructureProjectDashboardItemViewUtil;
import uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.DashboardItemService;

@Service
public class ForwardWorkPlanDashboardItemService implements DashboardItemService {

  //TODO PAT-466 make this reference forward work plan dashboard item template once it has been built
  protected static final String TEMPLATE_PATH = "workarea/infrastructure/infrastructureDashboardItem.ftl";

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public DashboardProjectItemView getDashboardProjectItemView(DashboardProjectItem dashboardProjectItem) {
    //TODO PAT-466 make this reference forward work plan dashboard item util once it has been built
    return InfrastructureProjectDashboardItemViewUtil.from(dashboardProjectItem);
  }

  @Override
  public String getTemplatePath() {
    return TEMPLATE_PATH;
  }

  @Override
  public Map<String, Object> getTemplateModel(DashboardProjectItem dashboardProjectItem) {
    //TODO PAT-466 make this reference forward work plan dashboard model attr once it has been built
    final var templateModel = new LinkedHashMap<String, Object>();
    templateModel.put("infrastructureDashboardItem", getDashboardProjectItemView(dashboardProjectItem));
    return templateModel;
  }
}
