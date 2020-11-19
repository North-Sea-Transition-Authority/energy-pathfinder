package uk.co.ogauthority.pathfinder.model.view.projectmanagement;

import java.util.Map;

public class ProjectManagementSection {

  private final String templatePath;

  private final Map<String, Object> templateModel;

  private final int displayOrder;

  public ProjectManagementSection(String templatePath,
                                  Map<String, Object> templateModel,
                                  int displayOrder) {
    this.templatePath = templatePath;
    this.templateModel = templateModel;
    this.displayOrder = displayOrder;
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
