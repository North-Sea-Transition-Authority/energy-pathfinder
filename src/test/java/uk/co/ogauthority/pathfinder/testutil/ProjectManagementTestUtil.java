package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView;

public class ProjectManagementTestUtil {

  private static final String TITLE = "title";
  private static final String OPERATOR = "operator";
  private static final String STATIC_CONTENT_HTML = "html";
  private static final String VERSION_CONTENT_HTML = "html";

  public static ProjectManagementView createProjectManagementView() {
    return new ProjectManagementView(
        TITLE,
        OPERATOR,
        STATIC_CONTENT_HTML,
        VERSION_CONTENT_HTML
    );
  }
}
