package uk.co.ogauthority.pathfinder.service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.DashboardFilterTestUtil;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

  @Mock
  RegulatorDashboardService regulatorDashboardService;

  @Mock
  OperatorDashboardService operatorDashboardService;

  @Mock
  private TeamService teamService;

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
        operatorDashboardService
    );
  }

  @Test
  public void getDashboardProjectItemsForUser_noResultsForRegulatorUser() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(true);
    when(regulatorDashboardService.getDashboardProjectItems(filter)).thenReturn(Collections.emptyList());
    assertThat(dashboardService.getDashboardProjectItemsForUser(authenticatedUser, filter).size()).isZero();

    verify(regulatorDashboardService, times(1)).getDashboardProjectItems(filter);
    verify(operatorDashboardService, times(0)).getDashboardProjectItems(any(), any());
  }

  @Test
  public void getDashboardProjectItemViewsForUser_regulatorUserCorrectNumberOfViewsReturned() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(true);
    when(regulatorDashboardService.getDashboardProjectItems(filter)).thenReturn(List.of(qaItem));
    assertThat(dashboardService.getDashboardProjectItemViewsForUser(authenticatedUser, filter).size()).isEqualTo(1);

    verify(regulatorDashboardService, times(1)).getDashboardProjectItems(filter);
    verify(operatorDashboardService, times(0)).getDashboardProjectItems(any(), any());
  }

  @Test
  public void getDashboardProjectItemsForUser_noResultsForOperatorUser() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(false);
    when(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson(), filter)).thenReturn(
        Collections.emptyList()
    );
    assertThat(dashboardService.getDashboardProjectItemsForUser(authenticatedUser, filter).size()).isZero();

    verify(regulatorDashboardService, times(0)).getDashboardProjectItems(any());
    verify(operatorDashboardService, times(1)).getDashboardProjectItems(any(), any());
  }

  @Test
  public void getDashboardProjectItemViewsForUser_operatorUserCorrectNumberOfViewsReturned() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(false);
    when(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson(), filter)).thenReturn(
        List.of(dashboardProjectItem)
    );
    assertThat(dashboardService.getDashboardProjectItemViewsForUser(authenticatedUser, filter).size()).isEqualTo(1);

    verify(regulatorDashboardService, times(0)).getDashboardProjectItems(any());
    verify(operatorDashboardService, times(1)).getDashboardProjectItems(any(), any());
  }
}
