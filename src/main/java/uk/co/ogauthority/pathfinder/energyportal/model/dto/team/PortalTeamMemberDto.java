package uk.co.ogauthority.pathfinder.energyportal.model.dto.team;

import java.util.Set;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.PersonId;

public class PortalTeamMemberDto {

  private final PersonId personId;

  private final Set<PortalRoleDto> roles;

  public PortalTeamMemberDto(PersonId personId, Set<PortalRoleDto> roles) {
    this.personId = personId;
    this.roles = roles;
  }

  public PersonId getPersonId() {
    return personId;
  }

  public Set<PortalRoleDto> getRoles() {
    return roles;
  }
}