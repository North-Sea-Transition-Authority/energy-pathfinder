package uk.co.ogauthority.pathfinder.service.team.teammanagementcontext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.service.teammanagement.TeamManagementService;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TeamManagementContextServiceTest {

  @Mock
  private TeamManagementService teamManagementService;

  private TeamManagementContextService teamManagementContextService;

  private final Team team = TeamTestingUtil.getRegulatorTeam();

  @Before
  public void setup() {
    teamManagementContextService = new TeamManagementContextService(teamManagementService);
  }

  @Test
  public void buildTeamManagementContext_whenHasRequiredTeamManagementPermissions_thenContextCreated() {

    final var teamManagementPermission = TeamManagementPermission.VIEW;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());

    var result = teamManagementContextService.buildTeamManagementContext(requiredTeamManagementPermissions, null, user);

    assertThat(result.getTeamManagementPermissions()).containsExactly(teamManagementPermission);

  }

  @Test(expected = AccessDeniedException.class)
  public void buildTeamManagementContext_whenNotRequiredTeamManagementPermissions_thenAccessDenied() {
    final var requiredTeamManagementPermissions = Set.of(TeamManagementPermission.VIEW);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(Set.of(UserPrivilege.PATHFINDER_WORK_AREA));
    teamManagementContextService.buildTeamManagementContext(requiredTeamManagementPermissions, null, user);
  }

  @Test
  public void buildTeamManagementContext_whenNoResourceIdProvided_thenContextCreatedAndTeamIsNull() {

    final var teamManagementPermission = TeamManagementPermission.VIEW;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());

    var result = teamManagementContextService.buildTeamManagementContext(requiredTeamManagementPermissions, null, user);
    assertThat(result.getTeam()).isNull();
  }

  @Test
  public void buildTeamManagementContext_whenResourceIdProvidedAndUserCanViewTeam_thenContextCreatedWithTeam() {

    final var teamManagementPermission = TeamManagementPermission.VIEW;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());
    final var teamId = team.getId();

    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);
    when(teamManagementService.canViewTeam(team, user)).thenReturn(true);

    var result = teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );
    assertThat(result.getTeam()).isEqualTo(team);
  }

  @Test(expected = AccessDeniedException.class)
  public void buildTeamManagementContext_whenResourceIdProvidedAndUserCannotViewTeam_thenAccessDenied() {

    final var teamManagementPermission = TeamManagementPermission.VIEW;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());
    final var teamId = team.getId();

    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);
    when(teamManagementService.canViewTeam(team, user)).thenReturn(false);

    teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );
  }

  @Test
  public void buildTeamManagementContext_whenResourceIdProvidedAndUserCanManageTeam_thenContextCreatedWithTeam() {

    final var teamManagementPermission = TeamManagementPermission.MANAGE;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());
    final var teamId = team.getId();

    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);
    when(teamManagementService.canManageTeam(team, user)).thenReturn(true);

    var result = teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );
    assertThat(result.getTeam()).isEqualTo(team);
  }

  @Test(expected = AccessDeniedException.class)
  public void buildTeamManagementContext_whenResourceIdProvidedAndUserCannotManageTeam_thenAccessDenied() {

    final var teamManagementPermission = TeamManagementPermission.MANAGE;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());
    final var teamId = team.getId();

    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);
    when(teamManagementService.canManageTeam(team, user)).thenReturn(false);

    teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );
  }

  @Test
  public void buildTeamManagementContext_whenIsTeamAccessManager_thenFieldIsTrue() {

    final var teamManagementPermission = TeamManagementPermission.MANAGE;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());

    when(teamManagementService.canManageTeam(any(), any())).thenReturn(true);
    when(teamManagementService.canManageAnyOrgTeam(user)).thenReturn(false);

    final var teamId = team.getId();
    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);

    var result = teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );

    assertThat(result.isTeamAccessManager()).isTrue();
    assertThat(result.isOrganisationAccessManager()).isFalse();
    assertThat(result.canManageTeam()).isTrue();
  }

  @Test
  public void buildTeamManagementContext_whenIsOrganisationAccessManager_thenFieldIsTrue() {

    final var teamManagementPermission = TeamManagementPermission.MANAGE;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());

    when(teamManagementService.canManageAnyOrgTeam(user)).thenReturn(true);
    when(teamManagementService.canManageTeam(team, user)).thenReturn(true);

    final var teamId = team.getId();
    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);

    var result = teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );

    assertThat(result.isOrganisationAccessManager()).isTrue();
    assertThat(result.isTeamAccessManager()).isTrue();
    assertThat(result.canManageTeam()).isTrue();
  }

  @Test
  public void buildTeamManagementContext_whenViewerOnly_thenAccessManagerFieldFalse() {

    final var teamManagementPermission = TeamManagementPermission.VIEW;
    final var requiredTeamManagementPermissions = Set.of(teamManagementPermission);
    final var user = UserTestingUtil.getAuthenticatedUserAccount(teamManagementPermission.getUserPrivileges());

    when(teamManagementService.canManageAnyOrgTeam(user)).thenReturn(false);
    when(teamManagementService.canManageTeam(team, user)).thenReturn(false);
    when(teamManagementService.canViewTeam(team, user)).thenReturn(true);

    final var teamId = team.getId();
    when(teamManagementService.getTeamOrError(teamId)).thenReturn(team);

    var result = teamManagementContextService.buildTeamManagementContext(
        requiredTeamManagementPermissions,
        teamId,
        user
    );

    assertThat(result.isOrganisationAccessManager()).isFalse();
    assertThat(result.isTeamAccessManager()).isFalse();
    assertThat(result.canManageTeam()).isFalse();
  }
}