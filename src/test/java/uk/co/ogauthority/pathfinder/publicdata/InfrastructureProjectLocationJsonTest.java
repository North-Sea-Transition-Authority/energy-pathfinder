package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectLocationJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStage(FieldStage.OIL_AND_GAS);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DECOMMISSIONING);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.<ProjectLocationBlock>of();

    var infrastructureProjectLocationJson =
        InfrastructureProjectLocationJson.from(projectInformation, projectLocation, projectLocationBlocks);

    var expectedInfrastructureProjectLocationJson = new InfrastructureProjectLocationJson(
        CoordinateJson.from(
            projectLocation.getCentreOfInterestLatitudeDegrees(),
            projectLocation.getCentreOfInterestLatitudeMinutes(),
            projectLocation.getCentreOfInterestLatitudeSeconds(),
            projectLocation.getCentreOfInterestLatitudeHemisphere()
        ),
        CoordinateJson.from(
            projectLocation.getCentreOfInterestLongitudeDegrees(),
            projectLocation.getCentreOfInterestLongitudeMinutes(),
            projectLocation.getCentreOfInterestLongitudeSeconds(),
            projectLocation.getCentreOfInterestLongitudeHemisphere()
        ),
        InfrastructureProjectFieldJson.from(projectLocation),
        projectLocation.getMaximumWaterDepth(),
        List.of()
    );

    assertThat(infrastructureProjectLocationJson).isEqualTo(expectedInfrastructureProjectLocationJson);
  }

  @Test
  void from_fieldStageIsNotEnergyTransitionAndProjectLocationBlocksIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStage(FieldStage.OIL_AND_GAS);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DECOMMISSIONING);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var infrastructureProjectLocationJson =
        InfrastructureProjectLocationJson.from(projectInformation, projectLocation, null);

    assertThat(infrastructureProjectLocationJson.licenceBlocks()).isNull();
  }

  @Test
  void from_fieldStageIsNotEnergyTransitionAndProjectLocationBlocksIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStage(FieldStage.OIL_AND_GAS);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DECOMMISSIONING);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "1/1b", "1", "1", "b"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "1/1a", "1", "1", "a"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "10/1", "10", "1", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "10/2", "10", "2", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "100/1", "100", "1", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "2/1", "2", "1", "")
    );

    var infrastructureProjectLocationJson =
        InfrastructureProjectLocationJson.from(projectInformation, projectLocation, projectLocationBlocks);

    assertThat(infrastructureProjectLocationJson.licenceBlocks())
        .containsExactly("1/1a", "1/1b", "2/1", "10/1", "10/2", "100/1");
  }

  @Test
  void from_fieldStageIsEnergyTransition() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStage(FieldStage.ELECTRIFICATION);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var infrastructureProjectLocationJson =
        InfrastructureProjectLocationJson.from(projectInformation, projectLocation, null);

    assertThat(infrastructureProjectLocationJson.field()).isNull();
    assertThat(infrastructureProjectLocationJson.maximumWaterDepthMeters()).isNull();
    assertThat(infrastructureProjectLocationJson.licenceBlocks()).isNull();
  }
}
