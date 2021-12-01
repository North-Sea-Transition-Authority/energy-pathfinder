package uk.co.ogauthority.pathfinder.energyportal.service.organisation;

import java.util.List;
import java.util.Objects;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;

public class OrganisationGroupMembership {

  private int resourceId;

  private PortalOrganisationGroup organisationGroup;

  private List<Person> teamMembers;

  public OrganisationGroupMembership() {}

  public OrganisationGroupMembership(int resourceId,
                              PortalOrganisationGroup portalOrganisationGroup,
                              List<Person> teamMembers) {
    this.resourceId = resourceId;
    this.organisationGroup = portalOrganisationGroup;
    this.teamMembers = teamMembers;
  }

  public int getResourceId() {
    return resourceId;
  }

  public void setResourceId(int resourceId) {
    this.resourceId = resourceId;
  }

  public PortalOrganisationGroup getOrganisationGroup() {
    return organisationGroup;
  }

  public void setOrganisationGroup(PortalOrganisationGroup organisationGroup) {
    this.organisationGroup = organisationGroup;
  }

  public List<Person> getTeamMembers() {
    return teamMembers;
  }

  public void setTeamMembers(List<Person> teamMembers) {
    this.teamMembers = teamMembers;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof OrganisationGroupMembership)) {
      return false;
    }

    OrganisationGroupMembership organisationGroupMembership = (OrganisationGroupMembership) obj;
    return resourceId == organisationGroupMembership.resourceId
        && Objects.equals(organisationGroup, organisationGroupMembership.organisationGroup)
        && Objects.equals(teamMembers, organisationGroupMembership.teamMembers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        resourceId,
        organisationGroup,
        teamMembers
    );
  }

}
