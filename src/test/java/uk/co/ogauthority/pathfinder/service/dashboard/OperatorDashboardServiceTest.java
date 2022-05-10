package uk.co.ogauthority.pathfinder.service.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
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
import uk.co.ogauthority.pathfinder.model.entity.dashboard.OperatorDashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.repository.dashboard.OperatorDashboardProjectItemRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.DashboardFilterTestUtil;
import uk.co.ogauthority.pathfinder.testutil.DashboardProjectItemTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class OperatorDashboardServiceTest {

  @Mock
  private OperatorDashboardProjectItemRepository operatorDashboardProjectItemRepository;

  @Mock
  private TeamService teamService;

  private final DashboardFilterService filterService = new DashboardFilterService();

  private OperatorDashboardService operatorDashboardService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private final DashboardProjectItem item1 = DashboardProjectItemTestUtil.getDashboardProjectItem();
  private final DashboardProjectItem item2 = DashboardProjectItemTestUtil.getDashboardProjectItem();
  private final DashboardFilter filter = DashboardFilterTestUtil.getEmptyFilter();

  @Before
  public void setUp() throws Exception {
    operatorDashboardService = new OperatorDashboardService(
        operatorDashboardProjectItemRepository,
        teamService,
        filterService
    );
  }

  @Test
  public void getDashboardProjectItems_correctNumberOfItemsReturned() {
    when(operatorDashboardProjectItemRepository.findAllByOrganisationGroupIn(any())).thenReturn(
        List.of(item1, item2)
    );
    assertThat(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson(), filter).size()).isEqualTo(2);
  }

  @Test
  public void getDashboardProjectItems_includingContributingProjects_correctNumberOfItemsReturned() {
    var portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org1", "org1");
    OrganisationTeam organisationTeam = TeamTestingUtil.getOrganisationTeam(portalOrganisationGroup);
    OperatorDashboardProjectItem contributingProject = new OperatorDashboardProjectItem();
    contributingProject.setSortKey(Instant.now());
    contributingProject.setProjectTypeSortKey(1);
    contributingProject.setUpdateSortKey(Instant.now());
    contributingProject.setContributorOrgIds(List.of(portalOrganisationGroup.getOrgGrpId()));
    OperatorDashboardProjectItem nonContributingProject = new OperatorDashboardProjectItem();
    var dashboardProjectItems = new ArrayList<DashboardProjectItem>();
    dashboardProjectItems.add(item1);
    dashboardProjectItems.add(item2);

    when(operatorDashboardProjectItemRepository.findAllByOrganisationGroupIn(any())).thenReturn(dashboardProjectItems);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUser.getLinkedPerson())).thenReturn(List.of(organisationTeam));
    when(operatorDashboardProjectItemRepository.findAll()).thenReturn(List.of(contributingProject, nonContributingProject));

    assertThat(operatorDashboardService.getDashboardProjectItems(authenticatedUser.getLinkedPerson(), filter))
        .containsExactly(item1, item2, contributingProject);
  }
}
