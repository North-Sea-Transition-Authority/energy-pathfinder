package uk.co.ogauthority.pathfinder.model.view.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
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
    assertThat(projectLocationView.getWaterDepth()).isEqualTo(projectLocation.getWaterDepth());
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
  public void from_withFieldFromList() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withField(projectDetails);
    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/34a"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/34b")
    );
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, projectLocationBlocks);

    var field = projectLocationView.getField();
    assertThat(field.getValue()).isEqualTo(projectLocation.getField().getFieldName());
    assertThat(field.getTag()).isEqualTo(Tag.NONE);

    checkCommonFields(projectLocationView, projectLocation, projectLocationBlocks);
  }

  @Test
  public void from_withManualField() {
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withManualField(projectDetails);
    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/34a"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "12/34b")
    );
    var projectLocationView = ProjectLocationViewUtil.from(projectLocation, projectLocationBlocks);

    var field = projectLocationView.getField();
    assertThat(field.getValue()).isEqualTo(projectLocation.getManualFieldName());
    assertThat(field.getTag()).isEqualTo(Tag.NOT_FROM_PORTAL);

    checkCommonFields(projectLocationView, projectLocation, projectLocationBlocks);
  }
}
