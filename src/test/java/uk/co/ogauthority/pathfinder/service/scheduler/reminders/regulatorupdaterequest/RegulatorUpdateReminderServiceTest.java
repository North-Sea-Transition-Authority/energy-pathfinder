package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.OrganisationGroupMembership;
import uk.co.ogauthority.pathfinder.energyportal.service.organisation.PortalOrganisationGroupPersonMembershipService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateReminderServiceTest {

  @Mock
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  @Mock
  private PortalOrganisationGroupPersonMembershipService portalOrganisationGroupPersonMembershipService;

  @Mock
  private EmailService emailService;

  @Mock
  private TestRegulatorUpdateReminderService testRegulatorUpdateReminderService;

  private RegulatorUpdateReminderService regulatorUpdateReminderService;

  @Before
  public void setup() {
    regulatorUpdateReminderService = new RegulatorUpdateReminderService(
        List.of(testRegulatorUpdateReminderService),
        regulatorUpdateRequestService,
        portalOrganisationGroupPersonMembershipService,
        emailService
    );
  }

  @Test
  public void processDueReminders_whenNoProjectsWithOutstandingReminders_verifyNoEmailsSent() {

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines()).thenReturn(
        Collections.emptyList()
    );

    regulatorUpdateReminderService.processDueReminders();

    verify(emailService, never()).sendEmail(any(), anyString(), anyString());
  }

  @Test
  public void processDueReminders_whenExceptionPriorToProjectProcessing_verifyExceptionHandle() {

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines())
        .thenThrow(new IllegalArgumentException());

    Assertions.assertDoesNotThrow(() -> regulatorUpdateReminderService.processDueReminders());
  }

  @Test
  public void processDueReminders_whenExceptionInProjectProcessingLoop_verifyExceptionHandle() {

    var regulatorUpdateRequestDto = new RegulatorUpdateRequestProjectDto(new RegulatorUpdateRequest(), new ProjectOperator());

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines()).thenReturn(
        List.of(regulatorUpdateRequestDto)
    );

    when(testRegulatorUpdateReminderService.isReminderDue(any()))
        .thenThrow(new IllegalArgumentException());

    Assertions.assertDoesNotThrow(() -> regulatorUpdateReminderService.processDueReminders());
  }

  @Test
  public void processDueReminders_whenReminderNotDue_verifyNoEmailsSent() {

    var regulatorUpdateRequestDto = new RegulatorUpdateRequestProjectDto(new RegulatorUpdateRequest(), new ProjectOperator());

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines()).thenReturn(
        List.of(regulatorUpdateRequestDto)
    );

    when(testRegulatorUpdateReminderService.isReminderDue(any())).thenReturn(false);

    regulatorUpdateReminderService.processDueReminders();

    verify(emailService, never()).sendEmail(any(), anyString(), anyString());
  }

  @Test
  public void processDueReminders_whenReminderDue_verifyEmailSentToEachTeamMember() {

    var projectOperator = ProjectOperatorTestUtil.getOperator();

    var regulatorUpdateRequestDto = new RegulatorUpdateRequestProjectDto(new RegulatorUpdateRequest(), projectOperator);

    when(regulatorUpdateRequestService.getAllProjectsWithOutstandingRegulatorUpdateRequestsWithDeadlines()).thenReturn(
        List.of(regulatorUpdateRequestDto)
    );

    when(testRegulatorUpdateReminderService.isReminderDue(any())).thenReturn(true);

    var organisationMembership = new OrganisationGroupMembership(
        1,
        projectOperator.getOrganisationGroup(),
        List.of(
            UserTestingUtil.getPerson(1000, "forename-1000", "surname", "person1@org.co.uk", "123456"),
            UserTestingUtil.getPerson(2000, "forename-2000", "surname", "person2@org.co.uk", "123456")
        )
    );

    when(portalOrganisationGroupPersonMembershipService.getOrganisationGroupMembershipForOrganisationGroupIn(any()))
        .thenReturn(List.of(organisationMembership));

    regulatorUpdateReminderService.processDueReminders();

    organisationMembership.getTeamMembers().forEach(teamMember ->
        verify(emailService, times(1)).sendEmail(
            any(),
            eq(teamMember.getEmailAddress()),
            eq(teamMember.getForename())
        )
    );
  }
}