package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class SelectableProjectTestUtil {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Integer projectId = 1;
    private ProjectType projectType = ProjectType.INFRASTRUCTURE;
    private String operatorGroupName = "Test operator group name";
    private String projectDisplayName = "Test project display name";
    private boolean published = true;

    private Builder() {
    }

    public Builder withProjectId(Integer projectId) {
      this.projectId = projectId;
      return this;
    }

    public Builder withProjectType(ProjectType projectType) {
      this.projectType = projectType;
      return this;
    }

    public Builder withOperatorGroupName(String operatorGroupName) {
      this.operatorGroupName = operatorGroupName;
      return this;
    }

    public Builder withProjectDisplayName(String projectDisplayName) {
      this.projectDisplayName = projectDisplayName;
      return this;
    }

    public Builder withPublished(boolean published) {
      this.published = published;
      return this;
    }

    public SelectableProject build() {
      var selectableProject = new SelectableProject();

      selectableProject.setProjectId(projectId);
      selectableProject.setProjectType(projectType);
      selectableProject.setOperatorGroupName(operatorGroupName);
      selectableProject.setProjectDisplayName(projectDisplayName);
      selectableProject.setPublished(published);

      return selectableProject;
    }
  }
}
