package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;

record InfrastructureProjectFieldJson(
    String fieldName,
    String fieldType,
    String ukcsArea
) {

  static InfrastructureProjectFieldJson from(ProjectLocation projectLocation) {
    var fieldName = projectLocation.getField().getFieldName();
    var fieldType = projectLocation.getFieldType().name();
    var ukcsArea = projectLocation.getField().getUkcsArea() != null
        ? projectLocation.getField().getUkcsArea().name()
        : null;

    return new InfrastructureProjectFieldJson(fieldName, fieldType, ukcsArea);
  }
}
