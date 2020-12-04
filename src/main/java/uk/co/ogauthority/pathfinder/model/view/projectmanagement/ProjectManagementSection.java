package uk.co.ogauthority.pathfinder.model.view.projectmanagement;

import java.util.Map;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;

public class ProjectManagementSection {

  private final String templatePath;

  private final Map<String, Object> templateModel;

  private final int displayOrder;

  private final ProjectManagementPageSectionType sectionType;

  public ProjectManagementSection(String templatePath,
                                  Map<String, Object> templateModel,
                                  int displayOrder,
                                  ProjectManagementPageSectionType sectionType) {
    this.templatePath = templatePath;
    this.templateModel = templateModel;
    this.displayOrder = displayOrder;
    this.sectionType = sectionType;
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

  public ProjectManagementPageSectionType getSectionType() {
    return sectionType;
  }
}
