package uk.co.ogauthority.pathfinder.model.team;

public abstract class Team {

  private final int id;

  private final String name;

  private final String description;

  private final TeamType type;

  Team(int id, String name, String description, TeamType type) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public TeamType getType() {
    return type;
  }
}
