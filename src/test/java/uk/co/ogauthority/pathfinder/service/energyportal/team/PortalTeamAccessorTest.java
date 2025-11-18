package uk.co.ogauthority.pathfinder.service.energyportal.team;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.fivium.energyportal.starter.accounts.EnergyPortalServiceAccessService;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.repository.team.PortalTeamRepository;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class PortalTeamAccessorTest {

  private final int TEAM_RES_ID = 12345;

  @Mock
  private PortalTeamRepository portalTeamRepository;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EnergyPortalServiceAccessService energyPortalServiceAccessService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private PortalTeamAccessor portalTeamAccessor;

  private AuthenticatedUserAccount authenticatedUserAccount;

  @Before
  public void setup() {
    portalTeamAccessor = spy(new PortalTeamAccessor(
        portalTeamRepository,
        entityManager,
        energyPortalServiceAccessService,
        webUserAccountService
    ));

    authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount(Set.of(UserPrivilege.PATHFINDER_REG_ORG_MANAGER));
  }

  @Test(expected = RuntimeException.class)
  public void createOrganisationGroupTeam_caughtExceptionIsRethrown() {
    doThrow(new NullPointerException()).when(portalTeamRepository).createTeam(any(), any(), any(), any(), any());
    portalTeamAccessor.createOrganisationGroupTeam(new PortalOrganisationGroup(), authenticatedUserAccount);
  }

  @Test
  public void createOrganisationGroupTeam_verifyRepositoryInteraction() {
    portalTeamAccessor.createOrganisationGroupTeam(new PortalOrganisationGroup(), authenticatedUserAccount);
    verify(portalTeamRepository, times(1)).createTeam(any(), any(), any(), any(), any());
  }

  @Test
  public void addPersonToTeamWithRoles_whenNoPreviousAccess() {

    var personToAdd = UserTestingUtil.getPerson();
    var wuaOfPersonToAdd = UserTestingUtil.getWebUserAccount(2, personToAdd);

    var actionedByWua = UserTestingUtil.getWebUserAccount(1, UserTestingUtil.getPerson());

    doReturn(false)
        .when(portalTeamAccessor).hasAccessToService(personToAdd);

    when(webUserAccountService.findByPerson(personToAdd))
        .thenReturn(Optional.of(wuaOfPersonToAdd));

    portalTeamAccessor.addPersonToTeamWithRoles(
        TEAM_RES_ID,
        personToAdd,
        List.of("ROLE_NAME_1", "ROLE_NAME_2"),
        actionedByWua
    );

    verify(portalTeamRepository).updateUserRoles(
        TEAM_RES_ID,
        "ROLE_NAME_1,ROLE_NAME_2",
        personToAdd.getId().asInt(),
        actionedByWua.getWuaId()
    );

    verify(energyPortalServiceAccessService).addUser(2);

  }

  @Test
  public void addPersonToTeamWithRoles_whenPreviousAccess() {

    var personToAdd = UserTestingUtil.getPerson();

    var actionedByWua = UserTestingUtil.getWebUserAccount(1, UserTestingUtil.getPerson());

    doReturn(true)
        .when(portalTeamAccessor).hasAccessToService(personToAdd);

    portalTeamAccessor.addPersonToTeamWithRoles(
        TEAM_RES_ID,
        personToAdd,
        List.of("ROLE_NAME_1", "ROLE_NAME_2"),
        actionedByWua
    );

    verify(portalTeamRepository).updateUserRoles(
        TEAM_RES_ID,
        "ROLE_NAME_1,ROLE_NAME_2",
        personToAdd.getId().asInt(),
        actionedByWua.getWuaId()
    );

    verify(energyPortalServiceAccessService, never()).addUser(anyLong());
  }

  @Test
  public void addPersonToTeamWithRoles_whenException_thenRethrown() {

    var personToAdd = UserTestingUtil.getPerson();

    var actionedByWua = UserTestingUtil.getWebUserAccount(1, UserTestingUtil.getPerson());

    doReturn(false)
        .when(portalTeamAccessor).hasAccessToService(personToAdd);

    doThrow(new IllegalStateException("unexpected error"))
        .when(portalTeamRepository).updateUserRoles(any(), any(), any(), any());

    assertThatThrownBy(() -> portalTeamAccessor.addPersonToTeamWithRoles(
        TEAM_RES_ID,
        personToAdd,
        List.of("ROLE_NAME_1", "ROLE_NAME_2"),
        actionedByWua
    ))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error adding person to team");
  }

  @Test
  public void removePersonFromTeam_whenStillHasAccess() {
    var personToRemove = UserTestingUtil.getPerson();

    var actionPerformedBy = UserTestingUtil.getWebUserAccount(1, UserTestingUtil.getPerson());

    doReturn(true).when(portalTeamAccessor).hasAccessToService(personToRemove);

    portalTeamAccessor.removePersonFromTeam(TEAM_RES_ID, personToRemove, actionPerformedBy);

    verify(portalTeamRepository)
        .removeUserFromTeam(TEAM_RES_ID, personToRemove.getId().asInt(), actionPerformedBy.getWuaId());

    verify(energyPortalServiceAccessService, never()).removeUser(anyLong());
  }

  @Test
  public void removePersonFromTeam_whenNoLongerHasAccess() {
    var personToRemove = UserTestingUtil.getPerson();
    var personToRemoveWua = UserTestingUtil.getWebUserAccount(1, personToRemove);

    var actionPerformedBy = UserTestingUtil.getWebUserAccount(2, UserTestingUtil.getPerson());

    doReturn(false).when(portalTeamAccessor).hasAccessToService(personToRemove);

    when(webUserAccountService.findByPerson(personToRemove))
        .thenReturn(Optional.of(personToRemoveWua));

    portalTeamAccessor.removePersonFromTeam(TEAM_RES_ID, personToRemove, actionPerformedBy);

    verify(portalTeamRepository)
        .removeUserFromTeam(TEAM_RES_ID, personToRemove.getId().asInt(), actionPerformedBy.getWuaId());

    verify(energyPortalServiceAccessService).removeUser(personToRemoveWua.getWuaId());
  }

  @Test
  public void removePersonFromTeam_whenUnexpectedError_thenRethrown() {
    var personToRemove = UserTestingUtil.getPerson();

    var actionPerformedBy = UserTestingUtil.getWebUserAccount(2, UserTestingUtil.getPerson());

    doThrow(new IllegalStateException("test")).when(portalTeamRepository)
        .removeUserFromTeam(TEAM_RES_ID, personToRemove.getId().asInt(), actionPerformedBy.getWuaId());

    assertThatThrownBy(() -> portalTeamAccessor.removePersonFromTeam(TEAM_RES_ID, personToRemove, actionPerformedBy))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error Removing person from team.");

  }
}
