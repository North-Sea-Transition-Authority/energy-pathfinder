package uk.co.ogauthority.pathfinder.service;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
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

  @Autowired
  public WorkAreaService(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  public ModelAndView getWorkAreaModelAndViewForUser(AuthenticatedUserAccount user, DashboardFilter filter, DashboardFilterForm form) {
    var dashboardProjectItemViews =  dashboardService.getDashboardProjectItemViewsForUser(user, filter);
    return new ModelAndView(WORK_AREA_TEMPLATE_PATH)
        .addObject("startProjectButton", getStartProjectLinkButton(user))
        .addObject("form", form)
        .addObject("statuses", ProjectStatus.getAllAsMap())
        .addObject("fieldStages", FieldStage.getAllAsMap())
        .addObject("ukcsAreas", UkcsArea.getAllAsMap())
        .addObject("dashboardProjectItemViews", dashboardProjectItemViews)
        .addObject("resultSize", dashboardProjectItemViews.size());
  }

  public LinkButton getStartProjectLinkButton(AuthenticatedUserAccount user) {
    return new LinkButton(
        LINK_BUTTON_TEXT,
        ReverseRouter.route(on(StartProjectController.class).startProject(null)),
        user.getUserPrivileges().contains(UserPrivilege.PATHFINDER_PROJECT_CREATE),
        ButtonType.PRIMARY
    );
  }

}
