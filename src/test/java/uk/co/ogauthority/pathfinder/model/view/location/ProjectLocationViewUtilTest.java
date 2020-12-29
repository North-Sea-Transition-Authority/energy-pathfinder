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
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationViewUtilTest {

  private final ProjectDetail projectDetails = ProjectUtil.getProjectDetails();

  private void checkCommonFields(ProjectLocationView projectLocationView,
                                 ProjectLocation projectLocation,
                                 List<ProjectLocationBlock> projectLocationBlocks) {
    assertThat(projectLocationView.getFieldType()).isEqualTo(projectLocation.getFieldType().getDisplayName());
    assertThat(projectLocationView.getMaximumWaterDepth()).isEqualTo(ProjectLocationViewUtil.getWaterDepthString(projectLocation.getMaximumWaterDepth()));
    assertThat(projectLocationView.getApprovedFieldDevelopmentPlan()).isEqualTo(projectLocation.getApprovedFieldDevelopmentPlan());
    assertThat(projectLocationView.getApprovedFdpDate()).isEqualTo(DateUtil.formatDate(projectLocation.getApprovedFdpDate()));
    assertThat(projectLocationView.getApprovedDecomProgram()).isEqualTo(projectLocation.getApprovedDecomProgram());
    assertThat(projectLocationView.getApprovedDecomProgramDate()).isEqualTo(DateUtil.formatDate(projectLocation.getApprovedDecomProgramDate()));
    assertThat(projectLocationView.getUkcsArea()).isEqualTo(projectLocation.getUkcsArea().getDisplayName());
    assertThat(projectLocationView.getLicenceBlocks()).containsExactlyElementsOf(projectLocationBlocks.stream()
      .map(ProjectLocationBlock::getBlockReference)
      .collect(Collectors.toList()));
  }

  @Test
  public void from_withEmptyLocation() {
    var projectLocation = new ProjectLocation();
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, Collections.emptyList());

    assertThat(projectLocationView.getFieldType()).isNull();
    assertThat(projectLocationView.getMaximumWaterDepth()).isEmpty();
    assertThat(projectLocationView.getApprovedFieldDevelopmentPlan()).isNull();
    assertThat(projectLocationView.getApprovedFdpDate()).isEmpty();
    assertThat(projectLocationView.getApprovedDecomProgram()).isNull();
    assertThat(projectLocationView.getApprovedDecomProgramDate()).isEmpty();
    assertThat(projectLocationView.getUkcsArea()).isNull();
    assertThat(projectLocationView.getLicenceBlocks()).isEmpty();
  }

  @Test
  public void from_withFieldFromList() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withField(projectDetails);
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, Collections.emptyList());

    var field = projectLocationView.getField();
    assertThat(field.getValue()).isEqualTo(projectLocation.getField().getFieldName());
    assertThat(field.getTag()).isEqualTo(Tag.NONE);

    checkCommonFields(projectLocationView, projectLocation, Collections.emptyList());
  }

  @Test
  public void from_withManualField() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withManualField(projectDetails);
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, Collections.emptyList());

    var field = projectLocationView.getField();
    assertThat(field.getValue()).isEqualTo(projectLocation.getManualFieldName());
    assertThat(field.getTag()).isEqualTo(Tag.NOT_FROM_LIST);

    checkCommonFields(projectLocationView, projectLocation, Collections.emptyList());
  }
}
