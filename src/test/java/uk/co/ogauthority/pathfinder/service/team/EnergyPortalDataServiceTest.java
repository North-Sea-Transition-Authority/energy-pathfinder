package uk.co.ogauthority.pathfinder.service.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportal.serviceproviders.epmq.ScopeType;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamDto;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamTypeRoleDto;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderUserTeamRolesDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamDto;
import uk.co.ogauthority.pathfinder.energyportal.model.dto.team.PortalTeamTypeRoleDto;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.team.OrganisationTeam;
import uk.co.ogauthority.pathfinder.model.team.RegulatorTeam;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.model.team.TeamType;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@ExtendWith(MockitoExtension.class)
class EnergyPortalDataServiceTest {

  @Mock
  private TeamDtoFactory teamDtoFactory;

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  @Mock
  private WebUserAccountService webUserAccountService;

  @InjectMocks
  private EnergyPortalDataService energyPortalDataService;

  private final PortalTeamDto organisationPortalTeam = new PortalTeamDto(
      1,
      "orgTeam",
      "description",
      TeamType.ORGANISATION.getPortalTeamType(),
      "primaryTeamUsage"
  );

  private final PortalOrganisationGroup portalOrganisationGroup = TeamTestingUtil
      .generateOrganisationGroup(50, "name", "shortName");

  private final OrganisationTeam organisationTeam = new OrganisationTeam(
      organisationPortalTeam.getResId(),
      organisationPortalTeam.getName(),
      organisationPortalTeam.getDescription(),
      portalOrganisationGroup
  );

  private final PortalTeamDto regulatorPortalTeam = new PortalTeamDto(
      2,
      "regTeam",
      "description",
      TeamType.REGULATOR.getPortalTeamType(),
      "primaryTeamUsage"
  );
  private final RegulatorTeam regulatorTeam = new RegulatorTeam(
      regulatorPortalTeam.getResId(),
      regulatorPortalTeam.getName(),
      regulatorPortalTeam.getDescription()
  );

  private final PortalRoleDto adminRole = new PortalRoleDto(
      99999,
      Role.TEAM_ADMINISTRATOR_ROLE_NAME,
      "Team admin",
      "description (Team admin)",
      10
  );

  private final PortalRoleDto nonAdminRole = new PortalRoleDto(
      99999,
      "NON_ADMIN",
      "Non team admin",
      "description",
      20
  );

  @Test
  void getServiceProviderTeamDtos() {
    when(portalTeamAccessor.getAllPortalTeams())
        .thenReturn(List.of(organisationPortalTeam, regulatorPortalTeam));

    when(teamDtoFactory.createOrganisationTeamList(List.of(organisationPortalTeam)))
        .thenReturn(List.of(organisationTeam));

    var expectedDto1 = new ServiceProviderTeamDto(
        "1",
        "50",
        ScopeType.ORGANISATION_GROUP,
        TeamType.ORGANISATION.name()
    );
    var expectedDto2 = new ServiceProviderTeamDto(
        "2",
        null,
        ScopeType.ORGANISATION_GROUP,
        TeamType.REGULATOR.name()
    );

    assertThat(energyPortalDataService.getServiceProviderTeamDtos())
        .containsExactlyInAnyOrder(expectedDto1, expectedDto2);
  }

  @Test
  void getTeamTypeToServiceProviderTeamTypeRoleDtos() {
    var regulatorAdminRole = new PortalTeamTypeRoleDto(
        TeamType.REGULATOR.getPortalTeamType(),
        adminRole.getName(),
        adminRole.getTitle(),
        adminRole.getDescription(),
        adminRole.getDisplaySequence()
    );
    var regulatorNonAdminRole = new PortalTeamTypeRoleDto(
        TeamType.REGULATOR.getPortalTeamType(),
        nonAdminRole.getName(),
        nonAdminRole.getTitle(),
        nonAdminRole.getDescription(),
        nonAdminRole.getDisplaySequence()
    );
    var organisationAdminRole = new PortalTeamTypeRoleDto(
        TeamType.ORGANISATION.getPortalTeamType(),
        adminRole.getName(),
        adminRole.getTitle(),
        adminRole.getDescription(),
        adminRole.getDisplaySequence()
    );

    when(portalTeamAccessor.getAllPortalTeamTypeRoles())
        .thenReturn(List.of(organisationAdminRole, regulatorNonAdminRole, regulatorAdminRole));


    var regulatorServiceRoleDtos = Set.of(
        createServiceRoleDto(regulatorAdminRole, true),
        createServiceRoleDto(regulatorNonAdminRole, false)
    );

    var organisationServiceRoleDtos = Set.of(
        createServiceRoleDto(organisationAdminRole, true)
    );

    var result = energyPortalDataService.getTeamTypeToServiceProviderTeamTypeRoleDtos();

    assertThat(result)
        .hasSize(2)
        .contains(
            Map.entry(TeamType.REGULATOR.name(), regulatorServiceRoleDtos),
            Map.entry(TeamType.ORGANISATION.name(), organisationServiceRoleDtos)
        );

  }

  @Test
  void getTeamTypes() {
    assertThat(energyPortalDataService.getTeamTypes()).isEqualTo(Set.of(
        TeamType.REGULATOR.name(),
        TeamType.ORGANISATION.name()
    ));
  }

  @Test
  void getServiceProviderUserTeamRolesDtos() {
    var person1 = new Person(1, "forename1", "surname1", "emailAddress1", "phone1");
    var person2 = new Person(2, "forename2", "surname2", "emailAddress2", "phone2");

    when(portalTeamAccessor.getAllPersonTeamRoles())
        .thenReturn(
            List.of(
                new PersonTeamRoleDto(
                    1,
                    organisationTeam.getId(),
                    organisationPortalTeam.getType(),
                    nonAdminRole.getName()
                ),
                new PersonTeamRoleDto(
                    1,
                    organisationTeam.getId(),
                    organisationPortalTeam.getType(),
                    adminRole.getName()
                ),
                new PersonTeamRoleDto(
                    1,
                    regulatorPortalTeam.getResId(),
                    regulatorPortalTeam.getType(),
                    adminRole.getName()
                ),
                new PersonTeamRoleDto(
                    2,
                    regulatorPortalTeam.getResId(),
                    regulatorPortalTeam.getType(),
                    nonAdminRole.getName()
                ),
                new PersonTeamRoleDto( // Person without a web user account
                    3,
                    regulatorPortalTeam.getResId(),
                    regulatorPortalTeam.getType(),
                    nonAdminRole.getName()
                )
            )
        );

    when(webUserAccountService.findAllByPersonIdIn(Set.of(1, 2, 3)))
        .thenReturn(List.of(new WebUserAccount(10, person1), new WebUserAccount(20, person2)));

    assertThat(energyPortalDataService.getServiceProviderUserTeamRolesDtos())
        .containsExactlyInAnyOrder(
            new ServiceProviderUserTeamRolesDto(
                10L,
                String.valueOf(organisationTeam.getId()),
                TeamType.ORGANISATION.name(),
                Set.of(adminRole.getName(), nonAdminRole.getName())
            ),
            new ServiceProviderUserTeamRolesDto(
                10L,
                String.valueOf(regulatorTeam.getId()),
                TeamType.REGULATOR.name(),
                Set.of(adminRole.getName())
            ),
            new ServiceProviderUserTeamRolesDto(
                20L,
                String.valueOf(regulatorTeam.getId()),
                TeamType.REGULATOR.name(),
                Set.of(nonAdminRole.getName())
            )
        );
  }

  @Test
  void belongsToAnyTeam_returnsTrue_whenUserHasTeamRoles() {
    var person = new Person(1, "forename", "surname", "emailAddress", "phone");
    var webUserAccount = new WebUserAccount(300165, person);

    when(webUserAccountService.getWebUserAccount(300165)).thenReturn(Optional.of(webUserAccount));
    when(portalTeamAccessor.hasAccessToService(person)).thenReturn(true);

    assertThat(energyPortalDataService.belongsToAnyTeam(300165L)).isTrue();
  }

  @Test
  void belongsToAnyTeam_returnsFalse_whenUserHasNoTeamRoles() {
    var person = new Person(1, "forename", "surname", "emailAddress", "phone");
    var webUserAccount = new WebUserAccount(300165, person);

    when(webUserAccountService.getWebUserAccount(300165)).thenReturn(Optional.of(webUserAccount));
    when(portalTeamAccessor.hasAccessToService(person)).thenReturn(false);

    assertThat(energyPortalDataService.belongsToAnyTeam(300165L)).isFalse();
  }

  @Test
  void belongsToAnyTeam_returnsFalse_whenUserNotFound() {
    when(webUserAccountService.getWebUserAccount(300165)).thenReturn(Optional.empty());

    assertThat(energyPortalDataService.belongsToAnyTeam(300165L)).isFalse();
  }

  private ServiceProviderTeamTypeRoleDto createServiceRoleDto(PortalTeamTypeRoleDto role, boolean isAssessManager) {
    return new ServiceProviderTeamTypeRoleDto(
        role.roleName(),
        role.roleTitle(),
        role.roleDescription().replace(" (Team admin)", ""),
        isAssessManager,
        role.roleDisplaySequence()
    );
  }
}