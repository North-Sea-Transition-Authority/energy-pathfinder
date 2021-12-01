package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.EmailAddress;
import uk.co.ogauthority.pathfinder.model.email.EmailRecipient;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class DayAfterRegulatorDeadlineUpdateReminderServiceTest {

  @Mock
  private RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Mock
  private TestDayAfterRegulatorDeadlineUpdateEmailPropertyProvider testDayAfterRegulatorDeadlineUpdateEmailPropertyProvider;

  private static final String REGULATOR_SHARED_EMAIL_ADDRESS = "email@regualtor.co.uk";

  private DayAfterRegulatorDeadlineUpdateReminderService dayAfterRegulatorDeadlineUpdateReminderService;

  @Before
  public void setup() {
    dayAfterRegulatorDeadlineUpdateReminderService = new DayAfterRegulatorDeadlineUpdateReminderService(
        List.of(testDayAfterRegulatorDeadlineUpdateEmailPropertyProvider),
        regulatorUpdateReminderEmailPropertiesService,
        REGULATOR_SHARED_EMAIL_ADDRESS
    );
  }

  @Test
  public void isReminderDue_whenDayAfterDeadline_thenTrue() {

    var deadlineDateOneDayInPast = LocalDate.now().minusDays(1);

    var isReminderDue = dayAfterRegulatorDeadlineUpdateReminderService.isReminderDue(deadlineDateOneDayInPast);

    assertThat(isReminderDue).isTrue();
  }

  @Test
  public void isReminderDue_whenNotDayAfterDeadline_thenFalse() {

    var deadlineDateOneDayInFuture = LocalDate.now().plusDays(1);

    var isReminderDue = dayAfterRegulatorDeadlineUpdateReminderService.isReminderDue(deadlineDateOneDayInFuture);

    assertThat(isReminderDue).isFalse();
  }

  @Test
  public void getEmailReminderProperties_whenSupportedProjectType_verifyCallToSupportedService() {

    var supportedProjectType = ProjectType.INFRASTRUCTURE;

    var projectDetail = ProjectUtil.getProjectDetails(supportedProjectType);

    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(projectDetail);

    var regulatorUpdateRequestProjectDto = new RegulatorUpdateRequestProjectDto(
        regulatorUpdateRequest,
        new ProjectOperator()
    );

    when(testDayAfterRegulatorDeadlineUpdateEmailPropertyProvider.getSupportedProjectType()).thenReturn(supportedProjectType);

    dayAfterRegulatorDeadlineUpdateReminderService.getEmailReminderProperties(regulatorUpdateRequestProjectDto);

    verify(testDayAfterRegulatorDeadlineUpdateEmailPropertyProvider, times(1)).getEmailProperties(
        regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest()
    );
  }

  @Test
  public void getEmailReminderProperties_whenNoSupportedProjectType_verifyDefaultEmailProperties() {

    var supportedProjectType = ProjectType.INFRASTRUCTURE;
    var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    var projectDetail = ProjectUtil.getProjectDetails(unsupportedProjectType);

    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(projectDetail);

    var updateReason = "update reason";
    regulatorUpdateRequest.setUpdateReason(updateReason);

    var regulatorUpdateRequestProjectDto = new RegulatorUpdateRequestProjectDto(
        regulatorUpdateRequest,
        new ProjectOperator()
    );

    when(testDayAfterRegulatorDeadlineUpdateEmailPropertyProvider.getSupportedProjectType()).thenReturn(supportedProjectType);

    var projectUrl = "project-url";
    when(regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest)).thenReturn(projectUrl);

    var resultingEmailProperties = dayAfterRegulatorDeadlineUpdateReminderService.getEmailReminderProperties(regulatorUpdateRequestProjectDto);

    verify(testDayAfterRegulatorDeadlineUpdateEmailPropertyProvider, never()).getEmailProperties(
        regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest()
    );

    assertThat(resultingEmailProperties).isEqualTo(
        new DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties(
            updateReason,
            projectUrl
        )
    );
  }

  @Test
  public void getAdditionalReminderRecipients_verifyRegulatorEmailAddressAndNoRecipientIdentifier() {

    var regulatorRecipient = new EmailRecipient(new EmailAddress(REGULATOR_SHARED_EMAIL_ADDRESS));

    var resultingAdditionalRecipients = dayAfterRegulatorDeadlineUpdateReminderService.getAdditionalReminderRecipients();

    assertThat(resultingAdditionalRecipients).containsExactly(regulatorRecipient);
  }

}