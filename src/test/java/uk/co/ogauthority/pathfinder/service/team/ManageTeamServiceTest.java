package uk.co.ogauthority.pathfinder.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.team.PortalTeamManagementController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.enums.team.ViewableTeamType;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ManageTeamServiceTest {

  @Mock
  private TeamService teamService;

  private ManageTeamService manageTeamService;

  private RegulatorTeam regulatorTeam;
  private OrganisationTeam organisationTeam;
  private Role regulatorOrganisationManagerRole;
  private Role teamAdministratorRole;

  @Before
  public void setUp() {
    manageTeamService = new ManageTeamService(teamService);
    regulatorTeam = new RegulatorTeam(1, "Regulator Team", "Mock regulator team");

    PortalOrganisationGroup portalOrganisationGroup = new PortalOrganisationGroup();
    organisationTeam = new OrganisationTeam(1, "Organisation team", "Mock organisation team", portalOrganisationGroup);

    regulatorOrganisationManagerRole = new Role(
        "ORGANISATION_MANAGER",
        "ORGANISATION_MANAGER",
        "ORGANISATION_MANAGER",
        1
    );
    teamAdministratorRole = new Role(
        "RESOURCE_COORDINATOR",
        "RESOURCE_COORDINATOR",
        "RESOURCE_COORDINATOR",
        1
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenRegulatorOrganisationManager_regulatorAndOrganisationTeamsReturned() {

    var regulatorOrganisationManager = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_REG_ORG_MANAGER,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    var teamMember = new TeamMember(regulatorTeam, regulatorOrganisationManager.getLinkedPerson(), Set.of(
        regulatorOrganisationManagerRole
    ));

    when(teamService.isPersonMemberOfRegulatorTeam(regulatorOrganisationManager.getLinkedPerson())).thenReturn(true);
    when(teamService.getRegulatorTeam()).thenReturn(regulatorTeam);
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorOrganisationManager.getLinkedPerson())).thenReturn(
        Optional.of(teamMember)
    );

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(regulatorOrganisationManager);

    assertThat(manageableTeamTypeMap).containsExactly(
        entry(
            ViewableTeamType.REGULATOR_TEAM,
            ReverseRouter.route(on(PortalTeamManagementController.class)
                .renderTeamMembers(teamService.getRegulatorTeam().getId(), null))
        ),
        entry(
            ViewableTeamType.ORGANISATION_TEAMS,
            ViewableTeamType.ORGANISATION_TEAMS.getLinkUrl()
        )
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenRegulatorTeamAdministrator_onlyRegulatorTeamReturned() {

    var regulatorTeamAdministrator = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_REGULATOR_ADMIN,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    var teamMember = new TeamMember(regulatorTeam, regulatorTeamAdministrator.getLinkedPerson(), Set.of(
        teamAdministratorRole));

    when(teamService.isPersonMemberOfRegulatorTeam(regulatorTeamAdministrator.getLinkedPerson())).thenReturn(true);
    when(teamService.getRegulatorTeam()).thenReturn(regulatorTeam);
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorTeamAdministrator.getLinkedPerson())).thenReturn(
        Optional.of(teamMember)
    );

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(regulatorTeamAdministrator);

    assertThat(manageableTeamTypeMap).containsExactly(
        entry(
            ViewableTeamType.REGULATOR_TEAM,
            ReverseRouter.route(on(PortalTeamManagementController.class)
                .renderTeamMembers(teamService.getRegulatorTeam().getId(), null))
        )
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenInRegulatorTeamNotAsTeamOrOrgAdmin_onlyRegulatorTeamReturned() {

    var regulatorTeamAdministrator = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    var teamMember = new TeamMember(regulatorTeam, regulatorTeamAdministrator.getLinkedPerson(), Set.of(
        teamAdministratorRole));

    when(teamService.isPersonMemberOfRegulatorTeam(regulatorTeamAdministrator.getLinkedPerson())).thenReturn(true);
    when(teamService.getRegulatorTeam()).thenReturn(regulatorTeam);
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorTeamAdministrator.getLinkedPerson())).thenReturn(
        Optional.of(teamMember)
    );

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(regulatorTeamAdministrator);

    assertThat(manageableTeamTypeMap).containsExactly(
        entry(
            ViewableTeamType.REGULATOR_TEAM,
            ReverseRouter.route(on(PortalTeamManagementController.class)
                .renderTeamMembers(teamService.getRegulatorTeam().getId(), null))
        )
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenRegulatorTeamAdministratorAndOrganisationManager_thenBothTeamsReturned() {

    var regulatorTeamAdministrator = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_REGULATOR_ADMIN,
        UserPrivilege.PATHFINDER_REGULATOR_ADMIN,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    var teamMember = new TeamMember(
        regulatorTeam,
        regulatorTeamAdministrator.getLinkedPerson(),
        Set.of(regulatorOrganisationManagerRole, teamAdministratorRole)
    );

    when(teamService.isPersonMemberOfRegulatorTeam(regulatorTeamAdministrator.getLinkedPerson())).thenReturn(true);
    when(teamService.getRegulatorTeam()).thenReturn(regulatorTeam);
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorTeamAdministrator.getLinkedPerson())).thenReturn(
        Optional.of(teamMember)
    );

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(regulatorTeamAdministrator);

    assertThat(manageableTeamTypeMap).containsExactly(
        entry(
            ViewableTeamType.REGULATOR_TEAM,
            ReverseRouter.route(on(PortalTeamManagementController.class)
                .renderTeamMembers(teamService.getRegulatorTeam().getId(), null))
        ),
        entry(
            ViewableTeamType.ORGANISATION_TEAMS,
            ViewableTeamType.ORGANISATION_TEAMS.getLinkUrl()
        )
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenOrganisationTeamAdministrator_onlyOrganisationTeamReturned() {

    var organisationTeamAdministrator = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_ORG_ADMIN,
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    when(teamService.isPersonMemberOfRegulatorTeam(organisationTeamAdministrator.getLinkedPerson())).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationTeamAdministrator.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam));

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(organisationTeamAdministrator);

    assertThat(manageableTeamTypeMap).containsExactly(
        entry(
            ViewableTeamType.ORGANISATION_TEAMS,
            ViewableTeamType.ORGANISATION_TEAMS.getLinkUrl()
        )
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenInOrganisationTeamNotAsAdministrator_organisationTeamReturned() {

    var organisationTeamMember = UserTestingUtil.getAuthenticatedUserAccount(List.of(
        UserPrivilege.PATHFINDER_TEAM_VIEWER
    ));

    when(teamService.isPersonMemberOfRegulatorTeam(organisationTeamMember.getLinkedPerson())).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(organisationTeamMember.getLinkedPerson()))
        .thenReturn(List.of(organisationTeam));

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(organisationTeamMember);

    assertThat(manageableTeamTypeMap).containsExactly(
        entry(
            ViewableTeamType.ORGANISATION_TEAMS,
            ViewableTeamType.ORGANISATION_TEAMS.getLinkUrl()
        )
    );
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenOrganisationUserDoesntHaveTeamViewerPrivilege_noTeamReturned() {

    var unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
        Collections.emptyList()
    );

    when(teamService.isPersonMemberOfRegulatorTeam(unauthenticatedUser.getLinkedPerson())).thenReturn(false);
    when(teamService.getOrganisationTeamsPersonIsMemberOf(unauthenticatedUser.getLinkedPerson()))
        .thenReturn(Collections.emptyList());

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(unauthenticatedUser);

    assertThat(manageableTeamTypeMap).isEmpty();
  }

  @Test
  public void getManageTeamTypesAndUrlsForUser_whenRegulatorUserDoesntHaveTeamViewerPrivilege_noTeamReturned() {

    var unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
        Collections.emptyList()
    );

    when(teamService.isPersonMemberOfRegulatorTeam(unauthenticatedUser.getLinkedPerson())).thenReturn(true);
    when(teamService.getRegulatorTeam()).thenReturn(regulatorTeam);
    when(teamService.getMembershipOfPersonInTeam(regulatorTeam, unauthenticatedUser.getLinkedPerson())).thenReturn(
        Optional.empty()
    );

    var manageableTeamTypeMap = manageTeamService.getViewableTeamTypesAndUrlsForUser(unauthenticatedUser);

    assertThat(manageableTeamTypeMap).isEmpty();
  }

}
