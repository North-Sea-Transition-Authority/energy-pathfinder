package uk.co.ogauthority.pathfinder.model.view.summary;

import java.util.List;
import java.util.Map;

public class ProjectSectionSummary {
  private final List<SidebarSectionLink> sidebarSectionLinks;

  private final String templatePath;

  private final Map<String, Object> templateModel;

  private final int displayOrder;

  public ProjectSectionSummary(
      List<SidebarSectionLink> sidebarSectionLinks,
      String templatePath,
      Map<String, Object> templateModel, int displayOrder) {
    this.sidebarSectionLinks = sidebarSectionLinks;
    this.templatePath = templatePath;
    this.templateModel = templateModel;
    this.displayOrder = displayOrder;
  }

  public List<SidebarSectionLink> getSidebarSectionLinks() {
    return sidebarSectionLinks;
  }

  public String getTemplatePath() {
    return templatePath;
  }

  public Map<String, Object> getTemplateModel() {
    return templateModel;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }
}
