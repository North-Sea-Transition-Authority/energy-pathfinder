package uk.co.ogauthority.pathfinder.service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.repository.dashboard.DashboardProjectItemRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

  @Mock
  private DashboardProjectItemRepository dashboardProjectItemRepository;

  @Mock
  private TeamService teamService;

  private DashboardService dashboardService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private final DashboardProjectItem dashboardProjectItem = DashboardProjectItemTestUtil.getDashboardProjectItem();

  @Before
  public void setUp() throws Exception {
    dashboardService = new DashboardService(
        dashboardProjectItemRepository,
        teamService
    );
  }

  @Test
  public void getDashboardProjectItemsForUser_emptyListIfUserIsInRegulatorTeam() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(true);
    assertThat(dashboardService.getDashboardProjectItemsForUser(authenticatedUser)).isEmpty();
  }

  @Test
  public void getDashboardProjectItemsForUser_resultsForOperatorUser() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(false);
    when(dashboardProjectItemRepository.findAllByOrganisationGroupInOrderByCreatedDatetimeDesc(any())).thenReturn(List.of(dashboardProjectItem));
    assertThat(dashboardService.getDashboardProjectItemsForUser(authenticatedUser).size()).isEqualTo(1);
  }

  @Test
  public void getDashboardProjectItemViewsForUser_operatorUserCorrectNumberOfViewsReturned() {
    when(teamService.isPersonMemberOfRegulatorTeam(authenticatedUser.getLinkedPerson())).thenReturn(false);
    when(dashboardProjectItemRepository.findAllByOrganisationGroupInOrderByCreatedDatetimeDesc(any())).thenReturn(List.of(dashboardProjectItem));
    assertThat(dashboardService.getDashboardProjectItemViewsForUser(authenticatedUser).size()).isEqualTo(1);
  }
}
