package uk.co.ogauthority.pathfinder.energyportal.model.dto.team;

import java.util.Objects;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;

/**
 * DTO representing the relationship between a resource and a person who is part of that resource.
 */
public class PortalTeamPersonMembershipDto {

  private int resourceId;

  private Person person;

  public PortalTeamPersonMembershipDto(int resourceId, Person person) {
    this.resourceId = resourceId;
    this.person = person;
  }

  public int getResourceId() {
    return resourceId;
  }

  public void setResourceId(int resourceId) {
    this.resourceId = resourceId;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PortalTeamPersonMembershipDto)) {
      return false;
    }

    PortalTeamPersonMembershipDto portalTeamPersonMembershipDto = (PortalTeamPersonMembershipDto) obj;
    return resourceId == portalTeamPersonMembershipDto.resourceId
        && Objects.equals(person, portalTeamPersonMembershipDto.person);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resourceId, person);
  }
}
