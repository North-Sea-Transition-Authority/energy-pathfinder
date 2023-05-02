package uk.co.ogauthority.pathfinder.externalapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

public class ProjectDto {

  private final Integer projectId;
  private final ProjectStatus projectStatus;
  private final Integer projectVersion;
  private final String projectTitle;
  private final Integer operatorOrganisationGroupId;
  private final Integer publishableOrganisationUnitId;
  private final ProjectType projectType;

  public ProjectDto(Integer projectId,
                    ProjectStatus projectStatus,
                    Integer projectVersion,
                    String projectTitle,
                    Integer operatorOrganisationGroupId,
                    Integer publishableOrganisationUnitId,
                    ProjectType projectType) {
    this.projectId = projectId;
    this.projectStatus = projectStatus;
    this.projectVersion = projectVersion;
    this.projectTitle = projectTitle;
    this.operatorOrganisationGroupId = operatorOrganisationGroupId;
    this.publishableOrganisationUnitId = publishableOrganisationUnitId;
    this.projectType = projectType;
  }

  @JsonProperty
  Integer getProjectId() {
    return projectId;
  }

  @JsonProperty
  ProjectStatus getProjectStatus() {
    return projectStatus;
  }

  @JsonProperty
  Integer getProjectVersion() {
    return projectVersion;
  }

  @JsonProperty
  String getProjectTitle() {
    return projectTitle;
  }

  @JsonProperty
  Integer getOperatorOrganisationGroupId() {
    return operatorOrganisationGroupId;
  }

  @JsonProperty
  Integer getPublishableOrganisationUnitId() {
    return publishableOrganisationUnitId;
  }

  @JsonProperty
  ProjectType getProjectType() {
    return projectType;
  }
}
