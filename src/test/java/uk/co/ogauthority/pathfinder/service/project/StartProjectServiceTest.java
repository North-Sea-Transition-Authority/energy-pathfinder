package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectRepository;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class StartProjectServiceTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectOperatorService projectOperatorService;

  private StartProjectService startProjectService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  @Before
  public void setUp() throws Exception {
    startProjectService = new StartProjectService(
        projectRepository,
        projectDetailsRepository,
        projectOperatorService
    );
  }

  @Test
  public void createInfrastructureProject_correctCalls() {

    final var projectOperatorForm = new ProjectOperatorForm();

    final var projectDetails = startProjectService.createInfrastructureProject(authenticatedUser, projectOperatorForm);
    verify(projectRepository, times(1)).save(any());
    verify(projectDetailsRepository, times(1)).save(any());
    verify(projectOperatorService, times(1)).createOrUpdateProjectOperator(projectDetails, projectOperatorForm);
  }

  @Test
  public void createInfrastructureProject_correctDetails() {

    final var projectOperatorForm = new ProjectOperatorForm();

    final var projectDetails = startProjectService.createInfrastructureProject(authenticatedUser, projectOperatorForm);
    assertThat(projectDetails.getCreatedByWua()).isEqualTo(authenticatedUser.getWuaId());
    assertThat(projectDetails.getStatus()).isEqualTo(ProjectStatus.DRAFT);
    assertThat(projectDetails.getVersion()).isEqualTo(1);
    assertThat(projectDetails.getIsCurrentVersion()).isTrue();
    assertThat(projectDetails.getProjectType()).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void createForwardWorkPlanProject_correctCalls() {

    var organisationGroup = new PortalOrganisationGroup();
    var projectOperatorForm = new ProjectOperatorForm();
    projectOperatorForm.setOperator(String.valueOf(organisationGroup.getOrgGrpId()));

    var projectDetails = startProjectService.createForwardWorkPlanProject(authenticatedUser, organisationGroup);

    verify(projectRepository, times(1)).save(any());
    verify(projectDetailsRepository, times(1)).save(any());
    verify(projectOperatorService, times(1)).createOrUpdateProjectOperator(
        projectDetails,
        projectOperatorForm
    );
  }

  @Test
  public void createForwardWorkPlanProject_correctDetails() {

    var organisationGroup = new PortalOrganisationGroup();

    var projectDetails = startProjectService.createForwardWorkPlanProject(authenticatedUser, organisationGroup);

    assertThat(projectDetails.getCreatedByWua()).isEqualTo(authenticatedUser.getWuaId());
    assertThat(projectDetails.getStatus()).isEqualTo(ProjectStatus.DRAFT);
    assertThat(projectDetails.getVersion()).isEqualTo(1);
    assertThat(projectDetails.getIsCurrentVersion()).isTrue();
    assertThat(projectDetails.getProjectType()).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }
}
