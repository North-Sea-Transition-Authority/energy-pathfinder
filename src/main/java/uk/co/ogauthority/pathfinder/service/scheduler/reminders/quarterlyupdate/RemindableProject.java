package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate;

import java.util.Objects;

public class RemindableProject {

  private final int projectDetailId;

  private final int operatorGroupId;

  private final String projectDisplayName;

  public RemindableProject(int projectDetailId, int operatorGroupId, String projectDisplayName) {
    this.projectDetailId = projectDetailId;
    this.operatorGroupId = operatorGroupId;
    this.projectDisplayName = projectDisplayName;
  }

  public int getProjectDetailId() {
    return projectDetailId;
  }

  public int getOperatorGroupId() {
    return operatorGroupId;
  }

  public String getProjectDisplayName() {
    return projectDisplayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RemindableProject)) {
      return false;
    }

    RemindableProject that = (RemindableProject) o;
    return projectDetailId == that.projectDetailId
        && operatorGroupId == that.operatorGroupId
        && Objects.equals(projectDisplayName, that.projectDisplayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        projectDetailId,
        operatorGroupId,
        projectDisplayName
    );
  }

}
