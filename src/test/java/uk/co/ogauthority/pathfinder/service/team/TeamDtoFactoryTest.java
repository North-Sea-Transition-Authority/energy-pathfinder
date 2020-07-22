package uk.co.ogauthority.pathfinder.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalSystemPrivilegeDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamMemberDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.repository.PersonRepository;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationAccessor;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.TeamMember;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TeamDtoFactoryTest {

  @Mock
  private PortalOrganisationAccessor portalOrganisationAccessor;

  @Mock
  private PersonRepository personRepository;

  private TeamDtoFactory teamDtoFactory;

  private PortalTeamDto regulatorTeamDto;
  private PortalTeamDto organisationTeamDto1;
  private PortalTeamDto organisationTeamDto2;
  private PortalOrganisationGroup portalOrganisationGroup1;
  private PortalOrganisationGroup portalOrganisationGroup2;
  private OrganisationTeam organisationTeam1;
  private Person orgMember1;
  private Person orgMember2;
  private PortalTeamMemberDto orgTeamMember1;
  private PortalRoleDto role1;
  private PortalRoleDto role2;

  @Before
  public void setup() {
    teamDtoFactory = new TeamDtoFactory(portalOrganisationAccessor, personRepository);

    setupPortalTeamDtos();
    organisationTeam1 = TeamTestingUtil.getOrganisationTeam(portalOrganisationGroup1);

    orgMember1 = new Person(1, "Person", "One", "person@onw.com", "0");
    orgMember2 = new Person(2, "Person", "Two", "person@two.com", "0");

    setupOrgTeamMember();

    when(portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(any()))
        .thenReturn(List.of(portalOrganisationGroup1));
  }

  private void setupPortalTeamDtos() {
    portalOrganisationGroup1 = TeamTestingUtil.generateOrganisationGroup(100, "GROUP1", "GRP1");
    portalOrganisationGroup2 = TeamTestingUtil.generateOrganisationGroup(200, "GROUP2", "GRP2");

    regulatorTeamDto = new PortalTeamDto(10, "NAME", "DESC", TeamType.REGULATOR.getPortalTeamType(), null);
    organisationTeamDto1 = new PortalTeamDto(20, "NAME1", "DESC1", TeamType.ORGANISATION.getPortalTeamType(), portalOrganisationGroup1.getUrefValue());
    organisationTeamDto2 = new PortalTeamDto(30, "NAME2", "DESC2", TeamType.ORGANISATION.getPortalTeamType(), portalOrganisationGroup2.getUrefValue());
  }

  private void setupOrgTeamMember() {
    role1 = new PortalRoleDto(organisationTeamDto1.getResId(), "role1", "title1", "desc1", 10);
    role2 = new PortalRoleDto(organisationTeamDto1.getResId(), "role2", "title2", "desc2", 20);
    orgTeamMember1 = new PortalTeamMemberDto(orgMember1.getId(), Set.of(role1, role2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void createTeam_errorsWhenTeamTypeNotSupported() {
    var unsupportedTeamType = new PortalTeamDto(1, "NAME", "DESC", "UNSUPPORTED", null);
    teamDtoFactory.createTeam(unsupportedTeamType);
  }

  @Test
  public void createTeam_createsRegulatorInstanceWhenExpected() {
    assertThat(teamDtoFactory.createTeam(regulatorTeamDto)).isInstanceOf(RegulatorTeam.class);
  }

  @Test
  public void createTeam_createsOrganisationInstanceWhenExpected() {
    assertThat(teamDtoFactory.createTeam(organisationTeamDto1)).isInstanceOf(OrganisationTeam.class);
  }

  @Test(expected = TeamFactoryException.class)
  public void createRegulatorTeam_errorsWhenGivenUnexpectedTeamTypeDto() {
    teamDtoFactory.createRegulatorTeam(organisationTeamDto1);
  }

  @Test
  public void createRegulatorTeam_mapsDtoAsExpected() {
    RegulatorTeam regulatorTeam = teamDtoFactory.createRegulatorTeam(regulatorTeamDto);
    assertThat(regulatorTeam.getId()).isEqualTo(regulatorTeamDto.getResId());
    assertThat(regulatorTeam.getName()).isEqualTo(regulatorTeamDto.getName());
    assertThat(regulatorTeam.getDescription()).isEqualTo(regulatorTeamDto.getDescription());
    assertThat(regulatorTeam.getType()).isEqualTo(TeamType.REGULATOR);
  }

  @Test
  public void createOrganisationTeam_mapsDtoAsExpected() {
    List<String> expectedUrefList = List.of(portalOrganisationGroup1.getUrefValue());
    organisationTeam1 = teamDtoFactory.createOrganisationTeam(organisationTeamDto1);
    verify(portalOrganisationAccessor, times(1)).getAllOrganisationGroupsWithUrefIn(eq(expectedUrefList));
    verifyNoMoreInteractions(portalOrganisationAccessor);

    assertThat(organisationTeam1.getId()).isEqualTo(organisationTeamDto1.getResId());
    assertThat(organisationTeam1.getName()).isEqualTo(portalOrganisationGroup1.getName());
    assertThat(organisationTeam1.getDescription()).isEqualTo(organisationTeamDto1.getName());
    assertThat(organisationTeam1.getPortalOrganisationGroup()).isEqualTo(portalOrganisationGroup1);
  }

  @Test(expected = TeamFactoryException.class)
  public void createOrganisationTeam_errorsWhenAssociatedUrefNotValidOrganisationGroup() {
    when(portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(any())).thenReturn(List.of());
    teamDtoFactory.createOrganisationTeam(organisationTeamDto1);
  }

  @Test(expected = TeamFactoryException.class)
  public void createOrganisationTeam_errorsWhenAssociatedUrefNotScoped() {
    when(portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(any())).thenReturn(List.of());
    organisationTeamDto1 = new PortalTeamDto(1, "NAME", "DESC", TeamType.ORGANISATION.getPortalTeamType(), null);

    teamDtoFactory.createOrganisationTeam(organisationTeamDto1);
  }

  @Test
  public void createOrganisationTeamList_verifyOrganisationsRetrievedInOneHit() {
    List<String> expectedUrefList = List.of(portalOrganisationGroup1.getUrefValue(), portalOrganisationGroup2.getUrefValue());
    when(portalOrganisationAccessor.getAllOrganisationGroupsWithUrefIn(any()))
        .thenReturn(List.of(portalOrganisationGroup1, portalOrganisationGroup2));

    teamDtoFactory.createOrganisationTeamList(List.of(organisationTeamDto1, organisationTeamDto2));
    verify(portalOrganisationAccessor, times(1)).getAllOrganisationGroupsWithUrefIn(eq(expectedUrefList));
    verifyNoMoreInteractions(portalOrganisationAccessor);
  }

  @Test
  public void createTeamMemberList_verifyTeamMemberPersonsGotInOneHit() {
    PortalTeamMemberDto orgTeamMember1 = TeamTestingUtil.createPortalTeamMember(orgMember1, organisationTeam1);
    PortalTeamMemberDto orgTeamMember2 = TeamTestingUtil.createPortalTeamMember(orgMember2, organisationTeam1);
    List<PortalTeamMemberDto> portalTeamMemberDtos = List.of(orgTeamMember1, orgTeamMember2);

    List<TeamMember> TeamMembers = teamDtoFactory.createTeamMemberList(portalTeamMemberDtos, organisationTeam1);

    assertThat(TeamMembers).hasSize(2);
    verify(personRepository, times(1))
        .findAllByIdIn(eq(Set.of(orgMember1.getId().asInt(), orgMember2.getId().asInt())));
  }

  @Test
  public void createTeamMember_mapsDtoPropertiesAsExpected() {
    TeamMember teamMember = teamDtoFactory.createTeamMember(orgTeamMember1, orgMember1, organisationTeam1);

    assertThat(teamMember.getPerson()).isEqualTo(orgMember1);
    assertThat(teamMember.getTeam()).isEqualTo(organisationTeam1);
  }

  @Test
  public void createTeamMember_mapsRolesAsExpected() {
    TeamMember teamMember = teamDtoFactory.createTeamMember(orgTeamMember1, orgMember1, organisationTeam1);

    Role mappedRole1 = teamMember.getRoleSet().stream()
        .filter(r -> r.getName().equals(role1.getName()))
        .findFirst()
        .get();

    Role mappedRole2 = teamMember.getRoleSet().stream()
        .filter(r -> r.getName().equals(role2.getName()))
        .findFirst()
        .get();

    assertRoleMappedAsExpected(mappedRole1, role1);
    assertRoleMappedAsExpected(mappedRole2, role2);
  }

  private void assertRoleMappedAsExpected(Role mappedRole, PortalRoleDto roleDtoToMap) {
    assertThat(mappedRole.getName()).isEqualTo(roleDtoToMap.getName());
    assertThat(mappedRole.getDescription()).isEqualTo(roleDtoToMap.getDescription());
    assertThat(mappedRole.getTitle()).isEqualTo(roleDtoToMap.getTitle());
    assertThat(mappedRole.getDisplaySequence()).isEqualTo(roleDtoToMap.getDisplaySequence());
  }


  @Test
  public void createUserPrivilegeSet_mapsPrivAsExpected_andRemovesDuplicates() {
    Set<UserPrivilege> privs = teamDtoFactory.createUserPrivilegeSet(List.of(
        new PortalSystemPrivilegeDto(TeamType.REGULATOR.getPortalTeamType(), "SomeRole",
            UserPrivilege.PATHFINDER_WORK_AREA.name()),
        new PortalSystemPrivilegeDto(TeamType.ORGANISATION.getPortalTeamType(), "SomeOtherRole",
            UserPrivilege.PATHFINDER_WORK_AREA.name()),
        new PortalSystemPrivilegeDto(TeamType.ORGANISATION.getPortalTeamType(), "DifferentRole",
            "unknown privilege")
    ));

    assertThat(privs).hasSize(1);
    assertThat(privs).containsExactly(UserPrivilege.PATHFINDER_WORK_AREA);
  }

}
