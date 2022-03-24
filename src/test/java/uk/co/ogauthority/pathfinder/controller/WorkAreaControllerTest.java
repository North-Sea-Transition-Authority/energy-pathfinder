package uk.co.ogauthority.pathfinder.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.MetricsProvider;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.model.form.dashboard.DashboardFilterForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.WorkAreaService;
import uk.co.ogauthority.pathfinder.service.dashboard.DashboardService;
import uk.co.ogauthority.pathfinder.testutil.DashboardFilterTestUtil;
import uk.co.ogauthority.pathfinder.testutil.MetricsProviderTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = WorkAreaController.class,
    includeFilters=@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WorkAreaService.class})
    )
public class WorkAreaControllerTest extends AbstractControllerTest {

  private static final String TITLE = "a test project title";
  private static final String FIELD = "Field name";
  private static final List<FieldStage> FIELD_STAGES = List.of(FieldStage.DISCOVERY, FieldStage.ENERGY_TRANSITION);
  private static final List<UkcsArea> UKCS_AREAS = List.of(UkcsArea.IS, UkcsArea.CNS);
  private static final List<ProjectStatus> STATUSES = List.of(ProjectStatus.DRAFT, ProjectStatus.QA);


  @MockBean
  private DashboardService dashboardService;

  @MockBean
  private MetricsProvider metricsProvider;

  @Autowired
  private WorkAreaService workAreaService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private static final DashboardFilter DEFAULT_FILTER = DashboardFilterTestUtil.getEmptyFilter();

  @Before
  public void setUp() throws Exception {
    when(dashboardService.getDefaultFilterForUser(authenticatedUser))
        .thenReturn(new DashboardFilter(DashboardService.OPERATOR_STATUS_DEFAULTS));
    when(metricsProvider.getDashboardTimer()).thenReturn(MetricsProviderTestUtil.getNoOpTimer());
  }

  @Test
  public void authenticatedUser_hasAccessToWorkArea() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(authenticatedUser, DEFAULT_FILTER)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessWorkArea() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(unAuthenticatedUser, DEFAULT_FILTER)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToFilteredWorkArea() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(WorkAreaController.class).getWorkAreaFiltered(authenticatedUser, null, DEFAULT_FILTER, Optional.empty())))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessFilteredWorkArea() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(WorkAreaController.class).getWorkAreaFiltered(unAuthenticatedUser, null, DEFAULT_FILTER, Optional.empty())))
        .with(authenticatedUserAndSession(unAuthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getWorkAreaClearFilter() throws Exception {
    var session = mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkAreaClearFilter(authenticatedUser, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .sessionAttr("dashboardFilter", new DashboardFilter(List.of(ProjectStatus.DRAFT))))
        .andExpect(status().is3xxRedirection())
        .andReturn().getRequest().getSession();

    assertThat(session.getAttribute("dashboardFilter")).isEqualTo(new DashboardFilter());
  }

  @Test
  public void filterPreferences_setInModel() throws Exception {
    var filter = new DashboardFilter();
    filter.setProjectTitle(TITLE);
    filter.setField(FIELD);
    filter.setFieldStages(FIELD_STAGES);
    filter.setUkcsAreas(UKCS_AREAS);
    filter.setProjectStatusList(STATUSES);

    var result = mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(authenticatedUser, filter)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .sessionAttr("dashboardFilter", filter)
        .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();

    var form = (DashboardFilterForm) result.getModelAndView().getModel().get("form");
    assertThat(form.getProjectTitle()).isEqualTo(TITLE);
    assertThat(form.getField()).isEqualTo(FIELD);
    assertThat(form.getFieldStages()).isEqualTo(FIELD_STAGES);
    assertThat(form.getUkcsAreas()).isEqualTo(UKCS_AREAS);
    assertThat(form.getProjectStatusList()).isEqualTo(STATUSES);
  }

  @Test
  public void filterPreferences_setInModel_fromForm() throws Exception {
    var form = new DashboardFilterForm();
    var filter = new DashboardFilter();
    form.setProjectTitle(TITLE);
    form.setField(FIELD);
    form.setFieldStages(FIELD_STAGES);
    form.setUkcsAreas(UKCS_AREAS);
    form.setProjectStatusList(STATUSES);
    filter.setFromForm(form);

    mockMvc.perform(post(ReverseRouter.route(on(WorkAreaController.class).getWorkAreaFiltered(authenticatedUser, form, DEFAULT_FILTER, Optional.empty())))
        .with(authenticatedUserAndSession(authenticatedUser))
        .sessionAttr("dashboardFilter", DEFAULT_FILTER)
        .flashAttr("form", form)
        .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    verify(analyticsService, times(1)).sendGoogleAnalyticsEvent(any(), eq(AnalyticsEventCategory.WORK_AREA_FILTERED),
        eq(Map.of("projectTitle", "true",
            "field", "true",
            "fieldStages", "true",
            "ukcsAreas", "true",
            "projectStatusList", "true")));

    var result = mockMvc.perform(get(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(authenticatedUser, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .sessionAttr("dashboardFilter", filter)
        .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();

    var modelForm = (DashboardFilterForm) result.getModelAndView().getModel().get("form");
    assertThat(modelForm.getProjectTitle()).isEqualTo(TITLE);
    assertThat(modelForm.getField()).isEqualTo(FIELD);
    assertThat(modelForm.getFieldStages()).isEqualTo(FIELD_STAGES);
    assertThat(modelForm.getUkcsAreas()).isEqualTo(UKCS_AREAS);
    assertThat(modelForm.getProjectStatusList()).isEqualTo(STATUSES);
  }
}
