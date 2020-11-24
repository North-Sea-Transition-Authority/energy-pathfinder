package uk.co.ogauthority.pathfinder.service.dashboard;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.dashboard.DashboardProjectItem;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.dashboard.DashboardProjectItemView;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class DashboardService {

  private final TeamService teamService;
  private final RegulatorDashboardService regulatorDashboardService;
  private final OperatorDashboardService operatorDashboardService;

  @Autowired
  public DashboardService(TeamService teamService,
                          RegulatorDashboardService regulatorDashboardService,
                          OperatorDashboardService operatorDashboardService) {
    this.teamService = teamService;
    this.regulatorDashboardService = regulatorDashboardService;
    this.operatorDashboardService = operatorDashboardService;
  }

  public List<DashboardProjectItem> getDashboardProjectItemsForUser(AuthenticatedUserAccount user) {
    var person = user.getLinkedPerson();

    if (teamService.isPersonMemberOfRegulatorTeam(person)) {
      return regulatorDashboardService.getDashboardProjectItems();
    }

    return operatorDashboardService.getDashboardProjectItems(person);
  }

  public List<DashboardProjectItemView> getDashboardProjectItemViewsForUser(AuthenticatedUserAccount user) {
    return getDashboardProjectItemsForUser(user).stream().map(DashboardProjectItemView::from)
        .collect(Collectors.toList());
  }
}
