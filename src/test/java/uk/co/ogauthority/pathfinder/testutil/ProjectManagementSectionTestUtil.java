package uk.co.ogauthority.pathfinder.testutil;

import java.util.List;
import java.util.Map;
import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.summary.SidebarSectionLink;

public class ProjectManagementSectionTestUtil {

  public static final int DISPLAY_ORDER = 1;
  public static final String TEMPLATE_PATH = "TEMPLATE";
  public static final Map<String, Object> TEMPLATE_MODEL = Map.of("key", "value");

  public static ProjectManagementSection getProjectManagementSection() {
    return new ProjectManagementSection(
        TEMPLATE_PATH,
        TEMPLATE_MODEL,
        DISPLAY_ORDER
    );
  }
}
