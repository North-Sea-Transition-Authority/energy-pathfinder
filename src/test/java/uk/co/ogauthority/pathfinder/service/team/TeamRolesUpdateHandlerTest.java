package uk.co.ogauthority.pathfinder.service.team;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamRolesEpasMessage;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderUserTeamRolesDto;
import uk.co.fivium.energyportal.starter.configuration.EnergyPortalAccountsConfigurationProperties;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.team.Role;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@ExtendWith(MockitoExtension.class)
class TeamRolesUpdateHandlerTest {

  private static final String SERVICE_NAME = "service-name";
  private static final Integer TEAM_ID = 1;

  private static final Long USER_WUA_ID = 1L;
  private static final Person PERSON_1 = new Person(USER_WUA_ID.intValue(), "forename1", "surname1", "email1", "phone1");
  private static final WebUserAccount WEB_USER_ACCOUNT_1 = new WebUserAccount(USER_WUA_ID.intValue(), PERSON_1);

  private static final Long INVOKING_USER_WUA_ID = 2L;
  private static final Person PERSON_2 = new Person(INVOKING_USER_WUA_ID.intValue(), "forename2", "surname2", "email2", "phone2");
  private static final WebUserAccount WEB_USER_ACCOUNT_2 = new WebUserAccount(INVOKING_USER_WUA_ID.intValue(), PERSON_2);

  private static final ServiceProviderUserTeamRolesDto SERVICE_PROVIDER_USER_TEAM_ROLES_DTO = new ServiceProviderUserTeamRolesDto(
      USER_WUA_ID,
      TEAM_ID.toString(),
      "teamType",
      Set.of(Role.TEAM_ADMINISTRATOR_ROLE_NAME)
  );

  @Mock
  private TeamService teamService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private final EnergyPortalAccountsConfigurationProperties configurationProperties
      = new EnergyPortalAccountsConfigurationProperties(SERVICE_NAME, "dev1", false);

  private TeamRolesUpdateHandler teamRolesUpdateHandler;

  private ServiceProviderTeamRolesEpasMessage message;

  @BeforeEach
  void setUp() {
    teamRolesUpdateHandler = new TeamRolesUpdateHandler(
        teamService,
        configurationProperties,
        webUserAccountService
    );

    message = new ServiceProviderTeamRolesEpasMessage(
        SERVICE_NAME,
        "correlationId",
        Instant.now(),
        SERVICE_PROVIDER_USER_TEAM_ROLES_DTO,
        INVOKING_USER_WUA_ID
    );
  }

  @Test
  void accept_whenMessageNotForService_thenDoNothing() {
    message.setService("different-service");

    teamRolesUpdateHandler.accept(message);

    verifyNoInteractions(teamService);
  }

  @Test
  void accept_whenInvalidRequester_thenDoNothing() {
    when(webUserAccountService.getWebUserAccount(USER_WUA_ID.intValue())).thenReturn(Optional.empty());

    teamRolesUpdateHandler.accept(message);

    verifyNoInteractions(teamService);
  }

  @Test
  void accept_whenInvalidDecider_thenDoNothing() {
    when(webUserAccountService.getWebUserAccount(USER_WUA_ID.intValue()))
        .thenReturn(Optional.of(WEB_USER_ACCOUNT_1));

    when(webUserAccountService.getWebUserAccount(INVOKING_USER_WUA_ID.intValue())).thenReturn(Optional.empty());

    teamRolesUpdateHandler.accept(message);

    verifyNoInteractions(teamService);
  }

  @Test
  void accept_whenValidMessage_thenUpdateTeamRoles() {
    when(webUserAccountService.getWebUserAccount(USER_WUA_ID.intValue()))
        .thenReturn(Optional.of(WEB_USER_ACCOUNT_1));

    when(webUserAccountService.getWebUserAccount(INVOKING_USER_WUA_ID.intValue()))
        .thenReturn(Optional.of(WEB_USER_ACCOUNT_2));

    var team = TeamTestingUtil.getRegulatorTeam();
    when(teamService.getTeamByResId(TEAM_ID)).thenReturn(team);

    teamRolesUpdateHandler.accept(message);

    verify(teamService).addPersonToTeamInRoles(
        team,
        PERSON_1,
        Set.of(Role.TEAM_ADMINISTRATOR_ROLE_NAME),
        WEB_USER_ACCOUNT_2
    );
  }
}