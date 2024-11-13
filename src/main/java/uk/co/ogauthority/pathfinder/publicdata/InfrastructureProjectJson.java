package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

record InfrastructureProjectJson(
    Integer id,
    InfrastructureProjectDetailsJson details,
    ContactJson contact,
    InfrastructureProjectFirstProductionDateJson firstProductionDate,
    InfrastructureProjectLocationJson location,
    LocalDateTime submittedOn
) {

  static InfrastructureProjectJson from(
      ProjectDetail projectDetail,
      ProjectOperator projectOperator,
      ProjectInformation projectInformation,
      ProjectLocation projectLocation,
      List<ProjectLocationBlock> projectLocationBlocks
  ) {
    var id = projectDetail.getProject().getId();

    var details = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    var contact = ContactJson.from(projectInformation);

    var firstProductionDate =
        projectInformation.getFirstProductionDateQuarter() != null && projectInformation.getFirstProductionDateYear() != null
            ? InfrastructureProjectFirstProductionDateJson.from(projectInformation)
            : null;

    var location = projectLocation != null ? InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks) : null;

    var submittedOn = LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault());

    return new InfrastructureProjectJson(
        id,
        details,
        contact,
        firstProductionDate,
        location,
        submittedOn
    );
  }
}
