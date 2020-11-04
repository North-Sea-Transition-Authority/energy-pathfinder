package uk.co.ogauthority.pathfinder.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.dashboard.DashboardService;

@Service
public class WorkAreaService {

  public static final String WORK_AREA_TEMPLATE_PATH = "workarea/workArea";

  private final DashboardService dashboardService;

  @Autowired
  public WorkAreaService(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  public ModelAndView getWorkAreaModelAndViewForUser(AuthenticatedUserAccount user) {
    var dashboardProjectItemViews =  dashboardService.getDashboardProjectItemViewsForUser(user);
    return new ModelAndView(WORK_AREA_TEMPLATE_PATH)
        .addObject("showStartProject", user.getUserPrivileges().contains(UserPrivilege.PATHFINDER_PROJECT_CREATE))
        .addObject("dashboardProjectItemViews", dashboardProjectItemViews)
        .addObject("resultSize", dashboardProjectItemViews.size())
        .addObject("startProjectUrl", ReverseRouter.route(on(StartProjectController.class).startProject(null)));
  }

}
