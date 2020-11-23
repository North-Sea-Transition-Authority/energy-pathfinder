package uk.co.ogauthority.pathfinder.model.view.projectmanagement;

public class ProjectManagementView {

  private String title;

  private String operator;

  private String staticContentHtml;

  private String versionContentHtml;

  public ProjectManagementView(String title, String operator, String staticContentHtml, String versionContentHtml) {
    this.title = title;
    this.operator = operator;
    this.staticContentHtml = staticContentHtml;
    this.versionContentHtml = versionContentHtml;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
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
