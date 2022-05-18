package uk.co.ogauthority.pathfinder.service.projectcontext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectFormPagePermissionCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectStatusCheck;
import uk.co.ogauthority.pathfinder.controller.project.annotation.ProjectTypeCheck;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectDetailVersionType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContextServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private ProjectContributorsCommonService projectContributorsCommonService;

  @Mock
  private TeamService teamService;

  private ProjectContextService projectContextService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final Set<ProjectPermission> projectPermissions = Set.of(ProjectPermission.EDIT, ProjectPermission.SUBMIT);

  private final Set<ProjectType> allowedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);

  @Before
  public void setUp() throws Exception {
    projectContextService = new ProjectContextService(
        projectService,
        projectOperatorService,
        projectContributorsCommonService,
        teamService);
    detail.setStatus(ProjectStatus.DRAFT);
  }

  @Test
  public void getProjectContext() {

    var context = projectContextService.getProjectContext(detail, authenticatedUser, projectPermissions);
    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT
    );
  }

  @Test
  public void getProjectDetailsOrError_whenCurrentVersionAndPresent() {
    when(projectService.getLatestDetailOrError(any())).thenReturn(detail);
    assertThat(projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.CURRENT_VERSION)).isEqualTo(detail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectDetailsOrError_whenCurrentVersionAndNotPresent() {
    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestDetailOrError(any());
    projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.CURRENT_VERSION);
  }

  @Test
  public void getProjectDetailsOrError_whenLatestSubmittedVersionAndPresent() {
    when(projectService.getLatestSubmittedDetailOrError(any())).thenReturn(detail);
    assertThat(projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.LATEST_SUBMITTED_VERSION)).isEqualTo(detail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getProjectDetailsOrError_whenLatestSubmittedVersionAndNotPresent() {
    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestSubmittedDetailOrError(any());
    projectContextService.getProjectDetailsOrError(detail.getId(), ProjectDetailVersionType.LATEST_SUBMITTED_VERSION);
  }

  @Test
  public void canBuildContext_whenNoException() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    assertThat(projectContextService.canBuildContext(detail, authenticatedUser, TestController.class)).isTrue();
  }

  @Test
  public void canBuildContext_whenException() {
    assertThat(projectContextService.canBuildContext(detail, authenticatedUser, TestController.class)).isFalse();
  }

  @Test
  public void buildProjectContext_whenHasAccess() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    var context = projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        false
    );
    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT,
        ProjectPermission.PROVIDE_UPDATE,
        ProjectPermission.ARCHIVE
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNoAccess() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);
    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        false
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenWrongStatus() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.QA),
        projectPermissions,
        allowedProjectTypes,
        false
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNullProjectStatus_thenAccessDeniedException() {

    detail.setStatus(null);

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.QA),
        projectPermissions,
        allowedProjectTypes,
        false
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNoPermissions() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(true);
    projectContextService.buildProjectContext(
        detail,
        unAuthenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        false
    );
  }

  @Test
  public void buildProjectContext_whenContributorAccessAllowed_andUserIsContributor_thenAccessGranted() {
    var myOrganisationTeam = TeamTestingUtil.getOrganisationTeam(1, "My org");
    var projectContributor = ProjectContributorTestUtil.contributorWithGroupOrgId(
        detail,
        myOrganisationTeam.getPortalOrganisationGroup().getOrgGrpId()
    );

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);
    when(projectContributorsCommonService.getProjectContributorsForDetail(detail))
        .thenReturn(List.of(projectContributor));
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUser.getLinkedPerson()))
        .thenReturn(List.of(myOrganisationTeam));

    var context = projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        true
    );

    assertThat(context.getProjectDetails()).isEqualTo(detail);
    assertThat(context.getUserAccount()).isEqualTo(authenticatedUser);
    assertThat(context.getProjectPermissions()).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT,
        ProjectPermission.PROVIDE_UPDATE,
        ProjectPermission.ARCHIVE
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenNoPermissionsAndIsNotContributor() {
    var myOrganisationTeam = TeamTestingUtil.getOrganisationTeam(1, "My org");
    var projectContributor = ProjectContributorTestUtil.contributorWithGroupOrgId(
        detail,
        142 //different organisationId than myOrganisationTeam
    );

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);
    when(projectContributorsCommonService.getProjectContributorsForDetail(detail))
        .thenReturn(List.of(projectContributor));
    when(teamService.getOrganisationTeamsPersonIsMemberOf(authenticatedUser.getLinkedPerson()))
        .thenReturn(List.of(myOrganisationTeam));

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        true
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenContributorAccessNotAllowed_andUserIsContributor_thenForbidden() {
    var myOrganisationTeam = TeamTestingUtil.getOrganisationTeam(1, "My org");
    var projectContributor = ProjectContributorTestUtil.contributorWithGroupOrgId(
        detail,
        myOrganisationTeam.getPortalOrganisationGroup().getOrgGrpId()
    );

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        false
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenContributorAccessNotAllowed_andUserIsNotContributor_thenForbidden() {
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(false);

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        allowedProjectTypes,
        false
    );
  }

  @Test
  public void buildProjectContext_whenCorrectProjectType_thenReturn() {

    final var projectType = ProjectType.INFRASTRUCTURE;
    detail.setProjectType(projectType);

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    final var context = projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        Set.of(projectType),
        false
    );

    assertThat(context.getProjectDetails()).isEqualTo(detail);
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenIncorrectProjectType_thenAccessDeniedException() {

    final var expectedProjectType = ProjectType.INFRASTRUCTURE;
    final var detailProjectType = ProjectType.FORWARD_WORK_PLAN;
    detail.setProjectType(detailProjectType);

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        Set.of(expectedProjectType),
        false
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenEmptyProjectTypeSet_thenAccessDeniedException() {

    Set<ProjectType> expectedProjectTypes = Set.of();
    final var detailProjectType = ProjectType.FORWARD_WORK_PLAN;
    detail.setProjectType(detailProjectType);

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        expectedProjectTypes,
        false
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void buildProjectContext_whenProjectTypeNull_thenAccessDeniedException() {

    Set<ProjectType> expectedProjectTypes = Set.of(ProjectType.INFRASTRUCTURE);
    detail.setProjectType(null);

    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);

    projectContextService.buildProjectContext(
        detail,
        authenticatedUser,
        Set.of(ProjectStatus.DRAFT),
        projectPermissions,
        expectedProjectTypes,
        false
    );
  }

  @Test
  public void getUserProjectPermissions_withMatch() {
    assertThat(projectContextService.getUserProjectPermissions(authenticatedUser)).containsExactlyInAnyOrder(
        ProjectPermission.EDIT,
        ProjectPermission.SUBMIT,
        ProjectPermission.PROVIDE_UPDATE,
        ProjectPermission.ARCHIVE
    );
  }

  @Test
  public void getUserProjectPermissions_withNoMatch() {
    assertThat(projectContextService.getUserProjectPermissions(unAuthenticatedUser)).isEmpty();
  }

  @Test
  public void getProjectStatusesForClass_whenNoStatuses() {
    assertThat(projectContextService.getProjectStatusesForClass(Object.class)).isEmpty();
  }

  @Test
  public void getProjectStatusesForClass_whenStatuses() {
    var statuses = projectContextService.getProjectStatusesForClass(TestClassWithContextAnnotations.class);

    assertThat(statuses).containsExactly(
        ProjectStatus.QA
    );
  }

  @Test
  public void getProjectPermissionsForClass_whenNoPermissions() {
    assertThat(projectContextService.getProjectPermissionsForClass(Object.class)).isEmpty();
  }

  @Test
  public void getProjectPermissionsForClass_whenPermissions() {
    var statuses = projectContextService.getProjectPermissionsForClass(TestClassWithContextAnnotations.class);

    assertThat(statuses).containsExactly(
        ProjectPermission.PROVIDE_ASSESSMENT
    );
  }

  @Test
  public void getProjectTypesForClass_whenNoTypes_thenEmptySet() {
    assertThat(projectContextService.getProjectTypesForClass(Object.class)).isEmpty();
  }

  @Test
  public void getProjectTypesForClass_whenTypes_thenPopulatedSet() {
    var allowedProjectTypes = projectContextService.getProjectTypesForClass(TestClassWithContextAnnotations.class);

    assertThat(allowedProjectTypes).containsExactlyInAnyOrder(
        ProjectType.INFRASTRUCTURE,
        ProjectType.FORWARD_WORK_PLAN
    );
  }

  @ProjectStatusCheck(status = ProjectStatus.DRAFT)
  @ProjectTypeCheck(types = ProjectType.INFRASTRUCTURE)
  @ProjectFormPagePermissionCheck
  private static final class TestController {}
}
