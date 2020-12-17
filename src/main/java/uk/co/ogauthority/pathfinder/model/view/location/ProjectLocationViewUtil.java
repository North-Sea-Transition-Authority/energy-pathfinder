package uk.co.ogauthority.pathfinder.model.view.location;

import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectLocationViewUtil {

  private ProjectLocationViewUtil() {
    throw new IllegalStateException("ProjectLocationViewUtil is a util class and should not be instantiated");
  }

  public static ProjectLocationView from(ProjectLocation projectLocation, List<ProjectLocationBlock> projectLocationBlocks) {
    var projectLocationView = new ProjectLocationView();

    var field = projectLocation.getField() != null
        ? new StringWithTag(projectLocation.getField().getFieldName(), Tag.NONE)
        : new StringWithTag(projectLocation.getManualFieldName(), Tag.NOT_FROM_LIST);
    projectLocationView.setField(field);

    var fieldType = projectLocation.getFieldType() != null
        ? projectLocation.getFieldType().getDisplayName()
        : null;
    projectLocationView.setFieldType(fieldType);

    projectLocationView.setMaximumWaterDepth(projectLocation.getMaximumWaterDepth());
    projectLocationView.setApprovedFieldDevelopmentPlan(projectLocation.getApprovedFieldDevelopmentPlan());
    projectLocationView.setApprovedFdpDate(DateUtil.formatDate(projectLocation.getApprovedFdpDate()));
    projectLocationView.setApprovedDecomProgram(projectLocation.getApprovedDecomProgram());
    projectLocationView.setApprovedDecomProgramDate(DateUtil.formatDate(projectLocation.getApprovedDecomProgramDate()));

    var ukcsArea = projectLocation.getUkcsArea() != null
        ? projectLocation.getUkcsArea().getDisplayName()
        : null;
    projectLocationView.setUkcsArea(ukcsArea);

    projectLocationView.setLicenceBlocks(projectLocationBlocks.stream()
        .map(ProjectLocationBlock::getBlockReference)
        .collect(Collectors.toList()));

    return projectLocationView;
  }
}
