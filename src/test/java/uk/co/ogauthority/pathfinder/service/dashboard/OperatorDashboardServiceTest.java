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
public class OperatorDashboardServiceTest {

  @Mock
  private DashboardProjectItemRepository dashboardProjectItemRepository;

  @Mock
  private TeamService teamService;

  private OperatorDashboardService operatorDashboardService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private final DashboardProjectItem item1 = DashboardProjectItemTestUtil.getDashboardProjectItem();
  private final DashboardProjectItem item2 = DashboardProjectItemTestUtil.getDashboardProjectItem();

  @Before
  public void setUp() throws Exception {
    operatorDashboardService = new OperatorDashboardService(
        dashboardProjectItemRepository,
        teamService
    );
  }

  @Test
  public void getDashboardProjectItems_correctNumberOfItemsReturned() {
    when(dashboardProjectItemRepository.findAllByOrganisationGroupInOrderByCreatedDatetimeDesc(any())).thenReturn(
        List.of(item1, item2)
    );
    operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson());
    assertThat(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson()).size()).isEqualTo(2);
  }
}
