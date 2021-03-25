package uk.co.ogauthority.pathfinder.model.view.projectinformation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.project.EnergyTransitionCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationViewUtilTest {

  private ProjectInformation projectInformation;

  @Before
  public void setup() {
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
    assertThat(projectInformationView.getDiscoveryFirstProductionDate()).isNull();
    assertThat(projectInformationView.getEnergyTransitionCategory()).isNull();
  }

  @Test
  public void from_whenDiscoveryFieldStage() {
    final var fieldStage = FieldStage.DISCOVERY;
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
    assertThat(projectInformationView.getDiscoveryFirstProductionDate()).isEqualTo(expectedFirstProductionDate);
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
    assertThat(projectInformationView.getEnergyTransitionCategory()).isNull();
  }

  @Test
  public void from_whenDecommissioningFieldStage() {

    final var fieldStage = FieldStage.DECOMMISSIONING;

    projectInformation.setFieldStage(fieldStage);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());

    assertThat(projectInformationView.getDiscoveryFirstProductionDate()).isNull();
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
    assertThat(projectInformationView.getEnergyTransitionCategory()).isNull();
  }

  @Test
  public void from_whenEnergyTransitionFieldStage() {

    final var fieldStage = FieldStage.ENERGY_TRANSITION;

    final var energyTransitionCategory = EnergyTransitionCategory.HYDROGEN;

    projectInformation.setFieldStage(fieldStage);
    projectInformation.setEnergyTransitionCategory(energyTransitionCategory);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());

    assertThat(projectInformationView.getEnergyTransitionCategory()).isEqualTo(energyTransitionCategory.getDisplayName());
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
    assertThat(projectInformationView.getDiscoveryFirstProductionDate()).isNull();
  }

  @Test
  public void from_whenFieldStageWithNoHiddenContent() {

    final var fieldStage = FieldStage.DECOMMISSIONING;

    projectInformation.setFieldStage(fieldStage);

    var projectInformationView = ProjectInformationViewUtil.from(projectInformation);

    assertCommonProperties(projectInformationView, projectInformation);
    assertThat(projectInformationView.getFieldStage()).isEqualTo(fieldStage.getDisplayName());
    assertThat(projectInformationView.getDevelopmentFirstProductionDate()).isNull();
    assertThat(projectInformationView.getDiscoveryFirstProductionDate()).isNull();
    assertThat(projectInformationView.getEnergyTransitionCategory()).isNull();
  }
}