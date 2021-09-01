package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.finalreminder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.finalreminder.FinalQuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminderService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyUpdateFinalReminderServiceTest {

  @Mock
  private QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  @Mock
  private EmailLinkService emailLinkService;

  private QuarterlyUpdateFinalReminderService quarterlyUpdateFinalReminderService;

  @Before
  public void setup() {
    quarterlyUpdateFinalReminderService = new QuarterlyUpdateFinalReminderService(
        quarterlyUpdateReminderService,
        emailLinkService
    );
  }

  @Test
  public void sendFinalQuarterlyUpdateReminder_verifyInteraction() {

    quarterlyUpdateFinalReminderService.sendFinalQuarterlyUpdateReminder();

    verify(quarterlyUpdateReminderService, times(1)).sendQuarterlyProjectUpdateReminderToOperators(
        quarterlyUpdateFinalReminderService
    );
  }

  @Test
  public void sendFinalQuarterlyUpdateReminder_whenDependentServiceThrowsException_verifyConsumerHandles() {

    doThrow(new RuntimeException()).when(quarterlyUpdateReminderService).sendQuarterlyProjectUpdateReminderToOperators(
        quarterlyUpdateFinalReminderService
    );

    Assertions.assertDoesNotThrow(() -> quarterlyUpdateFinalReminderService.sendFinalQuarterlyUpdateReminder());
  }

  @Test
  public void getRemindableProjects_whenNoRemindableProjectsFound_thenReturnEmptyList() {

    when(quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter()).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateFinalReminderService.getRemindableProjects();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  public void getRemindableProjects_whenRemindableProjectsFound_thenReturnPopulatedList() {

    var expectedRemindableProjects = List.of(
        new RemindableProject(1, 2, "project name")
    );

    when(quarterlyUpdateReminderService.getRemindableProjectsNotUpdatedInCurrentQuarter()).thenReturn(expectedRemindableProjects);

    var resultingRemindableProjects = quarterlyUpdateFinalReminderService.getRemindableProjects();

    assertThat(resultingRemindableProjects).isEqualTo(expectedRemindableProjects);
  }

  @Test
  public void getReminderEmailProperties_verifyExpectedEmailProperties() {

    var recipientIdentifier = "recipient identifier";
    var operatorName = "operator name";
    var serviceUrl = "/service-url";
    var projectNameList = List.of("project A", "project B");

    when(emailLinkService.getWorkAreaUrl()).thenReturn(serviceUrl);

    var expectedEmailProperties = new FinalQuarterlyUpdateReminderEmailProperties(
        recipientIdentifier,
        operatorName,
        projectNameList,
        serviceUrl
    );

    var resultingEmailProperties = quarterlyUpdateFinalReminderService.getReminderEmailProperties(
        recipientIdentifier,
        operatorName,
        projectNameList
    );

    assertThat(resultingEmailProperties).isEqualTo(expectedEmailProperties);

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(Map.of(
        "OPERATOR_NAME", operatorName,
        "OPERATOR_PROJECTS", projectNameList,
        "SERVICE_LOGIN_URL", serviceUrl,
        CommonEmailMergeField.RECIPIENT_IDENTIFIER, recipientIdentifier
    ));
  }

}