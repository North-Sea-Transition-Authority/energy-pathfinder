package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.initialreminder;

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
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.initialreminder.InitialQuarterlyUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.QuarterlyUpdateReminderService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;

@RunWith(MockitoJUnitRunner.class)
public class QuarterlyUpdateInitialReminderServiceTest {

  @Mock
  private QuarterlyUpdateReminderService quarterlyUpdateReminderService;

  @Mock
  private LinkService linkService;

  private QuarterlyUpdateInitialReminderService quarterlyUpdateInitialReminderService;

  @Before
  public void setup() {
    quarterlyUpdateInitialReminderService = new QuarterlyUpdateInitialReminderService(
        quarterlyUpdateReminderService,
        linkService
    );
  }

  @Test
  public void sendInitialQuarterlyReminder_verifyInteraction() {

    quarterlyUpdateInitialReminderService.sendInitialQuarterlyReminder();

    verify(quarterlyUpdateReminderService, times(1)).sendQuarterlyProjectUpdateReminderToOperators(
        quarterlyUpdateInitialReminderService
    );
  }

  @Test
  public void sendInitialQuarterlyReminder_whenDependentServiceThrowsException_verifyConsumerHandles() {

    doThrow(new RuntimeException()).when(quarterlyUpdateReminderService).sendQuarterlyProjectUpdateReminderToOperators(
        quarterlyUpdateInitialReminderService
    );

    Assertions.assertDoesNotThrow(() -> quarterlyUpdateInitialReminderService.sendInitialQuarterlyReminder());
  }

  @Test
  public void getRemindableProjects_whenNoRemindableProjectsFound_thenReturnEmptyList() {

    when(quarterlyUpdateReminderService.getAllRemindableProjects()).thenReturn(Collections.emptyList());

    var resultingRemindableProjects = quarterlyUpdateInitialReminderService.getRemindableProjects();

    assertThat(resultingRemindableProjects).isEmpty();
  }

  @Test
  public void getRemindableProjects_whenRemindableProjectsFound_thenReturnPopulatedList() {

    var expectedRemindableProjects = List.of(
        new RemindableProject(1, 2, "project name", true)
    );

    when(quarterlyUpdateReminderService.getAllRemindableProjects()).thenReturn(expectedRemindableProjects);

    var resultingRemindableProjects = quarterlyUpdateInitialReminderService.getRemindableProjects();

    assertThat(resultingRemindableProjects).isEqualTo(expectedRemindableProjects);
  }

  @Test
  public void getReminderEmailProperties_verifyExpectedEmailProperties() {

    var recipientIdentifier = "recipient identifier";
    var operatorName = "operator name";
    var serviceUrl = "/service-url";
    var projectNameList = List.of("project A", "project B");
    var projectsWithPastUpcomingTendersList = List.of("project B");


    when(linkService.getWorkAreaUrl()).thenReturn(serviceUrl);

    var expectedEmailProperties = new InitialQuarterlyUpdateReminderEmailProperties(
        recipientIdentifier,
        operatorName,
        projectNameList,
        serviceUrl,
        projectsWithPastUpcomingTendersList
    );

    var resultingEmailProperties = quarterlyUpdateInitialReminderService.getReminderEmailProperties(
        recipientIdentifier,
        operatorName,
        projectNameList,
        projectsWithPastUpcomingTendersList
    );

    assertThat(resultingEmailProperties).isEqualTo(expectedEmailProperties);

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(Map.of(
        "OPERATOR_NAME", operatorName,
        "OPERATOR_PROJECTS", projectNameList,
        "SERVICE_LOGIN_URL", serviceUrl,
        CommonEmailMergeField.RECIPIENT_IDENTIFIER, recipientIdentifier,
        "PROJECTS_WITH_PAST_UPCOMING_TENDERS", projectsWithPastUpcomingTendersList,
        "SHOW_PROJECTS_WITH_PAST_UPCOMING_TENDERS", "yes"
    ));
  }
}
