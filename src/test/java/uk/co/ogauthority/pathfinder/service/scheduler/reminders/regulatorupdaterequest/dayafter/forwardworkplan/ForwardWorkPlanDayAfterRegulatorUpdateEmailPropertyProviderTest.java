package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProviderTest {

  private static final ProjectType SUPPORTED_PROJECT_TYPE = ProjectType.FORWARD_WORK_PLAN;

  @Mock
  private RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  private ForwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider forwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider;

  @Before
  public void setup() {
    forwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider = new ForwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider(
        regulatorUpdateReminderEmailPropertiesService
    );
  }

  @Test
  public void getSupportedProjectType_verifyForwardWorkPlan() {
    var resultingSupportedProjectType = forwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider.getSupportedProjectType();
    assertThat(resultingSupportedProjectType).isEqualTo(SUPPORTED_PROJECT_TYPE);
  }

  @Test
  public void getEmailProperties_verifyExpectedProperties() {

    var regulatorUpdateRequest = new RegulatorUpdateRequest();

    var updateReason = "update reason";
    regulatorUpdateRequest.setUpdateReason(updateReason);

    var formattedDeadlineDate = "deadline-date";
    when(regulatorUpdateReminderEmailPropertiesService.getFormattedDeadlineDate(any())).thenReturn(formattedDeadlineDate);

    var projectUrl = "project-url";
    when(regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest)).thenReturn(projectUrl);

    var resultingEmailProperties = forwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider.getEmailProperties(
        regulatorUpdateRequest
    );

    var expectedSubjectText = String.format(
        "%s to your organisation's %s",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        SUPPORTED_PROJECT_TYPE.getLowercaseDisplayName()
    );

    var expectedIntroductionText = String.format(
        "%s to your organisation's %s was due by %s.",
        DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties.DEFAULT_INTRODUCTION_TEXT_PREFIX,
        SUPPORTED_PROJECT_TYPE.getLowercaseDisplayName(),
        formattedDeadlineDate
    );

    assertThat(resultingEmailProperties).isEqualTo(
        new DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties(
            updateReason,
            projectUrl,
            expectedSubjectText,
            expectedIntroductionText
        )
    );
  }

}