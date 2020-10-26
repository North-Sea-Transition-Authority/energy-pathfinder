package uk.co.ogauthority.pathfinder.model.view.summary;

import java.util.List;

/**
 * A collection of rendered {@link ProjectSectionSummary} objects and their associated sideBarSectionLinks.
 */
public class ProjectSummaryView {
  private String summaryHtml;
  private List<SidebarSectionLink> sidebarSectionLinks;

  public ProjectSummaryView(String summaryHtml,
                            List<SidebarSectionLink> sidebarSectionLinks) {
    this.summaryHtml = summaryHtml;
    this.sidebarSectionLinks = sidebarSectionLinks;
  }

  public String getSummaryHtml() {
    return summaryHtml;
  }

  public void setSummaryHtml(String summaryHtml) {
    this.summaryHtml = summaryHtml;
  }

  public List<SidebarSectionLink> getSidebarSectionLinks() {
    return sidebarSectionLinks;
  }

  public void setSidebarSectionLinks(
      List<SidebarSectionLink> sidebarSectionLinks) {
    this.sidebarSectionLinks = sidebarSectionLinks;
  }
}
