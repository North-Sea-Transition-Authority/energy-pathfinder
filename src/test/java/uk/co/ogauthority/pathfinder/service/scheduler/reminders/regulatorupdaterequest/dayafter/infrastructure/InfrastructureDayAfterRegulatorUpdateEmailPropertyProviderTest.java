package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.infrastructure;

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
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureDayAfterRegulatorUpdateEmailPropertyProviderTest {

  private static final ProjectType SUPPORTED_PROJECT_TYPE = ProjectType.INFRASTRUCTURE;

  @Mock
  private RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Mock
  private ProjectInformationService projectInformationService;

  private InfrastructureDayAfterRegulatorUpdateEmailPropertyProvider infrastructureDayAfterRegulatorUpdateEmailPropertyProvider;

  @Before
  public void setup() {
    infrastructureDayAfterRegulatorUpdateEmailPropertyProvider = new InfrastructureDayAfterRegulatorUpdateEmailPropertyProvider(
        regulatorUpdateReminderEmailPropertiesService,
        projectInformationService
    );
  }

  @Test
  public void getSupportedProjectType_verifyInfrastructure() {
    var resultingSupportedProjectType = infrastructureDayAfterRegulatorUpdateEmailPropertyProvider.getSupportedProjectType();
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

    var projectTitle = "project title";
    when(projectInformationService.getProjectTitle(any())).thenReturn(projectTitle);

    var resultingEmailProperties = infrastructureDayAfterRegulatorUpdateEmailPropertyProvider.getEmailProperties(
        regulatorUpdateRequest
    );

    var expectedSubjectText = String.format(
        "%s for %s: %s",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        SUPPORTED_PROJECT_TYPE.getLowercaseDisplayName(),
        projectTitle
    );

    var expectedIntroductionText = String.format(
        "%s to your %s, %s, was due by %s.",
        DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties.DEFAULT_INTRODUCTION_TEXT_PREFIX,
        SUPPORTED_PROJECT_TYPE.getLowercaseDisplayName(),
        projectTitle,
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