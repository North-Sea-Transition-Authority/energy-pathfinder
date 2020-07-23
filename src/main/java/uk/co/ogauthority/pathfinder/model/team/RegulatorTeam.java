package uk.co.ogauthority.pathfinder.model.team;

/**
 * The regulator team. This has no level of scoping.
 */
public class RegulatorTeam extends Team {
  public RegulatorTeam(int id, String name, String description) {
    super(id, name, description, TeamType.REGULATOR);
  }
}
