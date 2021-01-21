package uk.co.ogauthority.pathfinder.service.navigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.quarterlystatistics.QuarterlyStatisticsController;
import uk.co.ogauthority.pathfinder.controller.team.ManageTeamController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.navigation.TopNavigationItem;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class TopNavigationService {

  public static final String WORK_AREA_TITLE = "Work area";
  public static final String MANAGE_TEAM_TITLE = "Manage teams";
  public static final String ORGANISATION_USERS_TITLE = "Organisation users";

  private final SystemAccessService systemAccessService;
  private final TeamService teamService;

  @Autowired
  public TopNavigationService(SystemAccessService systemAreaAccessService,
                              TeamService teamService) {
    this.systemAccessService = systemAreaAccessService;
    this.teamService = teamService;
  }

  public List<TopNavigationItem> getTopNavigationItems(AuthenticatedUserAccount user) {
    List<TopNavigationItem> navigationItems = new ArrayList<>();

    if (systemAccessService.canAccessWorkArea(user)) {
      navigationItems.add(
          new TopNavigationItem(WORK_AREA_TITLE, ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)))
      );
    }

    if (systemAccessService.canViewTeam(user)) {
      navigationItems.add(getTeamManagementTopNavigationItem(user));
    }

    if (systemAccessService.canAccessQuarterlyStatistics(user)) {
      navigationItems.add(new TopNavigationItem(
          QuarterlyStatisticsController.QUARTERLY_STATISTICS_TITLE,
          ReverseRouter.route(on(QuarterlyStatisticsController.class).getQuarterlyStatistics(null)))
      );
    }

    return navigationItems;
  }

  private TopNavigationItem getTeamManagementTopNavigationItem(AuthenticatedUserAccount user) {
    var isMemberOfRegulatorTeam = teamService.isPersonMemberOfRegulatorTeam(user.getLinkedPerson());
    var title = (isMemberOfRegulatorTeam ? MANAGE_TEAM_TITLE : ORGANISATION_USERS_TITLE);
    return new TopNavigationItem(title, ReverseRouter.route(on(ManageTeamController.class).renderTeamTypes(null)));
  }

}
