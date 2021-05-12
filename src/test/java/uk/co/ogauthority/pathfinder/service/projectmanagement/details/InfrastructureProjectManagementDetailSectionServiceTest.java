package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.InfrastructureProjectManagementDetailView;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.InfrastructureProjectManagementDetailViewUtil;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureProjectManagementDetailSectionServiceTest {

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectLocationService projectLocationService;

  @Mock
  private WebUserAccountService webUserAccountService;

  public static final ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails();
  public static final ProjectInformation PROJECT_INFORMATION = ProjectInformationUtil.getProjectInformation_withCompleteDetails(PROJECT_DETAIL);
  public static final WebUserAccount USER_ACCOUNT = UserTestingUtil.getAuthenticatedUserAccount();

  private InfrastructureProjectManagementDetailSectionService infrastructureProjectManagementDetailSectionService;

  @Before
  public void setup() {
    infrastructureProjectManagementDetailSectionService = new InfrastructureProjectManagementDetailSectionService(
        projectInformationService,
        projectLocationService,
        webUserAccountService
    );
  }

  @Test
  public void getSupportedProjectType_assertReturnValue() {
    final var resultingProjectType = infrastructureProjectManagementDetailSectionService.getSupportedProjectType();
    assertThat(resultingProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getTemplatePath_assertReturnValue() {
    final var resultingTemplatePath = infrastructureProjectManagementDetailSectionService.getTemplatePath();
    assertThat(resultingTemplatePath).isEqualTo(InfrastructureProjectManagementDetailSectionService.TEMPLATE_PATH);
  }

  @Test
  public void getManagementDetailView_whenNotEnergyTransition_assertDevUkFieldIsProvided() {

    final var isEnergyTransitionProject = false;
    when(projectInformationService.isEnergyTransitionProject(PROJECT_INFORMATION)).thenReturn(isEnergyTransitionProject);

    final var projectLocation = ProjectLocationTestUtil.getProjectLocation(PROJECT_DETAIL);
    when(projectLocationService.getOrError(PROJECT_DETAIL)).thenReturn(projectLocation);

    final var expectedProjectManagementDetailView = InfrastructureProjectManagementDetailViewUtil.from(
        PROJECT_DETAIL,
        PROJECT_INFORMATION.getFieldStage(),
        projectLocation.getField(),
        isEnergyTransitionProject,
        USER_ACCOUNT
    );

    assertExpectedProjectManagementDetailView(expectedProjectManagementDetailView);
  }

  @Test
  public void getManagementDetailView_whenEnergyTransition_assertDevUkFieldIsNotProvided() {

    final var isEnergyTransitionProject = true;
    when(projectInformationService.isEnergyTransitionProject(any(ProjectInformation.class))).thenReturn(isEnergyTransitionProject);

    final var expectedProjectManagementDetailView = InfrastructureProjectManagementDetailViewUtil.from(
        PROJECT_DETAIL,
        PROJECT_INFORMATION.getFieldStage(),
        null,
        isEnergyTransitionProject,
        USER_ACCOUNT
    );

    assertExpectedProjectManagementDetailView(expectedProjectManagementDetailView);
  }

  private void assertExpectedProjectManagementDetailView(InfrastructureProjectManagementDetailView expectedProjectManagementDetailView) {

    when(projectInformationService.getProjectInformationOrError(PROJECT_DETAIL)).thenReturn(PROJECT_INFORMATION);

    when(webUserAccountService.getWebUserAccountOrError(any())).thenReturn(USER_ACCOUNT);

    final var resultingProjectManagementDetailView =
        (InfrastructureProjectManagementDetailView) infrastructureProjectManagementDetailSectionService.getManagementDetailView(PROJECT_DETAIL);

    assertThat(resultingProjectManagementDetailView).isEqualTo(expectedProjectManagementDetailView);
  }

}