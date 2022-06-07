package uk.co.ogauthority.pathfinder.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamMemberDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.team.OrganisationRole;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorRole;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  @Mock
  private TeamDtoFactory teamDtoFactory;

  @Captor
  private ArgumentCaptor<List<String>> stringListCaptor;

  private TeamService teamService;
  private Person regulatorPerson;
  private Person organisationPerson;
  private RegulatorTeam regulatorTeam;
  private PortalTeamDto regulatorTeamAsPortalTeamDto;
  private PortalTeamMemberDto regulatorTeamMemberDto;
  private List<PortalTeamMemberDto> regulatorTeamMembers;
  private OrganisationTeam organisationTeam1;
  private OrganisationTeam organisationTeam2;
  private PortalTeamDto organisationTeamAsPortalTeamDto1;
  private PortalTeamDto organisationTeamAsPortalTeamDto2;
  private final WebUserAccount someWebUserAccount = new WebUserAccount(99);

  @Before
  public void setup() {
    regulatorPerson = new Person(1, "reg", "person", "reg@person.com", "0");
    organisationPerson = new Person(2, "org", "person", "org@person.com", "0");

    teamService = new TeamService(portalTeamAccessor, teamDtoFactory);

    regulatorTeam = TeamTestingUtil.getRegulatorTeam();
    regulatorTeamAsPortalTeamDto = TeamTestingUtil.portalTeamDtoFrom(regulatorTeam);

    when(portalTeamAccessor.getPortalTeamsByPortalTeamType(TeamType.REGULATOR.getPortalTeamType()))
        .thenReturn(List.of(regulatorTeamAsPortalTeamDto));

    when(portalTeamAccessor.findPortalTeamById(regulatorTeam.getId()))
        .thenReturn(Optional.of(regulatorTeamAsPortalTeamDto));

    when(teamDtoFactory.createRegulatorTeam(regulatorTeamAsPortalTeamDto)).thenReturn(regulatorTeam);

    regulatorTeamMemberDto = TeamTestingUtil.createPortalTeamMember(regulatorPerson, regulatorTeam);

    regulatorTeamMembers = List.of(regulatorTeamMemberDto);
    when(portalTeamAccessor.getPortalTeamMembers(regulatorTeam.getId()))
        .thenReturn(regulatorTeamMembers);

    var organisationGroup1 = TeamTestingUtil.generateOrganisationGroup(10, "Group1", "Group1 Desc");
    var organisationGroup2 = TeamTestingUtil.generateOrganisationGroup(20, "Group2", "Group2 Desc");
    organisationTeam1 = new OrganisationTeam(11, "org1", "org1", organisationGroup1);
    organisationTeam2 = new OrganisationTeam(22, "org2", "org2", organisationGroup2);
    organisationTeamAsPortalTeamDto1 = TeamTestingUtil.portalTeamDtoFrom(organisationTeam1);
    organisationTeamAsPortalTeamDto2 = TeamTestingUtil.portalTeamDtoFrom(organisationTeam2);

    when(portalTeamAccessor.getPortalTeamsByPortalTeamType(TeamType.ORGANISATION.getPortalTeamType()))
        .thenReturn(List.of(organisationTeamAsPortalTeamDto1));
  }

  @Test(expected = RuntimeException.class)
  public void getRegulatorTeam_errorWhenMultipleTeamsFound() {
    when(portalTeamAccessor.getPortalTeamsByPortalTeamType(TeamType.REGULATOR.getPortalTeamType()))
        .thenReturn(List.of(regulatorTeamAsPortalTeamDto, regulatorTeamAsPortalTeamDto));

    teamService.getRegulatorTeam();
  }

  @Test(expected = RuntimeException.class)
  public void getRegulatorTeam_errorWhenZeroTeamsFound() {
    when(portalTeamAccessor.getPortalTeamsByPortalTeamType(TeamType.REGULATOR.getPortalTeamType()))
        .thenReturn(List.of());

    teamService.getRegulatorTeam();
  }

  @Test
  public void getRegulatorTeam_callsExpectedFactoryMethod() {
    teamService.getRegulatorTeam();
    verify(teamDtoFactory, times(1)).createRegulatorTeam(regulatorTeamAsPortalTeamDto);
  }

  @Test
  public void getAllOrganisationTeams_callsExpectedFactoryMethod() {
    teamService.getAllOrganisationTeams();
    verify(teamDtoFactory, times(1)).createOrganisationTeamList(List.of(organisationTeamAsPortalTeamDto1));
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getTeamByResId_errorThrownWhenTeamNotFound() {
    teamService.getTeamByResId(999);
  }

  @Test
  public void getTeamByResId_callsExpectedFactoryMethodWhenTeamFound() {
    when(teamDtoFactory.createTeam(regulatorTeamAsPortalTeamDto)).thenReturn(regulatorTeam);

    teamService.getTeamByResId(regulatorTeam.getId());

    verify(teamDtoFactory, times(1)).createTeam(regulatorTeamAsPortalTeamDto);
  }

  @Test
  public void getTeamMembers_callsServiceMethodsWithExpectedValues() {
    teamService.getTeamMembers(regulatorTeam);

    verify(portalTeamAccessor, times(1)).getPortalTeamMembers(regulatorTeam.getId());
    verify(teamDtoFactory, times(1)).createTeamMemberList(regulatorTeamMembers, regulatorTeam);
  }

  @Test
  public void getMembershipOfPersonInTeam_emptyOptionalWhenNotATeamMember() {
    assertThat(teamService.getMembershipOfPersonInTeam(regulatorTeam, organisationPerson)).isNotPresent();
  }

  @Test
  public void getMembershipOfPersonInTeam_populatedOptionalWhenTeamMember() {
    when(portalTeamAccessor.getPersonTeamMembership(regulatorPerson, regulatorTeam.getId()))
        .thenReturn(Optional.of(regulatorTeamMemberDto));
    when(teamDtoFactory.createTeamMember(regulatorTeamMemberDto, regulatorPerson, regulatorTeam))
        .thenReturn(mock(TeamMember.class));

    assertThat(teamService.getMembershipOfPersonInTeam(regulatorTeam, regulatorPerson)).isNotEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getRegulatorTeamIfPersonInRole_whenNoRolesProvided() {
    teamService.getRegulatorTeamIfPersonInRole(regulatorPerson, Set.of());
  }

  @Test
  public void getRegulatorTeamIfPersonInRole_whenNotInProvidedRoles() {
    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        regulatorPerson,
        TeamType.REGULATOR.getPortalTeamType(),
        List.of(RegulatorRole.ORGANISATION_MANAGER.getPortalTeamRoleName())))
        .thenReturn(List.of());

    assertThat(teamService.getRegulatorTeamIfPersonInRole(regulatorPerson, List.of(RegulatorRole.ORGANISATION_MANAGER)))
        .isEmpty();
  }

  @Test
  public void getRegulatorTeamIfPersonInRole_whenPersonInRoles() {
    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(regulatorPerson),
        eq(TeamType.REGULATOR.getPortalTeamType()),
        any()))
        .thenReturn(List.of(regulatorTeamAsPortalTeamDto));

    assertThat(teamService.getRegulatorTeamIfPersonInRole(regulatorPerson, EnumSet.allOf(RegulatorRole.class)))
        .isNotEmpty();

    verify(portalTeamAccessor).getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(regulatorPerson),
        any(),
        stringListCaptor.capture()
    );

    List<String> expectedRolesArgumentToPortalTeamsAPI = EnumSet.allOf(RegulatorRole.class).stream()
        .map(RegulatorRole::getPortalTeamRoleName)
        .collect(Collectors.toList());

    assertThat(stringListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(expectedRolesArgumentToPortalTeamsAPI);

    verify(teamDtoFactory, times(1)).createRegulatorTeam(regulatorTeamAsPortalTeamDto);
  }

  @Test
  public void getOrganisationTeamListIfPersonInRole_whenPersonInRoles_andSingleTeamMatched() {
    List<PortalTeamDto> foundOrganisationTeams = List.of(organisationTeamAsPortalTeamDto1);

    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(organisationPerson),
        eq(TeamType.ORGANISATION.getPortalTeamType()),
        any()))
        .thenReturn(foundOrganisationTeams);

    when(teamDtoFactory.createOrganisationTeamList(foundOrganisationTeams))
        .thenReturn(List.of(organisationTeam1));

    assertThat(teamService.getOrganisationTeamListIfPersonInRole(organisationPerson, EnumSet.allOf(OrganisationRole.class)))
        .containsExactly(organisationTeam1);

    verify(portalTeamAccessor).getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(organisationPerson),
        any(),
        stringListCaptor.capture()
    );

    List<String> expectedRoles = EnumSet.allOf(OrganisationRole.class).stream()
        .map(OrganisationRole::getPortalTeamRoleName)
        .collect(Collectors.toList());

    assertThat(stringListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(expectedRoles);

    verify(teamDtoFactory, times(1)).createOrganisationTeamList(foundOrganisationTeams);
  }

  @Test
  public void getOrganisationTeamListIfPersonInRole_whenPersonInRoles_andMultipleTeamsMatched() {
    List<PortalTeamDto> foundOrganisationTeams = List.of(organisationTeamAsPortalTeamDto1, organisationTeamAsPortalTeamDto2);

    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(organisationPerson),
        eq(TeamType.ORGANISATION.getPortalTeamType()),
        any()))
        .thenReturn(foundOrganisationTeams);

    when(teamDtoFactory.createOrganisationTeamList(foundOrganisationTeams))
        .thenReturn(List.of(organisationTeam1, organisationTeam2));

    assertThat(teamService.getOrganisationTeamListIfPersonInRole(organisationPerson, EnumSet.allOf(OrganisationRole.class)))
        .containsExactly(organisationTeam1, organisationTeam2);

    verify(teamDtoFactory, times(1)).createOrganisationTeamList(foundOrganisationTeams);
  }

  @Test
  public void getOrganisationTeamListIfPersonInRole_whenPersonInRoles_anNoTeamsMatched() {
    assertThat(teamService.getOrganisationTeamListIfPersonInRole(regulatorPerson, EnumSet.allOf(OrganisationRole.class)))
        .isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getOrganisationTeamListIfPersonInRole_errorWhenNoRolesProvided() {
    teamService.getOrganisationTeamListIfPersonInRole(regulatorPerson, Set.of());
  }

  @Test
  public void addPersonToTeamInRoles_verifyServiceInteraction() {
    var roles = List.of("some_role_1", "some_role_2");
    teamService.addPersonToTeamInRoles(regulatorTeam, organisationPerson, roles, someWebUserAccount);

    verify(portalTeamAccessor, times(1))
        .addPersonToTeamWithRoles(regulatorTeam.getId(), organisationPerson, roles, someWebUserAccount);
  }

  @Test
  public void removePersonFromTeam_verifyServiceInteraction() {
    teamService.removePersonFromTeam(regulatorTeam, regulatorPerson, someWebUserAccount);
    verify(portalTeamAccessor, times(1)).removePersonFromTeam(regulatorTeam.getId(), regulatorPerson, someWebUserAccount);
  }

  @Test
  public void personIsMemberOfTeam_verifyServiceInteractions() {
    teamService.isPersonMemberOfTeam(regulatorPerson, regulatorTeam);
    verify(portalTeamAccessor, times(1)).personIsAMemberOfTeam(regulatorTeam.getId(), regulatorPerson);
  }

  @Test
  public void getAllRolesForTeam_whenMultipleRolesReturned() {
    var mockRole = mock(Role.class);
    when(teamDtoFactory.createRole(any())).thenReturn(mockRole);
    when(portalTeamAccessor.getAllPortalRolesForTeam(regulatorTeam.getId()))
        .thenReturn(List.of(TeamTestingUtil.getTeamAdminRoleDto(regulatorTeam), TeamTestingUtil.getTeamAdminRoleDto(regulatorTeam)));

    assertThat(teamService.getAllRolesForTeam(regulatorTeam)).containsExactly(mockRole, mockRole);
  }

  @Test
  public void getOrganisationTeamsPersonIsMemberOf_verifyServiceInteractions() {
    teamService.getOrganisationTeamsPersonIsMemberOf(organisationPerson);

    verify(portalTeamAccessor, times(1)).getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(organisationPerson),
        eq(TeamType.ORGANISATION.getPortalTeamType()),
        stringListCaptor.capture()
    );

    List<String> expectedRoles = EnumSet.allOf(OrganisationRole.class).stream()
        .map(OrganisationRole::getPortalTeamRoleName)
        .collect(Collectors.toList());

    assertThat(stringListCaptor.getValue()).containsExactlyInAnyOrderElementsOf(expectedRoles);
  }

  @Test
  public void isPersonMemberOfTeamType_whenNoTeamsWherePersonMemberOfTeamType() {
    when(portalTeamAccessor.getNumberOfTeamsWherePersonMemberOfTeamType(organisationPerson, TeamType.ORGANISATION.getPortalTeamType())).thenReturn(0L);

    assertThat(teamService.isPersonMemberOfTeamType(organisationPerson, TeamType.ORGANISATION)).isFalse();
  }

  @Test
  public void isPersonMemberOfTeamType_whenTeamsWherePersonMemberOfTeamType() {
    when(portalTeamAccessor.getNumberOfTeamsWherePersonMemberOfTeamType(organisationPerson, TeamType.ORGANISATION.getPortalTeamType())).thenReturn(1L);

    assertThat(teamService.isPersonMemberOfTeamType(organisationPerson, TeamType.ORGANISATION)).isTrue();
  }

  @Test
  public void getOrganisationGroupsPersonInTeamFor_whenPersonNotPartOfAnyOrganisationTeams_thenEmptyList() {

    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamType(
        regulatorPerson,
        TeamType.ORGANISATION.getPortalTeamType()
    )).thenReturn(Collections.emptyList());

    final var resultingOrganisationGroups = teamService.getOrganisationGroupsPersonInTeamFor(regulatorPerson);

    assertThat(resultingOrganisationGroups).isEmpty();
  }

  @Test
  public void getOrganisationGroupsPersonInTeamFor_whenPersonPartOfAnyOrganisationTeams_thenPopulatedList() {

    final var expectedPortalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "name", "short name");

    final var organisationTeam = new OrganisationTeam(1, "name", "description", expectedPortalOrganisationGroup);
    final var portalTeamDto = TeamTestingUtil.portalTeamDtoFrom(organisationTeam);

    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamType(
        regulatorPerson,
        TeamType.ORGANISATION.getPortalTeamType()
    )).thenReturn(List.of(portalTeamDto));

    when(teamDtoFactory.createOrganisationTeamList(any())).thenReturn(List.of(organisationTeam));

    final var resultingOrganisationGroups = teamService.getOrganisationGroupsPersonInTeamFor(regulatorPerson);

    assertThat(resultingOrganisationGroups).containsExactly(expectedPortalOrganisationGroup);

  }

  @Test
  public void getContributorPortalOrganisationGroup_whenUserBelongsToOneOrgGroup_thenAssertOrganisationGroup() {
    var user = UserTestingUtil.getAuthenticatedUserAccount();
    var portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");
    var organisationTeam = TeamTestingUtil.getOrganisationTeam(portalOrganisationGroup);
    List<PortalTeamDto> foundOrganisationTeams = List.of(TeamTestingUtil.portalTeamDtoFrom(organisationTeam));

    when(portalTeamAccessor.getTeamsWherePersonMemberOfTeamTypeAndHasRoleMatching(
        eq(user.getLinkedPerson()),
        any(),
        any())
    ).thenReturn(foundOrganisationTeams);
    when(teamDtoFactory.createOrganisationTeamList(foundOrganisationTeams)).thenReturn(List.of(organisationTeam));

    assertThat(teamService.getContributorPortalOrganisationGroup(user)).isEqualTo(portalOrganisationGroup);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getContributorPortalOrganisationGroup_whenCantFindUsersPortalOrg_thenThrowException() {
    var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(teamDtoFactory.createOrganisationTeamList(any())).thenReturn(Collections.emptyList());

    teamService.getContributorPortalOrganisationGroup(user);
  }
}
