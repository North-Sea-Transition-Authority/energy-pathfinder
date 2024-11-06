package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectLocationJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();
    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);
    var projectLocationBlocks = List.<ProjectLocationBlock>of();

    var infrastructureProjectLocationJson = InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks);

    var expectedInfrastructureProjectLocationJson = new InfrastructureProjectLocationJson(
        InfrastructureProjectFieldJson.from(projectLocation),
        projectLocation.getMaximumWaterDepth(),
        List.of()
    );

    assertThat(infrastructureProjectLocationJson).isEqualTo(expectedInfrastructureProjectLocationJson);
  }

  @Test
  void from_projectLocationBlocksIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var infrastructureProjectLocationJson = InfrastructureProjectLocationJson.from(projectLocation, null);

    assertThat(infrastructureProjectLocationJson.licenceBlocks()).isNull();
  }

  @Test
  void from_projectLocationBlocksIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "1/1b", "1", "1", "b"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "1/1a", "1", "1", "a"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "10/1", "10", "1", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "10/2", "10", "2", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "100/1", "100", "1", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "2/1", "2", "1", "")
    );

    var infrastructureProjectLocationJson = InfrastructureProjectLocationJson.from(projectLocation, projectLocationBlocks);

    assertThat(infrastructureProjectLocationJson.licenceBlocks())
        .containsExactly("1/1a", "1/1b", "2/1", "10/1", "10/2", "100/1");
  }
}
