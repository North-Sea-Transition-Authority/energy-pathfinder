package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore.WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateRequestProjectDto;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class WeekBeforeRegulatorDeadlineUpdateReminderServiceTest {

  @Mock
  private RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Mock
  private TestWeekBeforeRegulatorDeadlineUpdateReminderService testWeekBeforeRegulatorDeadlineUpdateReminderService;

  private WeekBeforeRegulatorDeadlineUpdateReminderService weekBeforeRegulatorDeadlineUpdateReminderService;

  @Before
  public void setup() {
    weekBeforeRegulatorDeadlineUpdateReminderService = new WeekBeforeRegulatorDeadlineUpdateReminderService(
        List.of(testWeekBeforeRegulatorDeadlineUpdateReminderService),
        regulatorUpdateReminderEmailPropertiesService
    );
  }

  @Test
  public void isReminderDue_verifySevenDaysBeforeDeadline() {

    var deadlineDateOneWeekInFuture = LocalDate.now().plus(7, ChronoUnit.DAYS);

    var isReminderDue = weekBeforeRegulatorDeadlineUpdateReminderService.isReminderDue(deadlineDateOneWeekInFuture);

    assertThat(isReminderDue).isTrue();
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

    when(testWeekBeforeRegulatorDeadlineUpdateReminderService.getSupportedProjectType()).thenReturn(supportedProjectType);

    weekBeforeRegulatorDeadlineUpdateReminderService.getEmailReminderProperties(regulatorUpdateRequestProjectDto);

    verify(testWeekBeforeRegulatorDeadlineUpdateReminderService, times(1)).getEmailProperties(
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

    when(testWeekBeforeRegulatorDeadlineUpdateReminderService.getSupportedProjectType()).thenReturn(supportedProjectType);

    var expectedFormattedDeadlineDate = "deadline-date";
    when(regulatorUpdateReminderEmailPropertiesService.getFormattedDeadlineDate(any())).thenReturn(expectedFormattedDeadlineDate);

    var projectUrl = "project-url";
    when(regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest)).thenReturn(projectUrl);

    var resultingEmailProperties = weekBeforeRegulatorDeadlineUpdateReminderService.getEmailReminderProperties(regulatorUpdateRequestProjectDto);

    verify(testWeekBeforeRegulatorDeadlineUpdateReminderService, never()).getEmailProperties(
        regulatorUpdateRequestProjectDto.getRegulatorUpdateRequest()
    );

    assertThat(resultingEmailProperties).isEqualTo(
        new WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties(
            expectedFormattedDeadlineDate,
            updateReason,
            projectUrl
        )
    );
  }

}