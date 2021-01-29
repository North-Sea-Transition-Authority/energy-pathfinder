package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement.AddedToTeamEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement.RemovedFromTeamEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.teammanagement.TeamRolesUpdatedEmailProperties;
import uk.co.ogauthority.pathfinder.model.team.Team;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TeamManagementEmailServiceTest {

  private static final Team REGULATOR_TEAM = TeamTestingUtil.getRegulatorTeam();
  private static final Team ORGANISATION_TEAM = TeamTestingUtil.getOrganisationTeam(ProjectOperatorTestUtil.ORG_GROUP);
  private static final Person PERSON = UserTestingUtil.getPerson();
  private static final String ROLES_CSV = "Access manager, Project submitter";
  private static final WebUserAccount ACTION_PERFORMED_BY_USER = UserTestingUtil.getWebUserAccount();
  private static final String SERVICE_NAME = "Pathfinder";
  private static final String SERVICE_LOGIN_URL = "service-url";

  @Mock
  private EmailService emailService;

  @Mock
  private EmailLinkService emailLinkService;

  private TeamManagementEmailService teamManagementEmailService;

  @Before
  public void setup() {
    teamManagementEmailService = new TeamManagementEmailService(emailService, emailLinkService, SERVICE_NAME);

    when(emailLinkService.getWorkAreaUrl()).thenReturn(SERVICE_LOGIN_URL);
  }

  @Test
  public void sendAddedToTeamEmail_whenRegulatorTeam() {
    teamManagementEmailService.sendAddedToTeamEmail(REGULATOR_TEAM, PERSON, ROLES_CSV, ACTION_PERFORMED_BY_USER);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(PERSON.getEmailAddress()));
    AddedToTeamEmailProperties emailProperties = (AddedToTeamEmailProperties) emailCaptor.getValue();
    assertAddedToTeamEmailProperties(emailProperties, REGULATOR_TEAM.getName());
  }

  @Test
  public void sendAddedToTeamEmail_whenOrganisationTeam() {
    teamManagementEmailService.sendAddedToTeamEmail(ORGANISATION_TEAM, PERSON, ROLES_CSV, ACTION_PERFORMED_BY_USER);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(PERSON.getEmailAddress()));
    AddedToTeamEmailProperties emailProperties = (AddedToTeamEmailProperties) emailCaptor.getValue();
    assertAddedToTeamEmailProperties(emailProperties, String.format("%s %s team", ORGANISATION_TEAM.getName(), SERVICE_NAME));
  }

  private void assertAddedToTeamEmailProperties(AddedToTeamEmailProperties emailProperties, String teamName) {
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            "TEAM_NAME", teamName,
            "ADDED_BY_USER_NAME", ACTION_PERFORMED_BY_USER.getFullName(),
            "ROLES_CSV", ROLES_CSV,
            "SERVICE_LOGIN_URL", emailLinkService.getWorkAreaUrl(),
            "TEST_EMAIL", "no",
            "RECIPIENT_IDENTIFIER", PERSON.getForename(),
            "SIGN_OFF_IDENTIFIER", EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER
        )
    );
  }

  @Test
  public void sendTeamRolesUpdatedEmail_whenRegulatorTeam() {
    teamManagementEmailService.sendTeamRolesUpdatedEmail(REGULATOR_TEAM, PERSON, ROLES_CSV, ACTION_PERFORMED_BY_USER);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(PERSON.getEmailAddress()));
    TeamRolesUpdatedEmailProperties emailProperties = (TeamRolesUpdatedEmailProperties) emailCaptor.getValue();
    assertTeamRolesUpdatedEmailProperties(emailProperties, REGULATOR_TEAM.getName());
  }

  @Test
  public void sendTeamRolesUpdatedEmail_whenOrganisationTeam() {
    teamManagementEmailService.sendTeamRolesUpdatedEmail(ORGANISATION_TEAM, PERSON, ROLES_CSV, ACTION_PERFORMED_BY_USER);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(PERSON.getEmailAddress()));
    TeamRolesUpdatedEmailProperties emailProperties = (TeamRolesUpdatedEmailProperties) emailCaptor.getValue();
    assertTeamRolesUpdatedEmailProperties(emailProperties, String.format("%s %s team", ORGANISATION_TEAM.getName(), SERVICE_NAME));
  }

  private void assertTeamRolesUpdatedEmailProperties(TeamRolesUpdatedEmailProperties emailProperties, String teamName) {
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            "TEAM_NAME", teamName,
            "UPDATED_BY_USER_NAME", ACTION_PERFORMED_BY_USER.getFullName(),
            "ROLES_CSV", ROLES_CSV,
            "SERVICE_LOGIN_URL", emailLinkService.getWorkAreaUrl(),
            "TEST_EMAIL", "no",
            "RECIPIENT_IDENTIFIER", PERSON.getForename(),
            "SIGN_OFF_IDENTIFIER", EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER
        )
    );
  }

  @Test
  public void sendRemovedFromTeamEmail_whenRegulatorTeam() {
    teamManagementEmailService.sendRemovedFromTeamEmail(REGULATOR_TEAM, PERSON, ACTION_PERFORMED_BY_USER);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(PERSON.getEmailAddress()));
    RemovedFromTeamEmailProperties emailProperties = (RemovedFromTeamEmailProperties) emailCaptor.getValue();
    assertRemovedFromTeamEmailProperties(emailProperties, REGULATOR_TEAM.getName());
  }

  @Test
  public void sendRemovedFromTeamEmail_whenOrganisationTeam() {
    teamManagementEmailService.sendRemovedFromTeamEmail(ORGANISATION_TEAM, PERSON, ACTION_PERFORMED_BY_USER);

    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(PERSON.getEmailAddress()));
    RemovedFromTeamEmailProperties emailProperties = (RemovedFromTeamEmailProperties) emailCaptor.getValue();
    assertRemovedFromTeamEmailProperties(emailProperties, String.format("%s %s team", ORGANISATION_TEAM.getName(), SERVICE_NAME));
  }

  private void assertRemovedFromTeamEmailProperties(RemovedFromTeamEmailProperties emailProperties, String teamName) {
    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            "TEAM_NAME", teamName,
            "REMOVED_BY_USER_NAME", ACTION_PERFORMED_BY_USER.getFullName(),
            "TEST_EMAIL", "no",
            "RECIPIENT_IDENTIFIER", PERSON.getForename(),
            "SIGN_OFF_IDENTIFIER", EmailProperties.DEFAULT_SIGN_OFF_IDENTIFIER
        )
    );
  }
}
