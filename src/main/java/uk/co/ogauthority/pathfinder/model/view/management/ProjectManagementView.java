package uk.co.ogauthority.pathfinder.model.view.management;

public class ProjectManagementView {

  private String title;

  private String operator;

  private String sectionsHtml;

  public ProjectManagementView(String title, String operator, String sectionsHtml) {
    this.title = title;
    this.operator = operator;
    this.sectionsHtml = sectionsHtml;
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

  public String getSectionsHtml() {
    return sectionsHtml;
  }

  public void setSectionsHtml(String sectionsHtml) {
    this.sectionsHtml = sectionsHtml;
  }
}
