package uk.co.ogauthority.pathfinder.model.enums.project;

public enum FieldStage {
  DISCOVERY("Discovery", "Early phase before FDP approval", 1),
  DEVELOPMENT("Development", "FDP has been approved", 2),
  OPERATIONS("Operations", "Field now operational", 3),
  DECOMMISSIONING("Decommissioning", "Decommissioning planning commenced either pre / post COP", 4),
  ENERGY_TRANSITION("Energy transition", "", 5);

  private final String displayName;

  private final String description;

  private final Integer displayOrder;

  FieldStage(String displayName, String description, Integer displayOrder) {
    this.displayName = displayName;
    this.description = description;
    this.displayOrder = displayOrder;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }
}
