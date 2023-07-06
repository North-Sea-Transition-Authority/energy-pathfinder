package uk.co.ogauthority.pathfinder.model.view.projectinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
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
  public void from_whenNoFieldStage() {

    projectInformation.setFieldStage(null);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEmpty();
  }

  @Test
  public void from_whenDevelopmentFieldStage() {

    final var fieldStage = FieldStage.DEVELOPMENT;
    final var firstProductionQuarter = Quarter.Q1;
    final var firstProductionYear = 2020;

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setFirstProductionDateQuarter(firstProductionQuarter);
    projectInformation.setFirstProductionDateYear(firstProductionYear);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());

    var expectedFirstProductionDate = DateUtil.getDateFromQuarterYear(
        firstProductionQuarter,
        firstProductionYear
    );
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isEqualTo(expectedFirstProductionDate);
  }

  @Test
  public void from_whenDiscoveryFieldStage() {
    final var fieldStage = FieldStage.DISCOVERY;
    final var firstProductionQuarter = Quarter.Q1;
    final var firstProductionYear = 2020;

    projectInformation.setFieldStage(fieldStage);

    // should not be populated into view
    projectInformation.setFirstProductionDateQuarter(firstProductionQuarter);
    projectInformation.setFirstProductionDateYear(firstProductionYear);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @ParameterizedTest
  @EnumSource(value = FieldStage.class, names = {"DECOMMISSIONING", "HYDROGEN", "OFFSHORE_ELECTRIFICATION"}, mode = EnumSource.Mode.INCLUDE)
  void from_FieldStageWithoutHiddenFields(FieldStage fieldStage) {
    setup();
    projectInformation.setFieldStage(fieldStage);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());

    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }

  @Test
  public void from_whenCarbonCaptureAndStorageFieldStage() {
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

  @Test
  public void from_whenOffshoreWindFieldStage() {
    final var fieldStage = FieldStage.OFFSHORE_WIND;
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
  public void from_whenFieldStageWithNoHiddenContent() {
    final var fieldStage = FieldStage.DECOMMISSIONING;

    projectInformation.setFieldStage(fieldStage);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
  }
}
