package uk.co.ogauthority.pathfinder.service.navigation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.team.ManageTeamController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.navigation.TopNavigationItem;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

@Service
public class TopNavigationService {

  public static final String WORK_AREA_TITLE = "Work area";
  public static final String MANAGE_TEAM_TITLE = "Manage teams";

  private final SystemAccessService systemAccessService;

  @Autowired
  public TopNavigationService(SystemAccessService systemAreaAccessService) {
    this.systemAccessService = systemAreaAccessService;
  }

  public List<TopNavigationItem> getTopNavigationItems(AuthenticatedUserAccount user) {
    List<TopNavigationItem> navigationItems = new ArrayList<>();

    if (systemAccessService.canAccessWorkArea(user)) {
      navigationItems.add(
          new TopNavigationItem(WORK_AREA_TITLE, ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))
      );
    }

    if (systemAccessService.canAccessTeamAdministration(user)) {
      navigationItems.add(
          new TopNavigationItem(MANAGE_TEAM_TITLE, ReverseRouter.route(on(ManageTeamController.class).renderTeamTypes(null)))
      );
    }

    return navigationItems;
  }

}
