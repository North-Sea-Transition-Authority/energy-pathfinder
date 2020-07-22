package uk.co.ogauthority.pathfinder.model.team;

import java.util.Objects;

/**
 * Class which encapsulates concept of a 'Role' which members of a Team might have.
 */
public class Role {

  // The team coordinator role as it appears in the resource type file
  public static final String TEAM_ADMINISTRATOR_ROLE_NAME = "RESOURCE_COORDINATOR";

  // name is the string mnemonic which describes the role inside the portal based resource type file
  private final String name;
  private final String title;
  private final String description;
  private final int displaySequence;

  public Role(String name, String title, String description, int displaySequence) {
    this.name = name;
    this.title = title;
    this.description = description;
    this.displaySequence = displaySequence;
  }

  public boolean isTeamAdministratorRole() {
    return TEAM_ADMINISTRATOR_ROLE_NAME.equals(this.name);
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getDisplaySequence() {
    return displaySequence;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Role)) {
      return false;
    }
    Role role = (Role) o;
    return displaySequence == role.displaySequence
        && Objects.equals(name, role.name)
        && Objects.equals(title, role.title)
        && Objects.equals(description, role.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, title, description, displaySequence);
  }
}
