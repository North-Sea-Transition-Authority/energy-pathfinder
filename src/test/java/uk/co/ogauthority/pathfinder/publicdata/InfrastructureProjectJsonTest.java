package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocationBlock;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldType;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.LicenceBlockTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

class InfrastructureProjectJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.<ProjectLocationBlock>of();

    var expectedInfrastructureProjectJson = new InfrastructureProjectJson(
        projectDetail.getProject().getId(),
        projectOperator.getOrganisationGroup().getName(),
        projectInformation.getProjectTitle(),
        projectInformation.getProjectSummary(),
        projectInformation.getFieldStage().name(),
        null,
        projectInformation.getContactName(),
        projectInformation.getPhoneNumber(),
        projectInformation.getJobTitle(),
        projectInformation.getEmailAddress(),
        null,
        projectInformation.getFirstProductionDateYear(),
        projectLocation.getField().getFieldName(),
        projectLocation.getFieldType().name(),
        projectLocation.getField().getUkcsArea().name(),
        projectLocation.getMaximumWaterDepth(),
        List.of(),
        LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault())
    );

    assertThat(
        InfrastructureProjectJson.from(
            projectDetail,
            projectOperator,
            projectInformation,
            projectLocation,
            projectLocationBlocks
        )
    ).isEqualTo(expectedInfrastructureProjectJson);
  }

  @Test
  void from_publishedAsOperatorIsFalse() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);
    projectOperator.setIsPublishedAsOperator(false);

    var organisationUnit =
        TeamTestingUtil.generateOrganisationUnit(2, "Test organisation unit name", null);
    projectOperator.setPublishableOrganisationUnit(organisationUnit);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.operatorName()).isEqualTo("Test organisation unit name");
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(booleans = true)
  void from_publishedAsOperatorIsNullOrTrue(Boolean publishedAsOperator) {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);
    projectOperator.setIsPublishedAsOperator(publishedAsOperator);

    var organisationGroup =
        TeamTestingUtil.generateOrganisationGroup(1, "Test organisation group name", "Test grp");
    projectOperator.setOrganisationGroup(organisationGroup);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.operatorName()).isEqualTo("Test organisation group name");
  }

  @Test
  void from_fieldStageSubCategoryIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStageSubCategory(null);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.fieldStageSubCategory()).isNull();
  }

  @Test
  void from_fieldStageSubCategoryIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.fieldStageSubCategory())
        .isEqualTo(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND.name());
  }

  @Test
  void from_firstProductionDateQuarterIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFirstProductionDateQuarter(null);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDateQuarter()).isNull();
  }

  @Test
  void from_firstProductionDateQuarterIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFirstProductionDateQuarter(Quarter.Q1);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson.firstProductionDateQuarter()).isEqualTo(Quarter.Q1.name());
  }

  @Test
  void from_projectLocationIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        null,
        null
    );

    assertThat(infrastructureProjectJson)
        .extracting(
            InfrastructureProjectJson::fieldName,
            InfrastructureProjectJson::fieldType,
            InfrastructureProjectJson::ukcsArea,
            InfrastructureProjectJson::maximumWaterDepthMeters,
            InfrastructureProjectJson::licenceBlocks
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void from_projectLocationIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var field = DevUkTestUtil.getDevUkField(1, "Test field", 2, null);
    projectLocation.setField(field);
    projectLocation.setFieldType(FieldType.GAS);
    projectLocation.setMaximumWaterDepth(100);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        null
    );

    assertThat(infrastructureProjectJson)
        .extracting(
            InfrastructureProjectJson::fieldName,
            InfrastructureProjectJson::fieldType,
            InfrastructureProjectJson::ukcsArea,
            InfrastructureProjectJson::maximumWaterDepthMeters,
            InfrastructureProjectJson::licenceBlocks
        )
        .containsExactly(
            "Test field",
            FieldType.GAS.name(),
            null,
            100,
            null
        );
  }

  @Test
  void from_projectLocationIsNotNullAndUkcsAreaIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var field = DevUkTestUtil.getDevUkField(1, "Test field", 2, null);
    projectLocation.setField(field);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        null
    );

    assertThat(infrastructureProjectJson.ukcsArea()).isNull();
  }

  @Test
  void from_projectLocationIsNotNullAndUkcsAreaIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var field = DevUkTestUtil.getDevUkField(1, "Test field", 2, UkcsArea.CNS);
    projectLocation.setField(field);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        null
    );

    assertThat(infrastructureProjectJson.ukcsArea()).isEqualTo(UkcsArea.CNS.name());
  }

  @Test
  void from_projectLocationIsNotNullAndProjectLocationBlocksIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        null
    );

    assertThat(infrastructureProjectJson.licenceBlocks()).isNull();
  }

  @Test
  void from_projectLocationIsNotNullAndLicenceBlocksIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var projectLocationBlocks = List.of(
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "1/1b", "1", "1", "b"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "1/1a", "1", "1", "a"),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "10/1", "10", "1", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "10/2", "10", "2", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "100/1", "100", "1", ""),
        LicenceBlockTestUtil.getProjectLocationBlock(projectLocation, "2/1", "2", "1", "")
    );

    var infrastructureProjectJson = InfrastructureProjectJson.from(
        projectDetail,
        projectOperator,
        projectInformation,
        projectLocation,
        projectLocationBlocks
    );

    assertThat(infrastructureProjectJson.licenceBlocks()).containsExactly("1/1a", "1/1b", "2/1", "10/1", "10/2", "100/1");
  }
}
