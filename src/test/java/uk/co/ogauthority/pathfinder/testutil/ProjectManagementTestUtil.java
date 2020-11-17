package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementView;

public class ProjectManagementTestUtil {

  private static final String TITLE = "title";
  private static final String OPERATOR = "operator";
  private static final String HTML = "html";

  public static ProjectManagementView createProjectManagementView() {
    return new ProjectManagementView(
        TITLE,
        OPERATOR,
        HTML
    );
  }
}
