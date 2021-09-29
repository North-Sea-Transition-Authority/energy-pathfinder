package uk.co.ogauthority.pathfinder.service.teammanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.repository.PersonRepository;
import uk.co.ogauthority.pathfinder.energyportal.repository.WebUserAccountRepository;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.form.teammanagement.UserRolesForm;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamMemberView;
import uk.co.ogauthority.pathfinder.model.teammanagement.TeamRoleView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.email.TeamManagementEmailService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TeamManagementServiceTest {

  @Mock
  private TeamService teamService;

  @Mock
  private TeamManagementEmailService teamManagementEmailService;

  @Mock
  private PersonRepository personRepository;

  @Mock
  private WebUserAccountRepository webUserAccountRepository;

  private RegulatorTeam regulatorTeam;
  private Role teamAdminRole;
  private Role regTeamSomeOtherRole;
  private Role orgTeamSomeOtherRole;
  private List<Role> organisationRoles;
  private String organisationRolesCsv;
  private TeamMember regulatorPersonRegulatorTeamMember;
  private TeamMember otherRegulatorPersonTeamMember;
  private OrganisationTeam organisationTeam1;
  private OrganisationTeam organisationTeam2;
  private Person regulatorTeamAdminPerson;
  private Person otherRegulatorPerson;
  private Person organisationPerson;
  private AuthenticatedUserAccount organisationUser;
  private AuthenticatedUserAccount manageAnyOrgRegulatorUser;
  private AuthenticatedUserAccount manageRegTeamRegulatorUser;
  private AuthenticatedUserAccount workAreaOnlyUser;
  private AuthenticatedUserAccount teamViewerOnlyUser;
  private AuthenticatedUserAccount manageAllTeamsUser;
  private final WebUserAccount someWebUserAccount = UserTestingUtil.getWebUserAccount();
  private UserRolesForm userRolesForm;
  private TeamManagementService teamManagementService;

  /**
   * This creates:
   * - A regulator team with 1 member (regulatorTeamAdminPerson) who has admin privs only. This team has 2 roles, regTeamAdminRole
   * and regTeamSomeOtherRole.
   * - 2 organisation teams in separate org groupsL organisationTeam1 and organisationTeam2
   * - Several AuthenticatedUserAccount with various privileges
   */
  @Before
  public void setUp() {

    teamManagementService = new TeamManagementService(
        teamService,
        teamManagementEmailService,
        personRepository,
        webUserAccountRepository
    );

    regulatorTeam = TeamTestingUtil.getRegulatorTeam();

    teamAdminRole = TeamTestingUtil.getTeamAdminRole();
    regTeamSomeOtherRole = TeamTestingUtil.generateRole("SOME_ROLE", 999);
    orgTeamSomeOtherRole = TeamTestingUtil.generateRole("SOME_NON_ADMIN_ROLE", 999);
    organisationRoles = List.of(
        TeamTestingUtil.generateRole("FIRST_ROLE", 10),
        TeamTestingUtil.generateRole("SECOND_ROLE", 20)
    );
    organisationRolesCsv = "FIRST_ROLE, SECOND_ROLE";

    regulatorTeamAdminPerson = new Person(1, "reg", "person", "reg@person.com", "0");
    regulatorPersonRegulatorTeamMember = new TeamMember(regulatorTeam, regulatorTeamAdminPerson, Set.of(teamAdminRole));

    otherRegulatorPerson = new Person(2, "other reg", "person", "otherreg@person.com", "0");
    otherRegulatorPersonTeamMember = new TeamMember(regulatorTeam, otherRegulatorPerson, Set.of(regTeamSomeOtherRole));

    organisationPerson = new Person(3, "org", "person", "org@person.com", "0");
    organisationUser = new AuthenticatedUserAccount(new WebUserAccount(3, organisationPerson), List.of());

    var manageAnyOrgRegulatorPerson = new Person(4, "regOrg", "manage", "regOrg@manage.com", "0");
    manageAnyOrgRegulatorUser = new AuthenticatedUserAccount(new WebUserAccount(4, manageAnyOrgRegulatorPerson), List.of());
    when(teamService.getAllUserPrivilegesForPerson(manageAnyOrgRegulatorPerson))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_REG_ORG_MANAGER));

    var manageRegTeamRegulatorPerson = new Person(5, "regTeam", "manage", "regTeam@manage.com", "0");
    manageRegTeamRegulatorUser = new AuthenticatedUserAccount(new WebUserAccount(5, manageRegTeamRegulatorPerson), List.of());
    when(teamService.getAllUserPrivilegesForPerson(manageRegTeamRegulatorPerson))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_REGULATOR_ADMIN));

    var manageAllTeamsPerson = new Person(6, "all", "manage", "all@manage.com", "0");
    manageAllTeamsUser = new AuthenticatedUserAccount(new WebUserAccount(6, manageAllTeamsPerson), List.of());
    when(teamService.getAllUserPrivilegesForPerson(manageAllTeamsPerson))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_REGULATOR_ADMIN, UserPrivilege.PATHFINDER_REG_ORG_MANAGER));

    var workAreaOnlyPerson = new Person(7, "workarea", "only", "workarea@only.com", "0");
    workAreaOnlyUser = new AuthenticatedUserAccount(new WebUserAccount(7, workAreaOnlyPerson), List.of());
    when(teamService.getAllUserPrivilegesForPerson(workAreaOnlyPerson))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_WORK_AREA));

    var portalOrganisationGroup1 = TeamTestingUtil.generateOrganisationGroup(111, "GROUP1", "GRP1");
    organisationTeam1 = new OrganisationTeam(222, "team1", "team1", portalOrganisationGroup1);

    var portalOrganisationGroup2 = TeamTestingUtil.generateOrganisationGroup(333, "GROUP2", "GRP2");
    organisationTeam2 = new OrganisationTeam(444, "team2", "team2", portalOrganisationGroup2);

    teamViewerOnlyUser = UserTestingUtil.getAuthenticatedUserAccount(Set.of(UserPrivilege.PATHFINDER_TEAM_VIEWER));

    when(teamService.getRegulatorTeam()).thenReturn(regulatorTeam);

    when(teamService.getTeamByResId(regulatorTeam.getId())).thenReturn(regulatorTeam);

    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorTeamAdminPerson))
        .thenReturn(Optional.of(regulatorPersonRegulatorTeamMember));

    when(teamService.getTeamMembers(regulatorTeam))
        .thenReturn(List.of(regulatorPersonRegulatorTeamMember));

    when(teamService.isPersonMemberOfTeam(regulatorTeamAdminPerson, regulatorTeam)).thenReturn(true);

    when(teamService.getAllRolesForTeam(regulatorTeam))
        .thenReturn(List.of(teamAdminRole, regTeamSomeOtherRole));

    when(teamService.getAllOrganisationTeams())
        // Returned list must be mutable
        .thenReturn(new ArrayList<>(List.of(organisationTeam1, organisationTeam2)));

    userRolesForm = new UserRolesForm();
  }

  @Test
  public void getTeamOrError_verifyServiceInteraction() {
    Team regTeam = teamManagementService.getTeamOrError(regulatorTeam.getId());
    assertThat(regTeam).isEqualTo(regulatorTeam);
    verify(teamService, times(1)).getTeamByResId(regulatorTeam.getId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getTeamOrError_throwErrorWhenTeamNotFound() {
    when(teamService.getTeamByResId(anyInt())).thenThrow(new PathfinderEntityNotFoundException(""));
    teamManagementService.getTeamOrError(999);
  }

  @Test
  public void getTeamMemberViewForTeamAndPerson_whenNotATeamMember() {
    assertThat(teamManagementService.getTeamMemberViewForTeamAndPerson(
        regulatorTeam,
        organisationPerson,
        organisationUser
    )).isEmpty();
  }

  @Test
  public void getTeamMemberViewForTeamAndPerson_whenATeamMember_basicPersonPropertiesMappedAsExpected() {
    var teamMemberViewOptional = teamManagementService.getTeamMemberViewForTeamAndPerson(
        regulatorTeam,
        regulatorTeamAdminPerson,
        manageRegTeamRegulatorUser
    );

    assertThat(teamMemberViewOptional).isPresent();
    assertTeamUserViewHasExpectedSimpleProperties(regulatorTeam, regulatorTeamAdminPerson, teamMemberViewOptional.get());

  }

  @Test
  public void getTeamMemberViewForTeamAndPerson_whenATeamMember_memberRolesMappedAsExpected() {

    var teamMemberViewOptional = teamManagementService.getTeamMemberViewForTeamAndPerson(
        regulatorTeam,
        regulatorTeamAdminPerson,
        manageRegTeamRegulatorUser
    );

    Role expectedRole = TeamTestingUtil.getTeamAdminRole();
    assertThat(teamMemberViewOptional).isPresent();
    assertTeamUserViewHasSingleRoleMappedAsExpected(expectedRole, teamMemberViewOptional.get());
  }

  @Test
  public void getTeamMemberViewsForTeam_whenSingleTeamMemberIsAdmin() {
    Role expectedRole = TeamTestingUtil.getTeamAdminRole();
    List<TeamMemberView> teamMemberViewsForTeam = teamManagementService.getTeamMemberViewsForTeam(
        regulatorTeam,
        manageRegTeamRegulatorUser
    );

    assertThat(teamMemberViewsForTeam).hasSize(1);
    assertTeamUserViewHasSingleRoleMappedAsExpected(expectedRole, teamMemberViewsForTeam.get(0));
    assertTeamUserViewHasExpectedSimpleProperties(regulatorTeam, regulatorTeamAdminPerson, teamMemberViewsForTeam.get(0));
  }

  @Test
  public void getRolesForTeam_orderedByDisplaySequenceValue() {
    var firstRole = TeamTestingUtil.generateRole("FIRST_ROLE", 10);
    var secondRole = TeamTestingUtil.generateRole("SECOND_ROLE", 20);
    var thirdRole = TeamTestingUtil.generateRole("THIRD_ROLE", 30);
    List<Role> unorderedRoleList = List.of(secondRole, thirdRole, firstRole);

    when(teamService.getAllRolesForTeam(regulatorTeam)).thenReturn(unorderedRoleList);

    List<TeamRoleView> selectableViews = teamManagementService.getRolesForTeam(regulatorTeam);
    assertThat(selectableViews.get(0).getRoleName()).isEqualTo(firstRole.getName());
    assertThat(selectableViews.get(0).getDisplaySequence()).isEqualTo(firstRole.getDisplaySequence());

    assertThat(selectableViews.get(1).getRoleName()).isEqualTo(secondRole.getName());
    assertThat(selectableViews.get(1).getDisplaySequence()).isEqualTo(secondRole.getDisplaySequence());

    assertThat(selectableViews.get(2).getRoleName()).isEqualTo(thirdRole.getName());
    assertThat(selectableViews.get(2).getDisplaySequence()).isEqualTo(thirdRole.getDisplaySequence());
  }

  @Test
  public void notifyNewTeamUser() {
    teamManagementService.notifyNewTeamUser(organisationTeam1, organisationPerson, organisationRoles, organisationUser);

    verify(teamManagementEmailService, times(1)).sendAddedToTeamEmail(organisationTeam1, organisationPerson, organisationRolesCsv, organisationUser);
  }

  @Test
  public void notifyTeamRolesUpdated() {
    teamManagementService.notifyTeamRolesUpdated(organisationTeam1, organisationPerson, organisationRoles, organisationUser);

    verify(teamManagementEmailService, times(1)).sendTeamRolesUpdatedEmail(organisationTeam1, organisationPerson, organisationRolesCsv, organisationUser);
  }

  @Test
  public void notifyTeamUserRemoved() {
    teamManagementService.notifyTeamUserRemoved(organisationTeam1, organisationPerson, organisationUser);

    verify(teamManagementEmailService, times(1)).sendRemovedFromTeamEmail(organisationTeam1, organisationPerson, organisationUser);
  }

  @Test
  public void canManageTeam_whenUserIsMemberOfTeam_andIsTeamAdministrator() {
    var teamMember = new TeamMember(organisationTeam1, organisationPerson, Set.of(TeamTestingUtil.getTeamAdminRole()));
    when(teamService.getMembershipOfPersonInTeam(organisationTeam1, organisationPerson))
        .thenReturn(Optional.of(teamMember));

    assertThat(teamManagementService.canManageTeam(organisationTeam1, organisationUser)).isTrue();
  }

  @Test
  public void canManageTeam_whenUserIsMemberOfTeam_andNotTeamAdministrator() {
    var teamMember = new TeamMember(
        organisationTeam1,
        organisationPerson,
        Set.of(TeamTestingUtil.generateRole("Some non admin role", 999))
    );
    when(teamService.getMembershipOfPersonInTeam(organisationTeam1, organisationPerson)).thenReturn(
        Optional.of(teamMember));

    assertThat(teamManagementService.canManageTeam(organisationTeam1, organisationUser)).isFalse();
  }


  @Test
  public void canManageTeam_whenUserCanManageAnyOrgTeamOnly_AndTeamIsRegulator() {
    assertThat(teamManagementService.canManageTeam(regulatorTeam, manageAnyOrgRegulatorUser)).isFalse();
  }

  @Test
  public void canManageTeam_whenUserCanManageAnyOrgTeamOnly_AndTeamIsOrganisation() {
    assertThat(teamManagementService.canManageTeam(organisationTeam1, manageAnyOrgRegulatorUser)).isTrue();
  }

  @Test
  public void getAllTeamsUserCanManage_userCannotManageAnyTeams() {
    assertThat(teamManagementService.getAllTeamsUserCanManage(workAreaOnlyUser)).isEmpty();
  }

  @Test
  public void getAllTeamsUserCanManage_userCanManageRegulatorTeamOnly() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsUserCanManage(manageRegTeamRegulatorUser);
    assertThat(manageableTeams).containsExactly(regulatorTeam);
  }

  @Test
  public void getAllTeamsUserCanManage_userCanManageAllOrgTeamsOnly() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsUserCanManage(manageAnyOrgRegulatorUser);
    assertThat(manageableTeams).containsExactly(organisationTeam1, organisationTeam2);
  }

  @Test
  public void getAllIrsTeamsUserCanManage_userCanManageRegulatorTeamAndAllOrgs() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsUserCanManage(manageAllTeamsUser);
    assertThat(manageableTeams).containsExactly(regulatorTeam, organisationTeam1, organisationTeam2);
  }

  @Test
  public void populateExistingRoles_personHasNoRolesInTeam() {
    when(teamService.getMembershipOfPersonInTeam(organisationTeam1, regulatorTeamAdminPerson))
        .thenReturn(Optional.empty());

    teamManagementService.populateExistingRoles(
        regulatorTeamAdminPerson,
        organisationTeam1,
        userRolesForm,
        manageRegTeamRegulatorUser
    );
    assertThat(userRolesForm.getUserRoles()).isEmpty();
  }

  @Test
  public void populateExistingRoles_personHasRolesInTeam() {
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorTeamAdminPerson))
        .thenReturn(Optional.of(new TeamMember(regulatorTeam,
            regulatorTeamAdminPerson, Set.of(teamAdminRole, regTeamSomeOtherRole))));

    teamManagementService.populateExistingRoles(
        regulatorTeamAdminPerson,
        regulatorTeam,
        userRolesForm,
        manageRegTeamRegulatorUser
    );
    // Expect order to match role display sequence
    assertThat(userRolesForm.getUserRoles()).containsExactly(teamAdminRole.getName(),
        regTeamSomeOtherRole.getName());

  }

  @Test
  public void removeTeamMember_whenRegulatorTeamAndPersonBeingRemovedIsNotLastAdministrator_thenRemovalExpected() {
    // Set up a second team member who is also an admin
    var secondTeamMember = new TeamMember(regulatorTeam, otherRegulatorPerson, Set.of(teamAdminRole));

    when(teamService.getTeamMembers(regulatorTeam))
        .thenReturn(List.of(regulatorPersonRegulatorTeamMember, secondTeamMember));

    when(teamService.isPersonMemberOfTeam(or(eq(regulatorPersonRegulatorTeamMember.getPerson()), eq(secondTeamMember.getPerson())), eq(regulatorTeam)))
        .thenReturn(true);

    teamManagementService.removeTeamMember(secondTeamMember.getPerson(), regulatorTeam, someWebUserAccount);
    verify(teamService, times(1)).removePersonFromTeam(regulatorTeam, secondTeamMember.getPerson(), someWebUserAccount);
  }

  @Test(expected = LastAdministratorException.class)
  public void removeTeamMember_whenRegulatorTeamAndPersonBeingRemovedIsLastAdministrator_thenException() {

    var lastTeamAdministrator = new TeamMember(regulatorTeam, regulatorTeamAdminPerson, Set.of(teamAdminRole));

    when(teamService.getTeamMembers(regulatorTeam))
        .thenReturn(List.of(lastTeamAdministrator));

    when(teamService.isPersonMemberOfTeam(lastTeamAdministrator.getPerson(), regulatorTeam))
        .thenReturn(true);

    teamManagementService.removeTeamMember(lastTeamAdministrator.getPerson(), regulatorTeam, someWebUserAccount);
  }

  @Test
  public void removeTeamMember_whenOrganisationTeamAndPersonBeingRemovedIsNotLastAdministrator_thenRemovalExpected() {

    var teamAdministratorMember = new TeamMember(organisationTeam1, new Person(50, "other", "person", "other@person.com", "0"), Set.of(
        teamAdminRole));
    var nonTeamAdministratorMember = new TeamMember(organisationTeam1, organisationPerson, Set.of(orgTeamSomeOtherRole));

    when(teamService.getTeamMembers(organisationTeam1))
        .thenReturn(List.of(teamAdministratorMember, nonTeamAdministratorMember));

    when(teamService.isPersonMemberOfTeam(or(eq(nonTeamAdministratorMember.getPerson()), eq(nonTeamAdministratorMember.getPerson())), eq(organisationTeam1)))
        .thenReturn(true);

    teamManagementService.removeTeamMember(nonTeamAdministratorMember.getPerson(), organisationTeam1, someWebUserAccount);
    verify(teamService, times(1)).removePersonFromTeam(organisationTeam1, nonTeamAdministratorMember.getPerson(), someWebUserAccount);
  }

  @Test(expected = LastAdministratorException.class)
  public void removeTeamMember_whenOrganisationTeamAndPersonBeingRemovedIsLastAdministratorAndNotRemovedByRegulator_thenException() {

    var lastTeamAdministrator = new TeamMember(organisationTeam1, organisationPerson, Set.of(teamAdminRole));

    when(teamService.getTeamMembers(organisationTeam1))
        .thenReturn(List.of(lastTeamAdministrator));

    when(teamService.isPersonMemberOfTeam(lastTeamAdministrator.getPerson(), organisationTeam1))
        .thenReturn(true);

    var notRegulatorOrganisationManagerPrivilege = UserPrivilege.PATHFINDER_WORK_AREA;

    when(teamService.getAllUserPrivilegesForPerson(someWebUserAccount.getLinkedPerson()))
        .thenReturn(List.of(notRegulatorOrganisationManagerPrivilege));

    teamManagementService.removeTeamMember(lastTeamAdministrator.getPerson(), organisationTeam1, someWebUserAccount);
  }

  @Test
  public void removeTeamMember_whenOrganisationTeamAndPersonBeingRemovedIsLastAdministratorAndRemovedByRegulator_thenRemovalAllowed() {

    var lastTeamAdministrator = new TeamMember(organisationTeam1, organisationPerson, Set.of(teamAdminRole));

    when(teamService.getTeamMembers(organisationTeam1))
        .thenReturn(List.of(lastTeamAdministrator));

    when(teamService.isPersonMemberOfTeam(lastTeamAdministrator.getPerson(), organisationTeam1))
        .thenReturn(true);

    var regulatorOrganisationManagerPrivilege = UserPrivilege.PATHFINDER_REG_ORG_MANAGER;

    when(teamService.getAllUserPrivilegesForPerson(someWebUserAccount.getLinkedPerson()))
        .thenReturn(List.of(regulatorOrganisationManagerPrivilege));

    Assertions.assertDoesNotThrow(() ->
        teamManagementService.removeTeamMember(lastTeamAdministrator.getPerson(), organisationTeam1, someWebUserAccount)
    );
  }

  @Test(expected = RuntimeException.class)
  public void updateUserRoles_errorsWhenFormHasNoRoles() {
    userRolesForm.setUserRoles(List.of());
    teamManagementService.updateUserRoles(regulatorTeamAdminPerson, regulatorTeam, userRolesForm, someWebUserAccount);
  }

  @Test(expected = LastAdministratorException.class)
  public void updateUserRoles_errorsWhenLastAdminIsUpdatedAsNoLongerAnAdmin() {
    userRolesForm.setUserRoles(List.of(regTeamSomeOtherRole.getName()));
    teamManagementService.updateUserRoles(regulatorTeamAdminPerson, regulatorTeam, userRolesForm, someWebUserAccount);
  }

  @Test
  public void updateUserRoles_whenPersonWhoIsLastAdminHasRoleAdded() {
    userRolesForm.setUserRoles(List.of(teamAdminRole.getName(), regTeamSomeOtherRole.getName()));

    teamManagementService.updateUserRoles(regulatorTeamAdminPerson, regulatorTeam, userRolesForm, someWebUserAccount);

    var orderVerifier = Mockito.inOrder(teamService);
    orderVerifier.verify(teamService).removePersonFromTeam(regulatorTeam, regulatorTeamAdminPerson, someWebUserAccount);
    orderVerifier.verify(teamService).addPersonToTeamInRoles(regulatorTeam, regulatorTeamAdminPerson, userRolesForm.getUserRoles(), someWebUserAccount);
    orderVerifier.verifyNoMoreInteractions();
  }

  @Test
  public void updateUserRoles_whenNewPersonAddedToTeam() {
    when(teamService.isPersonMemberOfTeam(regulatorTeamAdminPerson, regulatorTeam)).thenReturn(false);
    userRolesForm.setUserRoles(List.of(teamAdminRole.getName(), regTeamSomeOtherRole.getName()));

    teamManagementService.updateUserRoles(regulatorTeamAdminPerson, regulatorTeam, userRolesForm, someWebUserAccount);

    var orderVerifier = Mockito.inOrder(teamService);
    orderVerifier.verify(teamService).addPersonToTeamInRoles(regulatorTeam, regulatorTeamAdminPerson, userRolesForm.getUserRoles(), someWebUserAccount);
    orderVerifier.verifyNoMoreInteractions();
  }

  @Test
  public void getSelectedRolesForTeam_formContainsOnlyUnsupportedRolesForTeam() {
    userRolesForm.setUserRoles(List.of("NOT_SUPPORTED1", "NOT_SUPPORTED2"));

    List<Role> validSelectedRoles = teamManagementService.getSelectedRolesForTeam(userRolesForm, regulatorTeam);
    assertThat(validSelectedRoles).isEmpty();
  }

  @Test
  public void getSelectedRolesForTeam_formContainsSomeSupportedRolesForTeam() {
    userRolesForm.setUserRoles(List.of("NOT_SUPPORTED1", "NOT_SUPPORTED2", teamAdminRole.getName(), regTeamSomeOtherRole.getName()));

    List<Role> validSelectedRoles = teamManagementService.getSelectedRolesForTeam(userRolesForm, regulatorTeam);
    assertThat(validSelectedRoles).containsExactlyInAnyOrder(teamAdminRole, regTeamSomeOtherRole);
  }

  @Test
  public void canManageAnyOrgTeam() {
    assertThat(teamManagementService.canManageAnyOrgTeam(List.of(UserPrivilege.PATHFINDER_REG_ORG_MANAGER))).isTrue();
    assertThat(teamManagementService.canManageAnyOrgTeam(List.of(UserPrivilege.PATHFINDER_WORK_AREA, UserPrivilege.PATHFINDER_REG_ORG_MANAGER))).isTrue();

    assertThat(teamManagementService.canManageAnyOrgTeam(List.of(UserPrivilege.PATHFINDER_WORK_AREA))).isFalse();
    assertThat(teamManagementService.canManageAnyOrgTeam(List.of(UserPrivilege.PATHFINDER_WORK_AREA, UserPrivilege.PATHFINDER_REGULATOR_ADMIN))).isFalse();
  }

  @Test
  public void canManageRegulatorTeam() {
    assertThat(teamManagementService.canManageRegulatorTeam(List.of(UserPrivilege.PATHFINDER_REGULATOR_ADMIN))).isTrue();
    assertThat(teamManagementService.canManageRegulatorTeam(List.of(UserPrivilege.PATHFINDER_WORK_AREA, UserPrivilege.PATHFINDER_REGULATOR_ADMIN))).isTrue();

    assertThat(teamManagementService.canManageRegulatorTeam(List.of(UserPrivilege.PATHFINDER_WORK_AREA))).isFalse();
    assertThat(teamManagementService.canManageRegulatorTeam(List.of(UserPrivilege.PATHFINDER_WORK_AREA, UserPrivilege.PATHFINDER_REG_ORG_MANAGER))).isFalse();
  }



  private void assertTeamUserViewHasSingleRoleMappedAsExpected(Role expectedRole,
                                                               TeamMemberView teamUserView) {
    Set<TeamRoleView> roleViews = teamUserView.getRoleViews();

    assertThat(roleViews).hasSize(1);
    for (TeamRoleView roleView : roleViews) {
      assertThat(roleView.getRoleName()).isEqualTo(expectedRole.getName());
      assertThat(roleView.getTitle()).isEqualTo(expectedRole.getTitle());
      assertThat(roleView.getDescription()).isEqualTo(expectedRole.getDescription());
      assertThat(roleView.getDisplayName()).isEqualTo(expectedRole.getDescription());
      assertThat(roleView.getIdentifier()).isEqualTo(expectedRole.getName());
    }
  }

  private void assertTeamUserViewHasExpectedSimpleProperties(Team team, Person person, TeamMemberView teamUserView) {
    String expectedEditRoute = ReverseRouter.route(on(PortalTeamManagementController.class).renderMemberRoles(
        team.getId(),
        person.getId().asInt(),
        null,
        null
    ));

    String expectedRemoveRoute = ReverseRouter.route(on(PortalTeamManagementController.class).renderRemoveTeamMember(
        team.getId(),
        person.getId().asInt(),
        null
    ));

    assertThat(teamUserView.getForename()).isEqualTo(person.getForename());
    assertThat(teamUserView.getSurname()).isEqualTo(person.getSurname());
    assertThat(teamUserView.getFullName()).isEqualTo(person.getForename() + " " + person.getSurname());
    assertThat(teamUserView.getEditAction().getUrl()).isEqualTo(expectedEditRoute);
    assertThat(teamUserView.getRemoveAction().getUrl()).isEqualTo(expectedRemoveRoute);
  }

  @Test
  public void getAllTeamsOfTypeUserCanManage_userCanManageAllOrgTeamsOnly_restrictToOrgTeams() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsOfTypeUserCanManage(manageAnyOrgRegulatorUser, TeamType.ORGANISATION);
    assertThat(manageableTeams).containsExactly(organisationTeam1, organisationTeam2);
  }

  @Test
  public void getAllTeamsOfTypeUserCanManage_userCanManageAllOrgTeamsOnly_restrictToRegulatorTeam() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsOfTypeUserCanManage(manageAnyOrgRegulatorUser, TeamType.REGULATOR);
    assertThat(manageableTeams).isEmpty();
  }

  @Test
  public void getAllTeamsOfTypeUserCanManage_userCanManageAllOrgTeamsOnly_noRestriction() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsOfTypeUserCanManage(manageAnyOrgRegulatorUser, null);
    assertThat(manageableTeams).containsExactly(organisationTeam1, organisationTeam2);
  }

  @Test
  public void getAllTeamsOfTypeUserCanManage_userCanManageAllTeams_restrictToRegulatorTeam() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsOfTypeUserCanManage(manageAllTeamsUser, TeamType.REGULATOR);
    assertThat(manageableTeams).containsExactly(regulatorTeam);
  }

  @Test
  public void getAllTeamsOfTypeUserCanManage_userCanManageAllTeams_restrictToOrganisationTeam() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsOfTypeUserCanManage(manageAllTeamsUser, TeamType.ORGANISATION);
    assertThat(manageableTeams).containsExactly(organisationTeam1, organisationTeam2);
  }

  @Test
  public void getAllTeamsOfTypeUserCanManage_userCanManageAllTeams_noRestriction() {
    List<Team> manageableTeams = teamManagementService.getAllTeamsOfTypeUserCanManage(manageAllTeamsUser, null);
    assertThat(manageableTeams).containsExactly(regulatorTeam, organisationTeam1, organisationTeam2);
  }

  @Test
  public void canViewTeam_whenTeamAdministratorOfOrganisationTeam_canViewOrganisationTeam() {

    var teamMember = new TeamMember(
        organisationTeam1,
        teamViewerOnlyUser.getLinkedPerson(),
        Set.of(TeamTestingUtil.getTeamAdminRole())
    );

    when(teamService.getAllUserPrivilegesForPerson(teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_TEAM_VIEWER, UserPrivilege.PATHFINDER_ORG_ADMIN));
    when(teamService.getMembershipOfPersonInTeam(organisationTeam1, teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(Optional.of(teamMember));

    assertThat(teamManagementService.canViewTeam(organisationTeam1, teamViewerOnlyUser)).isTrue();
  }

  @Test
  public void canViewTeam_whenMemberOfOrganisationTeamButNotTeamAdministrator_canViewOrganisationTeam() {

    var notTeamAdministratorRole = TeamTestingUtil.generateRole("SOME_ROLE", 999);

    var teamMember = new TeamMember(
        organisationTeam1,
        teamViewerOnlyUser.getLinkedPerson(),
        Set.of(notTeamAdministratorRole)
    );

    when(teamService.getAllUserPrivilegesForPerson(teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_TEAM_VIEWER));
    when(teamService.getMembershipOfPersonInTeam(organisationTeam1, teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(Optional.of(teamMember));

    assertThat(teamManagementService.canViewTeam(organisationTeam1, teamViewerOnlyUser)).isTrue();
  }

  @Test
  public void canViewTeam_whenTeamAdministratorOfRegulatorTeam_canViewRegulatorTeam() {

    var teamMember = new TeamMember(
        regulatorTeam,
        teamViewerOnlyUser.getLinkedPerson(),
        Set.of(TeamTestingUtil.getTeamAdminRole())
    );

    when(teamService.getAllUserPrivilegesForPerson(teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_TEAM_VIEWER, UserPrivilege.PATHFINDER_REGULATOR_ADMIN));
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(Optional.of(teamMember));

    assertThat(teamManagementService.canViewTeam(regulatorTeam, teamViewerOnlyUser)).isTrue();
  }

  @Test
  public void canViewTeam_whenMemberOfRegulatorTeamButNotTeamAdministrator_canViewRegulatorTeam() {

    var notTeamAdministratorRole = TeamTestingUtil.generateRole("SOME_ROLE", 999);

    var teamMember = new TeamMember(
        regulatorTeam,
        teamViewerOnlyUser.getLinkedPerson(),
        Set.of(notTeamAdministratorRole)
    );

    when(teamService.getAllUserPrivilegesForPerson(teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_TEAM_VIEWER));
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(Optional.of(teamMember));

    assertThat(teamManagementService.canViewTeam(regulatorTeam, teamViewerOnlyUser)).isTrue();
  }

  @Test
  public void canViewTeam_whenOrganisationManagerOfRegulatorTeamButNotTeamAdministrator_canViewOrganisationTeams() {

    when(teamService.getAllUserPrivilegesForPerson(teamViewerOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_TEAM_VIEWER, UserPrivilege.PATHFINDER_REG_ORG_MANAGER));

    assertThat(teamManagementService.canViewTeam(organisationTeam1, teamViewerOnlyUser)).isTrue();
  }

  @Test
  public void canViewTeam_whenNotInRegulatorTeam_cannotViewRegulatorTeam() {

    when(teamService.getAllUserPrivilegesForPerson(workAreaOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_WORK_AREA));
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, workAreaOnlyUser.getLinkedPerson()))
        .thenReturn(Optional.empty());

    assertThat(teamManagementService.canViewTeam(regulatorTeam, workAreaOnlyUser)).isFalse();
  }

  @Test
  public void canViewTeam_whenNotInOrganisationTeam_cannotViewOrganisationTeam() {

    when(teamService.getAllUserPrivilegesForPerson(workAreaOnlyUser.getLinkedPerson()))
        .thenReturn(List.of(UserPrivilege.PATHFINDER_WORK_AREA));
    when(teamService.getMembershipOfPersonInTeam(organisationTeam1, workAreaOnlyUser.getLinkedPerson()))
        .thenReturn(Optional.empty());

    assertThat(teamManagementService.canViewTeam(organisationTeam1, workAreaOnlyUser)).isFalse();
  }

  @Test
  public void getAllTeamsUserCanView_whenMemberOfRegulatorTeam_canViewRegulatorTeam() {

    when(teamService.getRegulatorTeamIfPersonInRole(
        manageRegTeamRegulatorUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));

    var viewableTeams = teamManagementService.getAllTeamsUserCanView(manageRegTeamRegulatorUser);

    assertThat(viewableTeams).containsExactly(regulatorTeam);
  }

  @Test
  public void getAllTeamsUserCanView_whenRegOrgManagerOfRegulatorTeam_canViewAllTeams() {

    var regulatorOrganisationManager = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_REG_ORG_MANAGER
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        regulatorOrganisationManager.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));
    when(teamService.getAllOrganisationTeams()).thenReturn(List.of(organisationTeam1, organisationTeam2));

    var viewableTeams = teamManagementService.getAllTeamsUserCanView(regulatorOrganisationManager);

    assertThat(viewableTeams).containsExactly(regulatorTeam, organisationTeam1, organisationTeam2);
  }

  @Test
  public void getAllTeamsUserCanView_whenMemberOfOrganisationTeam_canViewOrganisationTeam() {

    when(teamService.getRegulatorTeamIfPersonInRole(
        organisationUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.empty());
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationUser.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam1));

    var viewableTeams = teamManagementService.getAllTeamsUserCanView(organisationUser);

    assertThat(viewableTeams).containsExactly(organisationTeam1);
  }

  @Test
  public void getAllTeamsUserCanView_whenNotAMemberOfAnyTeam_cannotViewAnyTeam() {

    when(teamService.getRegulatorTeamIfPersonInRole(
        organisationUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.empty());
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationUser.getLinkedPerson()))
        .thenReturn(Collections.emptyList());

    var viewableTeams = teamManagementService.getAllTeamsUserCanView(organisationUser);

    assertThat(viewableTeams).isEmpty();
  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenRegulatorTeamMemberAndRegulatorType_thenRegulatorTeam() {
    when(teamService.getRegulatorTeamIfPersonInRole(
        manageRegTeamRegulatorUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(manageRegTeamRegulatorUser, TeamType.REGULATOR);

    assertThat(viewableTeams).containsExactly(regulatorTeam);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenRegulatorTeamMemberAndOrganisationType_thenNoTeam() {
    when(teamService.getRegulatorTeamIfPersonInRole(
        manageRegTeamRegulatorUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(manageRegTeamRegulatorUser, TeamType.ORGANISATION);

    assertThat(viewableTeams).isEmpty();

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenRegulatorTeamMemberAndNullType_thenRegulatorTeam() {
    when(teamService.getRegulatorTeamIfPersonInRole(
        manageRegTeamRegulatorUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(manageRegTeamRegulatorUser, null);

    assertThat(viewableTeams).containsExactly(regulatorTeam);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenRegOrgManagerAndOrganisationType_thenAllOrgTeams() {

    var regulatorOrganisationManager = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_REG_ORG_MANAGER
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        regulatorOrganisationManager.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));
    when(teamService.getAllOrganisationTeams()).thenReturn(List.of(organisationTeam1, organisationTeam2));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(regulatorOrganisationManager, TeamType.ORGANISATION);

    assertThat(viewableTeams).containsExactly(organisationTeam1, organisationTeam2);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenRegOrgManagerAndRegulatorType_thenRegulatorTeam() {

    var regulatorOrganisationManager = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_REG_ORG_MANAGER
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        regulatorOrganisationManager.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(regulatorOrganisationManager, TeamType.REGULATOR);

    assertThat(viewableTeams).containsExactly(regulatorTeam);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenRegOrgManagerAndNullType_thenAllTeams() {

    var regulatorOrganisationManager = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_REG_ORG_MANAGER
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        regulatorOrganisationManager.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));
    when(teamService.getAllOrganisationTeams()).thenReturn(List.of(organisationTeam1, organisationTeam2));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(regulatorOrganisationManager, null);

    assertThat(viewableTeams).containsExactly(regulatorTeam, organisationTeam1, organisationTeam2);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenOrganisationTeamMemberAndRegulatorType_thenNoTeam() {

    var organisationUser = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        organisationUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.empty());
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationUser.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam1));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(organisationUser, TeamType.REGULATOR);

    assertThat(viewableTeams).isEmpty();

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenOrganisationTeamMemberAndOrganisationType_thenOrganisationTeam() {

    var organisationUser = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        organisationUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.empty());
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationUser.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam1));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(organisationUser, TeamType.ORGANISATION);

    assertThat(viewableTeams).containsExactly(organisationTeam1);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_whenOrganisationTeamMemberAndNullType_thenOrganisationTeam() {

    var organisationUser = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        organisationUser.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.empty());
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationUser.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam1));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(organisationUser, null);

    assertThat(viewableTeams).containsExactly(organisationTeam1);

  }

  @Test
  public void getAllTeamsOfTypeUserCanView_isSortedByTeamNameAsc() {
    var organisationAdministrator = UserTestingUtil.getAuthenticatedUserAccount(Set.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN
    ));

    when(teamService.getRegulatorTeamIfPersonInRole(
        organisationAdministrator.getLinkedPerson(),
        EnumSet.allOf(RegulatorRole.class)
    )).thenReturn(Optional.of(regulatorTeam));

    var organisationTeamA = TeamTestingUtil.getOrganisationTeam(1, "A ORGANISATION");
    var organisationTeamC = TeamTestingUtil.getOrganisationTeam(2, "c organisation");
    var organisationTeamB = TeamTestingUtil.getOrganisationTeam(3, "B ORGANISATION");

    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationAdministrator.getLinkedPerson()))
        .thenReturn(List.of(organisationTeamC, organisationTeamB, organisationTeamA));

    var viewableTeams = teamManagementService.getAllTeamsOfTypeUserCanView(
        organisationAdministrator,
        TeamType.ORGANISATION
    );

    assertThat(viewableTeams).containsExactly(organisationTeamA, organisationTeamB, organisationTeamC);
  }

  @Test
  public void canManageAnyOrgTeam_authenticatedUserAccountVariant_whenOrganisationAccessManager_thenTrue() {
    final var regulatorOrganisationManagerPrivilege = UserPrivilege.PATHFINDER_REG_ORG_MANAGER;
    final var user = UserTestingUtil.getAuthenticatedUserAccount(
        Set.of(regulatorOrganisationManagerPrivilege)
    );
    when(teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson()))
        .thenReturn(List.of(regulatorOrganisationManagerPrivilege));

    assertThat(teamManagementService.canManageAnyOrgTeam(user)).isTrue();
  }

  @Test
  public void canManageAnyOrgTeam_authenticatedUserAccountVariant_whenNotOrganisationAccessManager_thenFalse() {
    final var regulatorOrganisationManagerPrivilege = UserPrivilege.PATHFINDER_WORK_AREA;
    final var user = UserTestingUtil.getAuthenticatedUserAccount(
        Set.of(regulatorOrganisationManagerPrivilege)
    );
    when(teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson()))
        .thenReturn(List.of(regulatorOrganisationManagerPrivilege));

    assertThat(teamManagementService.canManageAnyOrgTeam(user)).isFalse();
  }

  @Test
  public void canManageAnyOrgTeam_personVariant_whenOrganisationAccessManager_thenTrue() {

    final var regulatorOrganisationManagerPrivilege = UserPrivilege.PATHFINDER_REG_ORG_MANAGER;

    final var person = UserTestingUtil.getPerson();

    when(teamService.getAllUserPrivilegesForPerson(person))
        .thenReturn(List.of(regulatorOrganisationManagerPrivilege));

    assertThat(teamManagementService.canManageAnyOrgTeam(person)).isTrue();
  }

  @Test
  public void canManageAnyOrgTeam_personVariant_whenNotOrganisationAccessManager_thenFalse() {

    final var notRegulatorOrganisationManagerPrivilege = UserPrivilege.PATHFINDER_WORK_AREA;

    final var person = UserTestingUtil.getPerson();

    when(teamService.getAllUserPrivilegesForPerson(person))
        .thenReturn(List.of(notRegulatorOrganisationManagerPrivilege));

    assertThat(teamManagementService.canManageAnyOrgTeam(person)).isFalse();
  }

  @Test
  public void constructAddMemberAction_whenTeamAdmin_thenEnabled() {

    final var team = regulatorTeam;
    final var accessManagerPrivilege = UserPrivilege.PATHFINDER_REGULATOR_ADMIN;
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson()))
        .thenReturn(List.of(accessManagerPrivilege));

    when(teamService.getMembershipOfPersonInTeam(team, user.getLinkedPerson()))
        .thenReturn(Optional.of(regulatorPersonRegulatorTeamMember));

    var result = teamManagementService.constructAddMemberAction(team, user);

    assertConstructAddMemberActionCommonFields((LinkButton) result, team);
    assertThat(result.getEnabled()).isTrue();
  }

  @Test
  public void constructAddMemberAction_whenNotTeamAdmin_thenDisabled() {

    final var team = regulatorTeam;
    final var nonAccessManagerPrivilege = UserPrivilege.PATHFINDER_WORK_AREA;
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(teamService.getAllUserPrivilegesForPerson(user.getLinkedPerson()))
        .thenReturn(List.of(nonAccessManagerPrivilege));

    when(teamService.getMembershipOfPersonInTeam(team, user.getLinkedPerson()))
        .thenReturn(Optional.of(otherRegulatorPersonTeamMember));

    var result = teamManagementService.constructAddMemberAction(team, user);

    assertConstructAddMemberActionCommonFields((LinkButton) result, team);
    assertThat(result.getEnabled()).isFalse();
  }

  private void assertConstructAddMemberActionCommonFields(LinkButton linkButton, Team team) {
    assertThat(linkButton.getPrompt()).isEqualTo(TeamManagementService.ADD_USER_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(PortalTeamManagementController.class)
        .renderAddUserToTeam(team.getId(), null, null)
    ));
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.BLUE);
  }

}