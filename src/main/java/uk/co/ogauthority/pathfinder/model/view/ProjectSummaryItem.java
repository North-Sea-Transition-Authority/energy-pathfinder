package uk.co.ogauthority.pathfinder.model.view;

import uk.co.ogauthority.pathfinder.util.summary.SummaryItem;

public abstract class ProjectSummaryItem implements SummaryItem {

  protected Integer displayOrder;

  protected Integer id;

  protected Integer projectId;

  protected Boolean isValid;

  @Override
  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public void setIsValid(Boolean valid) {
    isValid = valid;
  }

  public Boolean getValid() {
    return isValid;
  }

  @Override
  public Boolean isValid() {
    return isValid;
  }
}
