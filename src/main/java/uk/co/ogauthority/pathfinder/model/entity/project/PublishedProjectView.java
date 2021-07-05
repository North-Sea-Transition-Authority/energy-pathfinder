package uk.co.ogauthority.pathfinder.model.entity.project;

import uk.co.ogauthority.pathfinder.model.addtolist.AddToListItem;

public class PublishedProjectView implements AddToListItem {

  private Integer projectId;

  private String displayName;

  private Boolean isValid;

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Boolean getValid() {
    return isValid;
  }

  public void setValid(Boolean valid) {
    isValid = valid;
  }

  @Override
  public String getId() {
    return String.valueOf(getProjectId());
  }

  @Override
  public String getName() {
    return getDisplayName();
  }

  @Override
  public Boolean isValid() {
    return getValid();
  }
}
