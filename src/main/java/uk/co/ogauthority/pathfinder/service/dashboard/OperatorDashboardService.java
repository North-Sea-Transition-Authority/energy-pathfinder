package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.model.dashboard.DashboardFilter;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.repository.dashboard.DashboardProjectItemRepository;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class OperatorDashboardService {

  private final DashboardProjectItemRepository dashboardProjectItemRepository;
  private final TeamService teamService;
  private final DashboardFilterService filterService;

  @Autowired
  public OperatorDashboardService(DashboardProjectItemRepository dashboardProjectItemRepository,
                                  TeamService teamService,
                                  DashboardFilterService filterService) {
    this.dashboardProjectItemRepository = dashboardProjectItemRepository;
    this.teamService = teamService;
    this.filterService = filterService;
  }

  public List<DashboardProjectItem> getDashboardProjectItems(Person person, DashboardFilter filter) {
    var orgGroups = teamService.getOrganisationTeamsPersonIsMemberOf(person)
        .stream()
        .map(OrganisationTeam::getPortalOrganisationGroup)
        .collect(Collectors.toList());

    var dashboardItems = dashboardProjectItemRepository.findAllByOrganisationGroupInOrderByCreatedDatetimeDesc(orgGroups);

    return filterService.filter(dashboardItems, filter);
  }
}
