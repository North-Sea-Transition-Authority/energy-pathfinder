package uk.co.ogauthority.pathfinder.testutil;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionPosition;

public class ProjectManagementSectionTestUtil {

  public static final int DISPLAY_ORDER = 1;
  public static final String TEMPLATE_PATH = "TEMPLATE";
  public static final Map<String, Object> TEMPLATE_MODEL = Map.of("key", "value");
  public static final ProjectManagementPageSectionPosition POSITION = ProjectManagementPageSectionPosition.STATIC_CONTENT;

  public static ProjectManagementSection getProjectManagementSection() {
    return getProjectManagementSection(DISPLAY_ORDER);
  }

  public static ProjectManagementSection getProjectManagementSection(int displayOrder) {
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        TEMPLATE_MODEL,
        displayOrder,
        POSITION
    );
  }
}
