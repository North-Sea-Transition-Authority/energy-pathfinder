package uk.co.ogauthority.pathfinder.service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.exception.UnsupportedDashboardItemServiceException;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.DashboardFilterType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.DashboardFilterTestUtil;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

  @Mock
  private RegulatorDashboardService regulatorDashboardService;

  @Mock
  private OperatorDashboardService operatorDashboardService;

  @Mock
  private TeamService teamService;

  @Mock
  private TestDashboardItemService testDashboardItemService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private DashboardService dashboardService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private final DashboardProjectItem dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();
  private final DashboardProjectItem qaItem = DashboardProjectItemTestUtil.getDashboardProjectItem(ProjectStatus.QA);
  private final DashboardFilter filter = DashboardFilterTestUtil.getEmptyFilter();

  @Before
  public void setUp() {
    dashboardService = new DashboardService(
        teamService,
        regulatorDashboardService,
        operatorDashboardService,
        List.of(testDashboardItemService),
        templateRenderingService
    );
  }

  @Test
  public void getDashboardProjectHtmlItemsForUser_noResultsForRegulatorUser() {
    when(regulatorDashboardService.getDashboardProjectItems(filter)).thenReturn(Collections.emptyList());

    final var dashboardProjectHtmlItems = dashboardService.getDashboardProjectHtmlItemsForUser(
        authenticatedUser,
        DashboardFilterType.REGULATOR,
        filter
    );

    assertThat(dashboardProjectHtmlItems).isEmpty();

    verify(templateRenderingService, never()).render(
        any(),
        anyMap(),
        anyBoolean()
    );

    verify(regulatorDashboardService, times(1)).getDashboardProjectItems(filter);
    verify(operatorDashboardService, times(0)).getDashboardProjectItems(any(), any());
  }

  @Test
  public void getDashboardProjectHtmlItemsForUser_regulatorUserCorrectNumberOfViewsReturned() {
    when(testDashboardItemService.getSupportedProjectType()).thenReturn(qaItem.getProjectType());
    when(regulatorDashboardService.getDashboardProjectItems(filter)).thenReturn(List.of(qaItem));

    final var dashboardProjectHtmlItems = dashboardService.getDashboardProjectHtmlItemsForUser(
        authenticatedUser,
        DashboardFilterType.REGULATOR,
        filter
    );

    assertThat(dashboardProjectHtmlItems).hasSize(1);

    verify(templateRenderingService, times(1)).render(
        testDashboardItemService.getTemplatePath(),
        new HashMap<>(testDashboardItemService.getTemplateModel(qaItem)),
        true
    );

    verify(regulatorDashboardService, times(1)).getDashboardProjectItems(filter);
    verify(operatorDashboardService, times(0)).getDashboardProjectItems(any(), any());
  }

  @Test(expected = UnsupportedDashboardItemServiceException.class)
  public void getDashboardProjectHtmlItemsForUser_whenUnsupportedProjectTypeItem_thenException() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    dashboardProjectItem.setProjectType(unsupportedProjectType);

    when(testDashboardItemService.getSupportedProjectType()).thenReturn(supportedProjectType);

    when(regulatorDashboardService.getDashboardProjectItems(filter)).thenReturn(List.of(dashboardProjectItem));
    
    dashboardService.getDashboardProjectHtmlItemsForUser(authenticatedUser, DashboardFilterType.REGULATOR, filter);
  }

  @Test
  public void getDashboardProjectHtmlItemsForUser_noResultsForOperatorUser() {
    when(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson(), filter)).thenReturn(
        Collections.emptyList()
    );

    final var dashboardProjectHtmlItems = dashboardService.getDashboardProjectHtmlItemsForUser(
        authenticatedUser,
        DashboardFilterType.OPERATOR,
        filter
    );

    assertThat(dashboardProjectHtmlItems).isEmpty();

    verify(templateRenderingService, never()).render(
        any(),
        anyMap(),
        anyBoolean()
    );

    verify(regulatorDashboardService, times(0)).getDashboardProjectItems(any());
    verify(operatorDashboardService, times(1)).getDashboardProjectItems(any(), any());
  }

  @Test
  public void getDashboardProjectHtmlItemsForUser_operatorUserCorrectNumberOfViewsReturned() {

    when(testDashboardItemService.getSupportedProjectType()).thenReturn(dashboardProjectItem.getProjectType());

    when(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson(), filter)).thenReturn(
        List.of(dashboardProjectItem)
    );

    final var dashboardProjectHtmlItems = dashboardService.getDashboardProjectHtmlItemsForUser(
        authenticatedUser,
        DashboardFilterType.OPERATOR,
        filter
    );

    assertThat(dashboardProjectHtmlItems).hasSize(1);

    verify(templateRenderingService, times(1)).render(
        testDashboardItemService.getTemplatePath(),
        new HashMap<>(testDashboardItemService.getTemplateModel(dashboardProjectItem)),
        true
    );

    verify(regulatorDashboardService, times(0)).getDashboardProjectItems(any());
    verify(operatorDashboardService, times(1)).getDashboardProjectItems(any(), any());
  }

  @Test
  public void getDashboardFilterType_regulator() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(true);
    assertThat(dashboardService.getDashboardFilterType(authenticatedUser)).isEqualTo(DashboardFilterType.REGULATOR);
  }

  @Test
  public void getDashboardFilterType_operator() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(false);
    assertThat(dashboardService.getDashboardFilterType(authenticatedUser)).isEqualTo(DashboardFilterType.OPERATOR);
  }

  @Test
  public void getDefaultFilterForUser_regulator() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(true);
    var filter = dashboardService.getDefaultFilterForUser(authenticatedUser);
    assertThat(filter.getProjectStatusList()).isEqualTo(DashboardService.REGULATOR_STATUS_DEFAULTS);
    assertCommonFields(filter);
  }

  @Test
  public void getDefaultFilterForUser_operator() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(false);
    var filter = dashboardService.getDefaultFilterForUser(authenticatedUser);
    assertThat(filter.getProjectStatusList()).isEqualTo(DashboardService.OPERATOR_STATUS_DEFAULTS);
    assertCommonFields(filter);
  }


  private void assertCommonFields(DashboardFilter filter) {
    assertThat(filter.getOperatorName()).isNull();
    assertThat(filter.getProjectTitle()).isNull();
    assertThat(filter.getFieldStages()).isNull();
    assertThat(filter.getField()).isNull();
    assertThat(filter.getUkcsAreas()).isNull();
  }
}
