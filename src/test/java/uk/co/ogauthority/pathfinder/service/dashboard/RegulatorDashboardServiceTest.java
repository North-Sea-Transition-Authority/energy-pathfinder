package uk.co.ogauthority.pathfinder.service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorDashboardServiceTest {

  @Mock
  private DashboardProjectItemRepository dashboardProjectItemRepository;

  private RegulatorDashboardService regulatorDashboardService;

  private final DashboardProjectItem item1 = DashboardProjectItemTestUtil.getDashboardProjectItem();
  private final DashboardProjectItem item2 = DashboardProjectItemTestUtil.getDashboardProjectItem();

  @Before
  public void setUp() throws Exception {
    regulatorDashboardService = new RegulatorDashboardService(
        dashboardProjectItemRepository
    );
  }

  @Test
  public void getDashboardProjectItems_correctNumberOfResultsReturned() {
    when(dashboardProjectItemRepository.findAllByStatusInOrderByCreatedDatetimeDesc(RegulatorDashboardService.REGULATOR_PROJECT_ACCESS_STATUSES))
        .thenReturn(List.of(item1, item2));
    assertThat(regulatorDashboardService.getDashboardProjectItems().size()).isEqualTo(2);
  }
}
