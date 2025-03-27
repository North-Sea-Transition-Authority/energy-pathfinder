package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;

record InfrastructureProjectLocationJson(
    CoordinateJson centreOfInterestLatitude,
    CoordinateJson centreOfInterestLongitude,
    InfrastructureProjectFieldJson field,
    Integer maximumWaterDepthMeters,
    List<String> licenceBlocks
) {

  static InfrastructureProjectLocationJson from(
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

    var field = InfrastructureProjectFieldJson.from(projectLocation);
    var maximumWaterDepthMeters = projectLocation.getMaximumWaterDepth();

    List<String> licenceBlocks = null;
    if (projectLocationBlocks != null) {
      licenceBlocks = projectLocationBlocks
          .stream()
          .sorted(Comparator.comparing(ProjectLocationBlock::getSortKey))
          .map(ProjectLocationBlock::getBlockReference)
          .toList();
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
