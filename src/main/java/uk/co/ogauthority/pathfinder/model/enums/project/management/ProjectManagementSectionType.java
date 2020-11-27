package uk.co.ogauthority.pathfinder.model.enums.project.management;

public enum ProjectManagementSectionType {

  PROJECT_DETAILS(10),
  ACTIONS(20),
  PROJECT_ASSESSMENT(30),
  PROJECT_SUMMARY(40);

  private final int displayOrder;

  ProjectManagementSectionType(int displayOrder) {
    this.displayOrder = displayOrder;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
