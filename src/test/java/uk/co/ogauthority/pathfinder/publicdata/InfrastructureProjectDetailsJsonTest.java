package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

class InfrastructureProjectDetailsJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    var infrastructureProjectDetailsJson = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    var expectedInfrastructureProjectDetailsJson = new InfrastructureProjectDetailsJson(
        projectOperator.getOrganisationGroup().getName(),
        projectInformation.getProjectTitle(),
        projectInformation.getProjectSummary(),
        projectInformation.getFieldStage().name(),
        projectInformation.getFieldStageSubCategory().name()
    );

    assertThat(infrastructureProjectDetailsJson).isEqualTo(expectedInfrastructureProjectDetailsJson);
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

    var infrastructureProjectDetailsJson = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    assertThat(infrastructureProjectDetailsJson.operatorName()).isEqualTo("Test organisation unit name");
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

    var infrastructureProjectDetailsJson = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    assertThat(infrastructureProjectDetailsJson.operatorName()).isEqualTo("Test organisation group name");
  }

  @Test
  void from_projectTypeSubCategoryIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStageSubCategory(null);

    var infrastructureProjectDetailsJson = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    assertThat(infrastructureProjectDetailsJson.projectTypeSubCategory()).isNull();
  }

  @Test
  void from_projectTypeSubCategoryIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    projectInformation.setFieldStageSubCategory(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND);

    var infrastructureProjectDetailsJson = InfrastructureProjectDetailsJson.from(projectOperator, projectInformation);

    assertThat(infrastructureProjectDetailsJson.projectTypeSubCategory())
        .isEqualTo(FieldStageSubCategory.FIXED_BOTTOM_OFFSHORE_WIND.name());
  }
}
