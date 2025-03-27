package uk.co.ogauthority.pathfinder.model.view.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.CoordinateUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationViewUtilTest {

  private final ProjectDetail projectDetails = ProjectUtil.getProjectDetails();

  private void checkCommonFields(
      ProjectLocationView projectLocationView,
      ProjectLocation projectLocation
  ) {
    assertThat(projectLocationView.getCentreOfInterestLatitude())
        .isEqualTo(CoordinateUtil.formatCoordinate(
            projectLocation.getCentreOfInterestLatitudeDegrees(),
            projectLocation.getCentreOfInterestLatitudeMinutes(),
            projectLocation.getCentreOfInterestLatitudeSeconds(),
            projectLocation.getCentreOfInterestLatitudeHemisphere()));
    assertThat(projectLocationView.getCentreOfInterestLongitude())
        .isEqualTo(CoordinateUtil.formatCoordinate(
            projectLocation.getCentreOfInterestLongitudeDegrees(),
            projectLocation.getCentreOfInterestLongitudeMinutes(),
            projectLocation.getCentreOfInterestLongitudeSeconds(),
            projectLocation.getCentreOfInterestLongitudeHemisphere()));
  }

  private void checkOilAndGasFields(
      ProjectLocationView projectLocationView,
      ProjectLocation projectLocation,
      List<ProjectLocationBlock> projectLocationBlocks
  ) {
    assertThat(projectLocationView.getFieldType()).isEqualTo(projectLocation.getFieldType().getDisplayName());
    assertThat(projectLocationView.getMaximumWaterDepth()).isEqualTo(ProjectLocationViewUtil.getWaterDepthString(projectLocation.getMaximumWaterDepth()));
    assertThat(projectLocationView.getApprovedFieldDevelopmentPlan()).isEqualTo(projectLocation.getApprovedFieldDevelopmentPlan());
    assertThat(projectLocationView.getApprovedFdpDate()).isEqualTo(DateUtil.formatDate(projectLocation.getApprovedFdpDate()));
    assertThat(projectLocationView.getApprovedDecomProgram()).isEqualTo(projectLocation.getApprovedDecomProgram());
    assertThat(projectLocationView.getApprovedDecomProgramDate()).isEqualTo(DateUtil.formatDate(projectLocation.getApprovedDecomProgramDate()));
    assertThat(projectLocationView.getLicenceBlocks()).containsExactlyElementsOf(projectLocationBlocks.stream()
        .map(ProjectLocationBlock::getBlockReference)
        .collect(Collectors.toList()));
  }

  @Test
  public void from_withEmptyLocation() {
    var projectLocation = new ProjectLocation();
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, true, Collections.emptyList());

    assertThat(projectLocationView.getFieldType()).isNull();
    assertThat(projectLocationView.getMaximumWaterDepth()).isEmpty();
    assertThat(projectLocationView.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(projectLocationView.getApprovedFdpDate()).isEmpty();
    assertThat(projectLocationView.getApprovedDecomProgram()).isNull();
    assertThat(projectLocationView.getApprovedDecomProgramDate()).isEmpty();
    assertThat(projectLocationView.getUkcsArea()).isEqualTo(ProjectLocationViewUtil.UKCS_AREA_NOT_SET_MESSAGE);
    assertThat(projectLocationView.getLicenceBlocks()).isEmpty();
  }

  @Test
  public void from_withFieldFromListWithNotNullUkcsArea() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetails);
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, true, Collections.emptyList());

    assertThat(projectLocationView.getField()).isEqualTo(projectLocation.getField().getFieldName());
    assertThat(projectLocationView.getUkcsArea()).isEqualTo(projectLocation.getField().getUkcsArea().getDisplayName());

    checkCommonFields(projectLocationView, projectLocation);
    checkOilAndGasFields(projectLocationView, projectLocation, Collections.emptyList());
  }

  @Test
  public void from_withFieldFromListWithNullUkcsArea() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetails);
    var field = DevUkTestUtil.getDevUkField();
    field.setUkcsArea(null);
    projectLocation.setField(field);
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, true, Collections.emptyList());

    assertThat(projectLocationView.getField()).isEqualTo(projectLocation.getField().getFieldName());
    assertThat(projectLocationView.getUkcsArea()).isEqualTo(ProjectLocationViewUtil.UKCS_AREA_NOT_SET_MESSAGE);

    checkCommonFields(projectLocationView, projectLocation);
    checkOilAndGasFields(projectLocationView, projectLocation, Collections.emptyList());
  }

  @Test
  public void from_notOilAndGasProject() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetails);
    var field = DevUkTestUtil.getDevUkField();
    field.setUkcsArea(null);
    projectLocation.setField(field);
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, false, Collections.emptyList());

    assertThat(projectLocationView.getField()).isNull();
    assertThat(projectLocationView.getUkcsArea()).isNull();

    checkCommonFields(projectLocationView, projectLocation);

    assertThat(projectLocationView.getFieldType()).isNull();
    assertThat(projectLocationView.getMaximumWaterDepth()).isNull();
    assertThat(projectLocationView.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(projectLocationView.getApprovedFdpDate()).isNull();
    assertThat(projectLocationView.getApprovedDecomProgram()).isNull();
    assertThat(projectLocationView.getApprovedDecomProgramDate()).isNull();
    assertThat(projectLocationView.getLicenceBlocks()).isEmpty();
  }
}
