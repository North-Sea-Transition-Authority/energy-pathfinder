package uk.co.ogauthority.pathfinder.model.enums.project.management;

public enum ProjectManagementSectionType {

  HEADING(10),
  PROJECT_DETAILS(20),
  ACTIONS(30),
  PROJECT_ARCHIVE(40),
  PROJECT_TRANSFER(50),
  PROJECT_ASSESSMENT(60),
  PROJECT_SUMMARY(70);

  private final int displayOrder;

  ProjectManagementSectionType(int displayOrder) {
    this.displayOrder = displayOrder;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
