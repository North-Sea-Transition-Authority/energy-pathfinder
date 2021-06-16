package uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.model.view.dashboard.forwardworkplan.ForwardWorkPlanDashboardItemViewUtil;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.dashboard.dashboarditem.DashboardItemService;

@Service
public class ForwardWorkPlanDashboardItemService implements DashboardItemService {

  protected static final String TEMPLATE_PATH = "workarea/forwardworkplan/forwardWorkPlanDashboardItem.ftl";

  private final ServiceProperties serviceProperties;

  @Autowired
  public ForwardWorkPlanDashboardItemService(ServiceProperties serviceProperties) {
    this.serviceProperties = serviceProperties;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public DashboardProjectItemView getDashboardProjectItemView(DashboardProjectItem dashboardProjectItem) {
    return ForwardWorkPlanDashboardItemViewUtil.from(dashboardProjectItem);
  }

  @Override
  public String getTemplatePath() {
    return TEMPLATE_PATH;
  }

  @Override
  public Map<String, Object> getTemplateModel(DashboardProjectItem dashboardProjectItem) {

    final var templateModel = new LinkedHashMap<String, Object>();
    templateModel.put("forwardWorkPlanDashboardItem", getDashboardProjectItemView(dashboardProjectItem));
    templateModel.put("service", serviceProperties);
    templateModel.put(
        "infrastructureProjectLowerCaseDisplayName",
        ProjectType.INFRASTRUCTURE.getLowercaseDisplayName()
    );
    templateModel.put(
        "forwardWorkPlanProjectLowerCaseDisplayName",
        ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()
    );
    templateModel.put(
        "startInfrastructureProjectUrl",
        ReverseRouter.route(on(StartProjectController.class).startPage(null))
    );

    return templateModel;
  }
}
