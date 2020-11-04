package uk.co.ogauthority.pathfinder.service;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.dashboard.DashboardService;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkAreaServiceTest {

  @Mock
  private DashboardService dashboardService;

  private WorkAreaService workAreaService;

  private static final AuthenticatedUserAccount workAreaOnlyUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private static final AuthenticatedUserAccount createProjectUser = UserTestingUtil.getAuthenticatedUserAccount(
      Set.of(UserPrivilege.PATHFINDER_WORK_AREA, UserPrivilege.PATHFINDER_PROJECT_CREATE)
  );

  private final List<DashboardProjectItemView> dashboardProjectItemViews = List.of(DashboardProjectItemTestUtil.getDashboardProjectItemView());

  @Before
  public void setUp() throws Exception {
    workAreaService = new WorkAreaService(dashboardService);
    when(dashboardService.getDashboardProjectItemViewsForUser(any())).thenReturn(dashboardProjectItemViews);
  }

  @Test
  public void getWorkAreaModelAndViewForUser_createProjectButton_notShownWithoutPrivilege() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(workAreaOnlyUser);
    assertThat(modelAndView.getModel()).containsEntry("showStartProject", false);
  }

  @Test
  public void getWorkAreaModelAndViewForUser_createProjectButton_shownWithPrivilege() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser);
    assertThat(modelAndView.getModel()).containsEntry("showStartProject", true);
  }

  @Test
  public void getWorkAreaModelAndViewForUser_resultSizeMatches() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser);
    assertThat(modelAndView.getModel()).containsEntry("resultSize", dashboardProjectItemViews.size());
  }

  @Test
  public void getWorkAreaModelAndViewForUser_allFieldsSet() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser);
    assertThat(modelAndView.getModel()).containsOnly(
        entry("showStartProject", true),
        entry("dashboardProjectItemViews", dashboardProjectItemViews),
        entry("resultSize", dashboardProjectItemViews.size()),
        entry("startProjectUrl", ReverseRouter.route(on(StartProjectController.class).startProject(null)))
    );
  }
}
