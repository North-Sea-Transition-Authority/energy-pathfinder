package uk.co.ogauthority.pathfinder.service.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.service.team.PortalTeamAccessor;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.service.email.projecttransfer.ProjectTransferEmailPropertyService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.UpdateRequestedEmailPropertyService;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class OperatorEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private PortalTeamAccessor portalTeamAccessor;

  @Mock
  private UpdateRequestedEmailPropertyService updateRequestedEmailPropertyService;

  @Mock
  private ProjectTransferEmailPropertyService projectTransferEmailPropertyService;

  private OperatorEmailService operatorEmailService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final ProjectOperator projectOperator = ProjectOperatorTestUtil.getOperator();

  private final List<Person> people = List.of(
      new Person(1, "Someone", "Example", "someone@example.com", "123"),
      new Person(2, "Someone", "Else", "someone.else@example.com", "123")
  );

  @Before
  public void setUp() {
    operatorEmailService = new OperatorEmailService(
        emailService,
        projectOperatorService,
        portalTeamAccessor,
        updateRequestedEmailPropertyService,
        projectTransferEmailPropertyService
    );

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
  public void sendUpdateRequestedEmail_whenDeadlineDateNotNull_verifyInteractions() {
    var updateReason = "Test update reason";
    var deadlineDate = LocalDate.now();

    operatorEmailService.sendUpdateRequestedEmail(projectDetail, updateReason, deadlineDate);

    verify(updateRequestedEmailPropertyService, times(1)).getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        DateUtil.formatDate(deadlineDate)
    );

    people.forEach(person ->
      verify(emailService, times(1)).sendEmail(
          any(),
          eq(person.getEmailAddress()),
          eq(person.getForename())
      )
    );
  }

  @Test
  public void sendUpdateRequestedEmail_whenDeadlineDateNull_verifyInteractions() {
    var updateReason = "Test update reason";

    operatorEmailService.sendUpdateRequestedEmail(projectDetail, updateReason, null);

    verify(updateRequestedEmailPropertyService, times(1)).getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        ""
    );

    people.forEach(person ->
      verify(emailService, times(1)).sendEmail(
          any(),
          eq(person.getEmailAddress()),
          eq(person.getForename())
      )
    );
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

    verify(projectTransferEmailPropertyService, times(1)).getIncomingOperatorProjectTransferEmailProperties(
        projectDetail,
        transferReason,
        fromOrganisationGroup.getName()
    );

    fromTeamMemberPeople.forEach(person ->
        verify(emailService, times(1)).sendEmail(any(), eq(person.getEmailAddress()), eq(person.getForename()))
    );

    toTeamMemberPeople.forEach(person ->
      verify(emailService, times(1)).sendEmail(any(), eq(person.getEmailAddress()), eq(person.getForename()))
    );
  }
}
