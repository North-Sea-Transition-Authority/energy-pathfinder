package uk.co.ogauthority.pathfinder.model.view.location;

import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.MeasurementUnits;
import uk.co.ogauthority.pathfinder.util.CoordinateUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectLocationViewUtil {

  public static final String UKCS_AREA_NOT_SET_MESSAGE = "Not set in DEVUK";

  private ProjectLocationViewUtil() {
    throw new IllegalStateException("ProjectLocationViewUtil is a util class and should not be instantiated");
  }

  public static ProjectLocationView from(ProjectLocation projectLocation, List<ProjectLocationBlock> projectLocationBlocks) {
    var projectLocationView = new ProjectLocationView();

    projectLocationView.setCentreOfInterestLatitude(
        CoordinateUtil.formatCoordinate(
            projectLocation.getCentreOfInterestLatitudeDegrees(),
            projectLocation.getCentreOfInterestLatitudeMinutes(),
            projectLocation.getCentreOfInterestLatitudeSeconds(),
            projectLocation.getCentreOfInterestLatitudeHemisphere()));

    projectLocationView.setCentreOfInterestLongitude(
        CoordinateUtil.formatCoordinate(
            projectLocation.getCentreOfInterestLongitudeDegrees(),
            projectLocation.getCentreOfInterestLongitudeMinutes(),
            projectLocation.getCentreOfInterestLongitudeSeconds(),
            projectLocation.getCentreOfInterestLongitudeHemisphere()));

    var field = projectLocation.getField();

    var fieldName = field != null
        ? field.getFieldName()
        : null;
    projectLocationView.setField(fieldName);

    var ukcsArea = field != null && field.getUkcsArea() != null
        ? field.getUkcsArea().getDisplayName()
        : UKCS_AREA_NOT_SET_MESSAGE;
    projectLocationView.setUkcsArea(ukcsArea);

    var fieldType = projectLocation.getFieldType() != null
        ? projectLocation.getFieldType().getDisplayName()
        : null;
    projectLocationView.setFieldType(fieldType);

    projectLocationView.setMaximumWaterDepth(projectLocation.getMaximumWaterDepth() != null
        ? getWaterDepthString(projectLocation.getMaximumWaterDepth())
        : "");
    projectLocationView.setApprovedFieldDevelopmentPlan(projectLocation.getApprovedFieldDevelopmentPlan());
    projectLocationView.setApprovedFdpDate(DateUtil.formatDate(projectLocation.getApprovedFdpDate()));
    projectLocationView.setApprovedDecomProgram(projectLocation.getApprovedDecomProgram());
    projectLocationView.setApprovedDecomProgramDate(DateUtil.formatDate(projectLocation.getApprovedDecomProgramDate()));

    projectLocationView.setLicenceBlocks(projectLocationBlocks.stream()
        .map(ProjectLocationBlock::getBlockReference)
        .collect(Collectors.toList()));

    return projectLocationView;
  }

  public static String getWaterDepthString(Integer waterDepth) {
    return String.format("%d %s", waterDepth, MeasurementUnits.METRES.getPlural());
  }
}
