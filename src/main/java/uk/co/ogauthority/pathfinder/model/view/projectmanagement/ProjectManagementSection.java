package uk.co.ogauthority.pathfinder.model.view.projectmanagement;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionPosition;

public class ProjectManagementSection {

  private final String templatePath;

  private final Map<String, Object> templateModel;

  private final int displayOrder;

  private final ProjectManagementPageSectionPosition position;

  public ProjectManagementSection(String templatePath,
                                  Map<String, Object> templateModel,
                                  int displayOrder,
                                  ProjectManagementPageSectionPosition position) {
    this.templatePath = templatePath;
    this.templateModel = templateModel;
    this.displayOrder = displayOrder;
    this.position = position;
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

  public ProjectManagementPageSectionPosition getPosition() {
    return position;
  }
}
