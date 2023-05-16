package uk.co.ogauthority.pathfinder.externalapi;

import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

class ProjectDtoTestUtil {

  private ProjectDtoTestUtil() {
  }

  static Builder builder() {
    return new Builder();
  }

  static class Builder {
    private Integer projectId;
    private ProjectStatus status;
    private Integer version;
    private String projectTitle;
    private Integer operatorOrganisationGroupId;
    private Integer publishableOrgUnitId;
    private ProjectType projectType;

    public Builder withProjectId(Integer projectId) {
      this.projectId = projectId;
      return this;
    }

    public Builder withStatus(ProjectStatus status) {
      this.status = status;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withProjectTitle(String projectTitle) {
      this.projectTitle = projectTitle;
      return this;
    }

    public Builder withOperatorOrganisationGroupId(Integer operatorOrganisationGroupId) {
      this.operatorOrganisationGroupId = operatorOrganisationGroupId;
      return this;
    }

    public Builder withPublishableOrgUnitId(Integer publishableOrgUnitId) {
      this.publishableOrgUnitId = publishableOrgUnitId;
      return this;
    }

    public Builder withProjectType(ProjectType projectType) {
      this.projectType = projectType;
      return this;
    }

    public ProjectDto build() {
      return new ProjectDto(
          projectId,
          status,
          version,
          projectTitle,
          operatorOrganisationGroupId,
          publishableOrgUnitId,
          projectType
      );
    }
  }
}
