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
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.project.StartProjectController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.enums.DashboardFilterType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.dashboard.DashboardService;
import uk.co.ogauthority.pathfinder.testutil.DashboardFilterTestUtil;
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
  private final DashboardFilter filter = DashboardFilterTestUtil.getEmptyFilter();
  private final DashboardFilterForm form = DashboardFilterTestUtil.getEmptyForm();

  @Before
  public void setUp() throws Exception {
    workAreaService = new WorkAreaService(dashboardService);
    when(dashboardService.getDashboardProjectItemViewsForUser(any(), any(), any())).thenReturn(dashboardProjectItemViews);
  }

  @Test
  public void getWorkAreaModelAndViewForUser_createProjectButton_notShownWithoutPrivilege() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(workAreaOnlyUser, filter, form);
    LinkButton link = (LinkButton) modelAndView.getModel().get("startProjectButton");
    assertThat(link.getEnabled()).isFalse();
  }

  @Test
  public void getWorkAreaModelAndViewForUser_createProjectButton_shownWithPrivilege() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser, filter, form);
    LinkButton link = (LinkButton) modelAndView.getModel().get("startProjectButton");
    assertThat(link.getEnabled()).isTrue();
  }

  @Test
  public void getWorkAreaModelAndViewForUser_resultSizeMatches() {
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser, filter, form);
    assertThat(modelAndView.getModel()).containsEntry("resultSize", dashboardProjectItemViews.size());
  }

  @Test
  public void getWorkAreaModelAndViewForUser_allFieldsSet_regulator() {
    when(dashboardService.getDashboardFilterType(createProjectUser)).thenReturn(DashboardFilterType.REGULATOR);
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser, filter, form);
    LinkButton link = (LinkButton) modelAndView.getModel().get("startProjectButton");

    assertThat(link.getEnabled()).isTrue();
    assertLinkFieldsCorrect(link);
    assertModelAndViewFieldsSet(
        modelAndView,
        link,
        true
    );
  }

  @Test
  public void getWorkAreaModelAndViewForUser_allFieldsSet_operator() {
    when(dashboardService.getDashboardFilterType(createProjectUser)).thenReturn(DashboardFilterType.OPERATOR);
    var modelAndView = workAreaService.getWorkAreaModelAndViewForUser(createProjectUser, filter, form);
    LinkButton link = (LinkButton) modelAndView.getModel().get("startProjectButton");

    assertThat(link.getEnabled()).isTrue();
    assertLinkFieldsCorrect(link);
    assertModelAndViewFieldsSet(
        modelAndView,
        link,
        false
    );
  }

  @Test
  public void getLink_enabledWithCorrectPrivilege() {
    var link = workAreaService.getStartProjectLinkButton(createProjectUser);
    assertThat(link.getEnabled()).isTrue();
    assertLinkFieldsCorrect(link);
  }

  @Test
  public void getLink_disabledWithCorrectPrivilege() {
    var link = workAreaService.getStartProjectLinkButton(workAreaOnlyUser);
    assertThat(link.getEnabled()).isFalse();
    assertLinkFieldsCorrect(link);
  }

  @Test
  public void getDefaultFilterForUser() {
    var filter = new DashboardFilter(DashboardService.REGULATOR_STATUS_DEFAULTS);
    when(dashboardService.getDefaultFilterForUser(createProjectUser)).thenReturn(filter);
    var defaultFilterForUser = workAreaService.getDefaultFilterForUser(createProjectUser);
    assertThat(defaultFilterForUser).isEqualTo(filter);
  }

  private void assertLinkFieldsCorrect(LinkButton link) {
    assertThat(link.getButtonType()).isEqualTo(ButtonType.PRIMARY);
    assertThat(link.getPrompt()).isEqualTo(WorkAreaService.LINK_BUTTON_TEXT);
    assertThat(link.getUrl()).isEqualTo(ReverseRouter.route(on(StartProjectController.class).startProject(null)));
  }

  private void assertModelAndViewFieldsSet(ModelAndView modelAndView, LinkButton link, Boolean includeOperatorFilter) {
    assertThat(modelAndView.getModel()).containsOnly(
        entry("dashboardProjectItemViews", dashboardProjectItemViews),
        entry("startProjectButton", link),
        entry("includeOperatorFilter", includeOperatorFilter),
        entry("resultSize", dashboardProjectItemViews.size()),
        entry("form", form),
        entry("statuses", ProjectStatus.getAllAsMap()),
        entry("fieldStages", FieldStage.getAllAsMap()),
        entry("ukcsAreas", UkcsArea.getAllAsMap())
    );
  }
}
