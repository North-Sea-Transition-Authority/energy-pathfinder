package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class OperatorEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private EmailLinkService emailLinkService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  private OperatorEmailService operatorEmailService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator();
  private final String projectName = "Operator email service project";
  private final List<Person> people = List.of(
      new Person(1, "Someone", "Example", "someone@example.com", "123"),
      new Person(2, "Someone", "Else", "someone.else@example.com", "123")
  );
  private final String projectUrl = "testurl";

  @Before
  public void setUp() {
    operatorEmailService = new OperatorEmailService(
        emailService,
        emailLinkService,
        projectInformationService,
        projectOperatorService,
        portalTeamAccessor
    );

    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectName);
    when(emailLinkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(
        projectOperator
    );
    var organisationGroup = projectOperator.getOrganisationGroup();
    var organisationTeam = TeamTestingUtil.getOrganisationTeam(organisationGroup);
    var portalTeamDto = TeamTestingUtil.portalTeamDtoFrom(organisationTeam);
    when(portalTeamAccessor.findPortalTeamByOrganisationGroup(projectOperator.getOrganisationGroup())).thenReturn(
        Optional.of(portalTeamDto)
    );
    when(portalTeamAccessor.getPortalTeamMemberPeople(List.of(portalTeamDto.getResId()))).thenReturn(people);
  }

  @Test
  public void sendUpdateRequestedEmail_whenDeadlineDateNotNull() {
    var updateReason = "Test update reason";
    var deadlineDate = LocalDate.now();

    operatorEmailService.sendUpdateRequestedEmail(projectDetail, updateReason, deadlineDate);

    people.forEach(person -> {
      ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
      verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(person.getEmailAddress()));
      ProjectUpdateRequestedEmailProperties emailProperties = (ProjectUpdateRequestedEmailProperties) emailCaptor.getValue();

      final var expectedEmailProperties = new HashMap<String, Object>();
      expectedEmailProperties.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, person.getForename());
      expectedEmailProperties.put("PROJECT_NAME", projectName);
      expectedEmailProperties.put("UPDATE_REASON", updateReason);
      expectedEmailProperties.put("DEADLINE_TEXT", String.format("An update to this project is due by %s.", DateUtil.formatDate(deadlineDate)));
      expectedEmailProperties.put("PROJECT_URL", projectUrl);
      assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
    });
  }

  @Test
  public void sendUpdateRequestedEmail_whenDeadlineDateNull() {
    var updateReason = "Test update reason";

    operatorEmailService.sendUpdateRequestedEmail(projectDetail, updateReason, null);

    people.forEach(person -> {
      ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
      verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(person.getEmailAddress()));
      ProjectUpdateRequestedEmailProperties emailProperties = (ProjectUpdateRequestedEmailProperties) emailCaptor.getValue();

      final var expectedEmailProperties = new HashMap<String, Object>();
      expectedEmailProperties.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, person.getForename());
      expectedEmailProperties.put("PROJECT_NAME", projectName);
      expectedEmailProperties.put("UPDATE_REASON", updateReason);
      expectedEmailProperties.put("DEADLINE_TEXT", "");
      expectedEmailProperties.put("PROJECT_URL", projectUrl);

      assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
    });
  }

  @Test
  public void sendProjectTransferEmails() {
    var fromOrganisationGroup = ProjectOperatorTestUtil.getOrgGroup("Old operator");
    var toOrganisationGroup = ProjectOperatorTestUtil.getOrgGroup("New operator");
    var transferReason = "Test transfer reason";

    var fromTeamMemberPeople = List.of(
        new Person(1, "From team", "Member 1", "fromteammember1@example.com", "123"),
        new Person(2, "From team", "Member 2", "fromteammember2@example.com", "123")
    );
    var toTeamMemberPeople = List.of(
        new Person(1, "To team", "Member 1", "toteammember1@example.com", "123"),
        new Person(2, "To team", "Member 2", "toteammember2@example.com", "123")
    );

    var fromTeam = TeamTestingUtil.getOrganisationTeam(200, fromOrganisationGroup);
    var toTeam = TeamTestingUtil.getOrganisationTeam(400, toOrganisationGroup);

    var fromPortalTeamDto = TeamTestingUtil.portalTeamDtoFrom(fromTeam);
    var toPortalTeamDto = TeamTestingUtil.portalTeamDtoFrom(toTeam);

    when(portalTeamAccessor.findPortalTeamByOrganisationGroup(fromOrganisationGroup)).thenReturn(
        Optional.of(fromPortalTeamDto)
    );
    when(portalTeamAccessor.findPortalTeamByOrganisationGroup(toOrganisationGroup)).thenReturn(
        Optional.of(toPortalTeamDto)
    );
    when(portalTeamAccessor.getPortalTeamMemberPeople(List.of(fromPortalTeamDto.getResId()))).thenReturn(fromTeamMemberPeople);
    when(portalTeamAccessor.getPortalTeamMemberPeople(List.of(toPortalTeamDto.getResId()))).thenReturn(toTeamMemberPeople);

    operatorEmailService.sendProjectTransferEmails(projectDetail, fromOrganisationGroup, toOrganisationGroup, transferReason);

    fromTeamMemberPeople.forEach(person -> {
      ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
      verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(person.getEmailAddress()));
      OutgoingOperatorProjectTransferEmailProperties emailProperties = (OutgoingOperatorProjectTransferEmailProperties) emailCaptor.getValue();

      final var expectedEmailProperties = new HashMap<String, Object>();
      expectedEmailProperties.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, person.getForename());
      expectedEmailProperties.put("PROJECT_NAME", projectName);
      expectedEmailProperties.put("TRANSFER_REASON", transferReason);
      expectedEmailProperties.put("NEW_OPERATOR_NAME", toOrganisationGroup.getName());

      assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
    });

    toTeamMemberPeople.forEach(person -> {
      ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
      verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(person.getEmailAddress()));
      IncomingOperatorProjectTransferEmailProperties emailProperties = (IncomingOperatorProjectTransferEmailProperties) emailCaptor.getValue();

      final var expectedEmailProperties = new HashMap<String, Object>();
      expectedEmailProperties.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, person.getForename());
      expectedEmailProperties.put("PROJECT_NAME", projectName);
      expectedEmailProperties.put("TRANSFER_REASON", transferReason);
      expectedEmailProperties.put("PREVIOUS_OPERATOR_NAME", fromOrganisationGroup.getName());
      expectedEmailProperties.put("PROJECT_URL", projectUrl);

      assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
    });
  }
}
