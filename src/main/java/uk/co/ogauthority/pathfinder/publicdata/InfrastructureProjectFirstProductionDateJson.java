package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

record InfrastructureProjectFirstProductionDateJson(
    String quarter,
    Integer year
) {

  static InfrastructureProjectFirstProductionDateJson from(ProjectInformation projectInformation) {
    return new InfrastructureProjectFirstProductionDateJson(
        projectInformation.getFirstProductionDateQuarter().name(),
        projectInformation.getFirstProductionDateYear()
    );
  }
}
