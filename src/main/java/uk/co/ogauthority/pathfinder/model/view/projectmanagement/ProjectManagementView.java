package uk.co.ogauthority.pathfinder.model.view.projectmanagement;

public class ProjectManagementView {

  private String staticContentHtml;

  private String versionContentHtml;

  public ProjectManagementView(String staticContentHtml, String versionContentHtml) {
    this.staticContentHtml = staticContentHtml;
    this.versionContentHtml = versionContentHtml;
  }

  public String getStaticContentHtml() {
    return staticContentHtml;
  }

  public void setStaticContentHtml(String staticContentHtml) {
    this.staticContentHtml = staticContentHtml;
  }

  public String getVersionContentHtml() {
    return versionContentHtml;
  }

  public void setVersionContentHtml(String versionContentHtml) {
    this.versionContentHtml = versionContentHtml;
  }
}
