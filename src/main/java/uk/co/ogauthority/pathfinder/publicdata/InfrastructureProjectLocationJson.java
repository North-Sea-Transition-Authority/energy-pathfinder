package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

record InfrastructureProjectLocationJson(
    CoordinateJson centreOfInterestLatitude,
    CoordinateJson centreOfInterestLongitude,
    InfrastructureProjectFieldJson field,
    Integer maximumWaterDepthMeters,
    List<String> licenceBlocks
) {

  static InfrastructureProjectLocationJson from(
      ProjectInformation projectInformation,
      ProjectLocation projectLocation,
      Collection<ProjectLocationBlock> projectLocationBlocks
  ) {
    var centreOfInterestLatitude = CoordinateJson.from(
        projectLocation.getCentreOfInterestLatitudeDegrees(),
        projectLocation.getCentreOfInterestLatitudeMinutes(),
        projectLocation.getCentreOfInterestLatitudeSeconds(),
        projectLocation.getCentreOfInterestLatitudeHemisphere()
    );
    var centreOfInterestLongitude = CoordinateJson.from(
        projectLocation.getCentreOfInterestLongitudeDegrees(),
        projectLocation.getCentreOfInterestLongitudeMinutes(),
        projectLocation.getCentreOfInterestLongitudeSeconds(),
        projectLocation.getCentreOfInterestLongitudeHemisphere()
    );

    InfrastructureProjectFieldJson field = null;
    Integer maximumWaterDepthMeters = null;
    List<String> licenceBlocks = null;

    if (!FieldStage.isEnergyTransition(projectInformation.getFieldStage())) {
      field = InfrastructureProjectFieldJson.from(projectLocation);
      maximumWaterDepthMeters = projectLocation.getMaximumWaterDepth();

      if (projectLocationBlocks != null) {
        licenceBlocks = projectLocationBlocks
            .stream()
            .sorted(Comparator.comparing(ProjectLocationBlock::getSortKey))
            .map(ProjectLocationBlock::getBlockReference)
            .toList();
      }
    }

    return new InfrastructureProjectLocationJson(
        centreOfInterestLatitude,
        centreOfInterestLongitude,
        field,
        maximumWaterDepthMeters,
        licenceBlocks
    );
  }
}
