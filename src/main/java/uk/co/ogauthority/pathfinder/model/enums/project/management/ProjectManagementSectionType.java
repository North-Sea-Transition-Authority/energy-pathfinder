package uk.co.ogauthority.pathfinder.model.enums.project.management;

public enum ProjectManagementSectionType {

  HEADING(10),
  PROJECT_DETAILS(20),
  NOTIFICATION(30),
  ACTIONS(40),
  PROJECT_ARCHIVE(50),
  PROJECT_UPDATE_REQUEST(60),
  PROJECT_TRANSFER(70),
  PROJECT_ASSESSMENT(80),
  PROJECT_SUMMARY(90);

  private final int displayOrder;

  ProjectManagementSectionType(int displayOrder) {
    this.displayOrder = displayOrder;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
