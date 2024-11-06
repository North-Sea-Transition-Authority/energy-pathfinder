package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Comparator;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;

record InfrastructureProjectLocationJson(
    InfrastructureProjectFieldJson field,
    Integer maximumWaterDepthMeters,
    List<String> licenceBlocks
) {

  static InfrastructureProjectLocationJson from(
      ProjectLocation projectLocation,
      List<ProjectLocationBlock> projectLocationBlocks
  ) {
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
        field,
        maximumWaterDepthMeters,
        licenceBlocks
    );
  }
}
