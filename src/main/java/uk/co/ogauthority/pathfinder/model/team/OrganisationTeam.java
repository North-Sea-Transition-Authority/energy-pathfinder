package uk.co.ogauthority.pathfinder.model.team;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

/**
 * A Team scoped to a PortalOrganisationGroup.
 */
public class OrganisationTeam extends Team {

  private final PortalOrganisationGroup portalOrganisationGroup;

  public OrganisationTeam(int id, String name, String description, PortalOrganisationGroup portalOrganisationGroup) {
    super(id, name, description, TeamType.ORGANISATION);
    this.portalOrganisationGroup = portalOrganisationGroup;
  }

  public PortalOrganisationGroup getPortalOrganisationGroup() {
    return portalOrganisationGroup;
  }
}
