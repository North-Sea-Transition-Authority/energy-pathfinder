package uk.co.ogauthority.pathfinder.model.enums.project;

public enum ContractBand {
  LESS_THAN_25M("Less than £25 million", 1),
  GREATER_THAN_OR_EQUAL_TO_25M("£25 million or more", 2);

  private final String displayName;
  private final Integer displayOrder;

  ContractBand(String displayName, Integer displayOrder) {
    this.displayName = displayName;
    this.displayOrder = displayOrder;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }
}
