package uk.co.ogauthority.pathfinder.testutil;

import java.util.List;
import java.util.Map;
import uk.co.ogauthority.pathfinder.model.view.SidebarSectionLink;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSectionSummary;

public class ProjectSectionSummaryTestUtil {
  public static final int DISPLAY_ORDER = 1;
  public static final String TEMPLATE_PATH = "TEMPLATE";
  public static final Map<String, Object> TEMPLATE_MODEL = Map.of("key", "value");
  public static final String LINK_NAME = "Section";
  public static final String LINK_HREF = "#section";
  public static final List<SidebarSectionLink> SIDEBAR_SECTION_LINKS = List.of(SidebarSectionLink.createAnchorLink(
      LINK_NAME,
      LINK_HREF
  ));


  public static ProjectSectionSummary getSummary() {
    return new ProjectSectionSummary(
        SIDEBAR_SECTION_LINKS,
        TEMPLATE_PATH,
        TEMPLATE_MODEL,
        DISPLAY_ORDER
    );
  }
}
