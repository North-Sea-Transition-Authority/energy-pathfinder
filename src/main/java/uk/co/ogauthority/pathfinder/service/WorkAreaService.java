package uk.co.ogauthority.pathfinder.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.dashboard.DashboardService;

@Service
public class WorkAreaService {

  public static final String WORK_AREA_TEMPLATE_PATH = "workarea/workArea";
  public static final String LINK_BUTTON_TEXT = "Create project";

  private final DashboardService dashboardService;
  private final MetricsProvider metricsProvider;

  @Autowired
  public WorkAreaService(DashboardService dashboardService, MetricsProvider metricsProvider) {
    this.dashboardService = dashboardService;
    this.metricsProvider = metricsProvider;
  }

  public ModelAndView getWorkAreaModelAndViewForUser(AuthenticatedUserAccount user, DashboardFilter filter, DashboardFilterForm form) {
    var dashboardStopwatch = Stopwatch.createStarted();
    var filterType = dashboardService.getDashboardFilterType(user);
    var dashboardProjectHtmlItems =  dashboardService.getDashboardProjectHtmlItemsForUser(user, filterType, filter);

    var elapsedMs = dashboardStopwatch.elapsed(TimeUnit.MILLISECONDS);
    metricsProvider.getDashboardTimer().record(elapsedMs, TimeUnit.MILLISECONDS);

    return new ModelAndView(WORK_AREA_TEMPLATE_PATH)
        .addObject("startProjectButton", getStartProjectLinkButton(user))
        .addObject("filterType", filterType)
        .addObject("clearFilterUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkAreaClearFilter(null, null)))
        .addObject("form", form)
        .addObject("statuses", ProjectStatus.getAllAsMap())
        .addObject("fieldStages", FieldStage.getAllAsMap())
        .addObject("ukcsAreas", UkcsArea.getAllAsMap())
        .addObject("dashboardProjectHtmlItems", dashboardProjectHtmlItems)
        .addObject("resultSize", dashboardProjectHtmlItems.size());
  }

  public LinkButton getStartProjectLinkButton(AuthenticatedUserAccount user) {
    return new LinkButton(
        LINK_BUTTON_TEXT,
        ReverseRouter.route(on(StartProjectController.class).startProject(null)),
        user.getUserPrivileges().contains(UserPrivilege.PATHFINDER_PROJECT_CREATE),
        ButtonType.PRIMARY
    );
  }

  public DashboardFilter getDefaultFilterForUser(AuthenticatedUserAccount user) {
    return dashboardService.getDefaultFilterForUser(user);
  }

}
