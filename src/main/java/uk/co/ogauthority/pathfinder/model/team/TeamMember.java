package uk.co.ogauthority.pathfinder.model.team;

import java.util.Set;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;

public class TeamMember {

  private final Team team;
  private final Person person;
  private final Set<Role> roleSet;

  public TeamMember(Team team, Person person, Set<Role> roleSet) {
    this.team = team;
    this.person = person;
    this.roleSet = roleSet;
  }

  public Person getPerson() {
    return person;
  }

  public Set<Role> getRoleSet() {
    return roleSet;
  }

  public Team getTeam() {
    return team;
  }

  public boolean isTeamAdministrator() {
    return roleSet.stream().anyMatch(Role::isTeamAdministratorRole);
  }
}
