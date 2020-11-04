package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.repository.dashboard.DashboardProjectItemRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class DashboardService {

  private final DashboardProjectItemRepository dashboardProjectItemRepository;
  private final TeamService teamService;

  @Autowired
  public DashboardService(DashboardProjectItemRepository dashboardProjectItemRepository,
                          TeamService teamService) {
    this.dashboardProjectItemRepository = dashboardProjectItemRepository;
    this.teamService = teamService;
  }

  public List<DashboardProjectItem> getDashboardProjectItemsForUser(AuthenticatedUserAccount user) {
    if (teamService.isPersonMemberOfRegulatorTeam(user.getLinkedPerson())) {
      return Collections.emptyList();//TODO PAT-62 admin dashboard
    }

    var orgGroups = teamService.getOrganisationTeamsPersonIsMemberOf(user.getLinkedPerson())
            .stream().map(OrganisationTeam::getPortalOrganisationGroup)
            .collect(Collectors.toList());
    return dashboardProjectItemRepository.findAllByOrganisationGroupInOrderByCreatedDatetimeDesc(orgGroups);
  }

  public List<DashboardProjectItemView> getDashboardProjectItemViewsForUser(AuthenticatedUserAccount user) {
    return getDashboardProjectItemsForUser(user).stream().map(DashboardProjectItemView::from)
        .collect(Collectors.toList());
  }
}
