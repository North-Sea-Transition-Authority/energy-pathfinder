package uk.co.ogauthority.pathfinder.service.projectmanagement.heading.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorDisplayNameUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureProjectManagementHeadingSectionServiceTest {

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  private InfrastructureProjectManagementHeadingSectionService infrastructureProjectManagementHeadingSectionService;

  @Before
  public void setup() {
    infrastructureProjectManagementHeadingSectionService = new InfrastructureProjectManagementHeadingSectionService(
        projectInformationService,
        projectOperatorService
    );
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    assertThat(infrastructureProjectManagementHeadingSectionService.getSupportedProjectType()).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getHeadingText_assertCorrectHeadingText() {

    final var projectDetails = ProjectUtil.getProjectDetails();

    final var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetails);

    when(projectInformationService.getProjectInformationOrError(projectDetails)).thenReturn(projectInformation);

    final var expectedHeadingText = String.format(
        "%s: %s",
        ProjectType.INFRASTRUCTURE.getDisplayName(),
        projectInformation.getProjectTitle()
    );

    final var resultingHeadingText = infrastructureProjectManagementHeadingSectionService.getHeadingText(
        projectDetails
    );

    assertThat(resultingHeadingText).isEqualTo(expectedHeadingText);
  }

  @Test
  public void getCaptionText_whenPublishedAsOperator_thenCaptionTextIsGroupName() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    projectOperator.setIsPublishedAsOperator(true);

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    final var resultingCaptionText = infrastructureProjectManagementHeadingSectionService.getCaptionText(projectDetail);

    assertThat(resultingCaptionText).isEqualTo(projectOperator.getOrganisationGroup().getName());
  }

  @Test
  public void getCaptionText_whenNotPublishedAsOperator_thenCaptionTextIsGroupAndOrganisationName() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    projectOperator.setIsPublishedAsOperator(false);

    var publishableOrganisation = TeamTestingUtil.generateOrganisationUnit(
        100,
        "organisation name",
        projectOperator.getOrganisationGroup()
    );

    projectOperator.setPublishableOrganisationUnit(publishableOrganisation);

    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    var expectedCaptionText = ProjectOperatorDisplayNameUtil.getProjectOperatorDisplayName(
        projectOperator.getOrganisationGroup(),
        projectOperator.getPublishableOrganisationUnit()
    );

    final var resultingCaptionText = infrastructureProjectManagementHeadingSectionService.getCaptionText(projectDetail);

    assertThat(resultingCaptionText).isEqualTo(expectedCaptionText);
  }

}