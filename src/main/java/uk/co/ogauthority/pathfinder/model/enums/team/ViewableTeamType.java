package uk.co.ogauthority.pathfinder.model.enums.team;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

/**
 * Enumeration of categories available inside 'Manage teams' screen.
 */
public enum ViewableTeamType {

  REGULATOR_TEAM(
      "OGA team",
      "Team for regulator users",
      ReverseRouter.route(on(PortalTeamManagementController.class).renderManageableTeams(null, TeamType.REGULATOR)),
      10
  ),

  ORGANISATION_TEAMS(
      "Organisation group teams",
      "Teams for industry users",
      ReverseRouter.route(on(PortalTeamManagementController.class).renderManageableTeams(null, TeamType.ORGANISATION)),
      20
  );

  private final String linkText;
  private final String linkHint;
  private final String linkUrl;
  private final int displayOrder;

  ViewableTeamType(String linkText, String linkHint, String linkUrl, int displayOrder) {
    this.linkText = linkText;
    this.linkHint = linkHint;
    this.linkUrl = linkUrl;
    this.displayOrder = displayOrder;
  }

  public String getLinkText() {
    return linkText;
  }

  public String getLinkHint() {
    return linkHint;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
