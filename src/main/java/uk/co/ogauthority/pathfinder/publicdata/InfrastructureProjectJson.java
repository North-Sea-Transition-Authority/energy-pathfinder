package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;

record InfrastructureProjectJson(
    Integer projectId,
    ProjectStatus projectStatus,
    Integer projectVersion
) {

  static InfrastructureProjectJson from(ProjectDetail projectDetail) {
    return new InfrastructureProjectJson(
        projectDetail.getProject().getId(),
        projectDetail.getStatus(),
        projectDetail.getVersion()
    );
  }
}
