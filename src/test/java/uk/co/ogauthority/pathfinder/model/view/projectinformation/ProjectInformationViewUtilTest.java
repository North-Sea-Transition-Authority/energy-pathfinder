package uk.co.ogauthority.pathfinder.model.view.projectinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
class ProjectInformationViewUtilTest {

  private ProjectInformation projectInformation;

  @BeforeEach
  void setup() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
  }

  private void assertCommonProperties(ProjectInformationView projectInformationView,
                                      ProjectInformation projectInformation) {
    assertThat(projectInformationView.getProjectTitle()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(projectInformationView.getProjectSummary()).isEqualTo(projectInformation.getProjectSummary());
    assertThat(projectInformationView.getContactName()).isEqualTo(projectInformation.getContactName());
    assertThat(projectInformationView.getContactPhoneNumber()).isEqualTo(projectInformation.getPhoneNumber());
    assertThat(projectInformationView.getContactJobTitle()).isEqualTo(projectInformation.getJobTitle());
    assertThat(projectInformationView.getContactEmailAddress()).isEqualTo(projectInformation.getEmailAddress());
  }

  @Test
  void from_whenNoFieldStage() {

    projectInformation.setFieldStage(null);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEmpty();
  }

  @Test
  void from_whenDevelopmentFieldStageSubCategory() {
    final var fieldStage = FieldStage.OIL_AND_GAS;
    final var fieldStageSubCategory = FieldStageSubCategory.DEVELOPMENT;
    final var firstProductionQuarter = Quarter.Q1;
    final var firstProductionYear = 2020;

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(fieldStageSubCategory);
    projectInformation.setFirstProductionDateQuarter(firstProductionQuarter);
    projectInformation.setFirstProductionDateYear(firstProductionYear);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage())
        .isEqualTo("%s: %s".formatted(fieldStage.getDisplayName(), fieldStageSubCategory.getDisplayName()));

    var expectedFirstProductionDate = DateUtil.getDateFromQuarterYear(
        firstProductionQuarter,
        firstProductionYear
    );
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isEqualTo(expectedFirstProductionDate);
  }

  @Test
  void from_whenDiscoveryFieldStageSubCategory() {
    final var fieldStage = FieldStage.OIL_AND_GAS;
    final var fieldStageSubCategory = FieldStageSubCategory.DISCOVERY;
    final var firstProductionQuarter = Quarter.Q1;
    final var firstProductionYear = 2020;

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(fieldStageSubCategory);

    // should not be populated into view
    projectInformation.setFirstProductionDateQuarter(firstProductionQuarter);
    projectInformation.setFirstProductionDateYear(firstProductionYear);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage())
        .isEqualTo("%s: %s".formatted(fieldStage.getDisplayName(), fieldStageSubCategory.getDisplayName()));

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = FieldStageSubCategory.class, names = {"DISCOVERY", "DECOMMISSIONING"}, mode = EnumSource.Mode.INCLUDE)
  void from_FieldStageSubcategoryWithoutHiddenFields(FieldStageSubCategory fieldStageSubCategory) {
    final var fieldStage = FieldStage.OIL_AND_GAS;
    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(fieldStageSubCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage())
        .isEqualTo("%s: %s".formatted(fieldStage.getDisplayName(), fieldStageSubCategory.getDisplayName()));

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @Test
  void from_whenCarbonCaptureAndStorageFieldStage() {
    final var fieldStage = FieldStage.CARBON_CAPTURE_AND_STORAGE;
    final var subCategory = FieldStageSubCategory.TRANSPORTATION_AND_STORAGE;

    var expectedFieldStage = String.format("%s: %s", fieldStage.getDisplayName(), subCategory.getDisplayName());

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(subCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(expectedFieldStage);

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = FieldStage.class)
  void from_whenFieldStageWithoutSubCategorySet(FieldStage fieldStage) {
    var expectedFieldStage = fieldStage.getDisplayName();
    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(null);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(expectedFieldStage);

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @Test
  void from_whenHydrogenFieldStage() {
    final var fieldStage = FieldStage.HYDROGEN;
    final var subCategory = FieldStageSubCategory.OFFSHORE_HYDROGEN;

    var expectedFieldStage = String.format("%s: %s", fieldStage.getDisplayName(), subCategory.getDisplayName());

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(subCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(expectedFieldStage);

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @Test
  void from_whenElectrificationFieldStage() {
    final var fieldStage = FieldStage.ELECTRIFICATION;
    final var subCategory = FieldStageSubCategory.ONSHORE_ELECTRIFICATION;

    var expectedFieldStage = String.format("%s: %s", fieldStage.getDisplayName(), subCategory.getDisplayName());

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(subCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(expectedFieldStage);

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @Test
  void from_whenWindEnergyFieldStage() {
    final var fieldStage = FieldStage.WIND_ENERGY;
    final var subCategory = FieldStageSubCategory.FLOATING_OFFSHORE_WIND;

    var expectedFieldStage = String.format("%s: %s", fieldStage.getDisplayName(), subCategory.getDisplayName());

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(subCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(expectedFieldStage);

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @Test
  void from_whenFieldStageWithNoHiddenContent() {
    final var fieldStage = FieldStage.OIL_AND_GAS;
    final var subCategory = FieldStageSubCategory.DECOMMISSIONING;

    var expectedFieldStage = String.format("%s: %s", fieldStage.getDisplayName(), subCategory.getDisplayName());

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFieldStageSubCategory(subCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(expectedFieldStage);
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }
  
  @Test
  void from_whenProjectInformationHasNullFields_thenHandleGracefully() {
      // Setup project information with null fields
      ProjectInformation incompleteInfo = new ProjectInformation();
      incompleteInfo.setFieldStage(FieldStage.OIL_AND_GAS);
      incompleteInfo.setFieldStageSubCategory(FieldStageSubCategory.DISCOVERY);
      // All other fields remain null
  
      // Execute
      ProjectInformationView view = ProjectInformationViewUtil.from(incompleteInfo);
  
      // Verify
      assertThat(view.getProjectTitle()).isNull();
      assertThat(view.getProjectSummary()).isNull();
      assertThat(view.getContactName()).isNull();
      assertThat(view.getContactPhoneNumber()).isNull();
      assertThat(view.getContactJobTitle()).isNull();
      assertThat(view.getContactEmailAddress()).isNull();
      assertThat(view.getFieldStage()).isEqualTo("Oil and Gas: Discovery");
      assertThat(view.getDevelopmentFirstProductionDate()).isNull();
  }
  
  @Test
  void from_whenFieldStageAndSubCategoryBothNull_thenEmptyFieldStage() {
      // Setup
      projectInformation.setFieldStage(null);
      projectInformation.setFieldStageSubCategory(null);
  
      // Execute
      ProjectInformationView view = ProjectInformationViewUtil.from(projectInformation);
  
      // Verify
      assertThat(view.getFieldStage()).isEmpty();
      assertThat(view.getDevelopmentFirstProductionDate()).isNull();
      // Verify other fields are still populated
      assertThat(view.getProjectTitle()).isEqualTo(projectInformation.getProjectTitle());
      assertThat(view.getProjectSummary()).isEqualTo(projectInformation.getProjectSummary());
      assertThat(view.getContactName()).isEqualTo(projectInformation.getContactName());
      assertThat(view.getContactPhoneNumber()).isEqualTo(projectInformation.getPhoneNumber());
      assertThat(view.getContactJobTitle()).isEqualTo(projectInformation.getJobTitle());
      assertThat(view.getContactEmailAddress()).isEqualTo(projectInformation.getEmailAddress());
  }
  
  @Test
  void from_whenFieldStageSetButNoFirstProductionDateForDevelopment() {
      // Setup
      projectInformation.setFieldStage(FieldStage.OIL_AND_GAS);
      projectInformation.setFieldStageSubCategory(FieldStageSubCategory.DEVELOPMENT);
      projectInformation.setFirstProductionDateQuarter(null);
      projectInformation.setFirstProductionDateYear(null);
  
      // Execute
      ProjectInformationView view = ProjectInformationViewUtil.from(projectInformation);
  
      // Verify 
      assertThat(view.getFieldStage()).isEqualTo("Oil and Gas: Development");
      assertThat(view.getDevelopmentFirstProductionDate()).isEmpty(); // Should handle null dates
  }
}
