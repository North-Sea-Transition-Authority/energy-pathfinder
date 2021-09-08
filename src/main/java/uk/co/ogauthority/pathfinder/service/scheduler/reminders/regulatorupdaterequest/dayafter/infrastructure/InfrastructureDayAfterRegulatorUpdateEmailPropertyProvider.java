package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.DayAfterRegulatorDeadlineUpdateEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.common.DayAfterRegulatorUpdateCommonEmailPropertyProvider;

@Service
class InfrastructureDayAfterRegulatorUpdateEmailPropertyProvider implements DayAfterRegulatorDeadlineUpdateEmailPropertyProvider {

  private static final ProjectType SUPPORTED_PROJECT_TYPE = ProjectType.INFRASTRUCTURE;

  private final RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  private final ProjectInformationService projectInformationService;

  private final DayAfterRegulatorUpdateCommonEmailPropertyProvider dayAfterRegulatorUpdateCommonEmailPropertyProvider;

  @Autowired
  InfrastructureDayAfterRegulatorUpdateEmailPropertyProvider(
      RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService,
      ProjectInformationService projectInformationService,
      DayAfterRegulatorUpdateCommonEmailPropertyProvider dayAfterRegulatorUpdateCommonEmailPropertyProvider
  ) {
    this.regulatorUpdateReminderEmailPropertiesService = regulatorUpdateReminderEmailPropertiesService;
    this.projectInformationService = projectInformationService;
    this.dayAfterRegulatorUpdateCommonEmailPropertyProvider = dayAfterRegulatorUpdateCommonEmailPropertyProvider;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return SUPPORTED_PROJECT_TYPE;
  }

  @Override
  public DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties getEmailProperties(RegulatorUpdateRequest regulatorUpdateRequest) {

    var projectTitle = projectInformationService.getProjectTitle(regulatorUpdateRequest.getProjectDetail());

    var projectTypeLowercaseDisplayName = getSupportedProjectType().getLowercaseDisplayName();

    var subjectText = String.format(
        "%s for %s: %s",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        projectTypeLowercaseDisplayName,
        projectTitle
    );

    var introductionText = String.format(
        "%s to your %s, %s, was due by %s.",
        dayAfterRegulatorUpdateCommonEmailPropertyProvider.getDefaultIntroductionTextPrefix(),
        projectTypeLowercaseDisplayName,
        projectTitle,
        regulatorUpdateReminderEmailPropertiesService.getFormattedDeadlineDate(regulatorUpdateRequest.getDeadlineDate())
    );

    return new DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties(
        regulatorUpdateRequest.getUpdateReason(),
        regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest),
        subjectText,
        introductionText
    );
  }
}
