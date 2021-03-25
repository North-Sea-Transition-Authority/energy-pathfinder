package uk.co.ogauthority.pathfinder.model.enums.project.management;

public enum ProjectManagementSectionType {

  HEADING(10),
  PROJECT_DETAILS(20),
  NOTIFICATION(30),
  ACTIONS(40),
  PROJECT_ARCHIVE(50),
  PROJECT_NO_UPDATE_NOTIFICATION(60),
  PROJECT_UPDATE_REQUEST(70),
  PROJECT_TRANSFER(80),
  PROJECT_ASSESSMENT(90),
  PROJECT_SUMMARY(100);

  private final int displayOrder;

  ProjectManagementSectionType(int displayOrder) {
    this.displayOrder = displayOrder;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
