package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore.WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore.WeekBeforeRegulatorDeadlineUpdateEmailPropertyProvider;

@Service
class InfrastructureWeekBeforeRegulatorDeadlineUpdateEmailPropertyProvider
    implements WeekBeforeRegulatorDeadlineUpdateEmailPropertyProvider {

  private static final ProjectType SUPPORTED_PROJECT_TYPE = ProjectType.INFRASTRUCTURE;

  private final RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  private final ProjectInformationService projectInformationService;

  @Autowired
  InfrastructureWeekBeforeRegulatorDeadlineUpdateEmailPropertyProvider(
      RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService,
      ProjectInformationService projectInformationService
  ) {
    this.regulatorUpdateReminderEmailPropertiesService = regulatorUpdateReminderEmailPropertiesService;
    this.projectInformationService = projectInformationService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return SUPPORTED_PROJECT_TYPE;
  }

  @Override
  public WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties getEmailProperties(RegulatorUpdateRequest regulatorUpdateRequest) {

    var projectTitle = projectInformationService.getProjectTitle(regulatorUpdateRequest.getProjectDetail());

    var projectTypeLowercaseDisplayName = getSupportedProjectType().getLowercaseDisplayName();

    var subjectText = String.format(
        "%s for %s: %s",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        projectTypeLowercaseDisplayName,
        projectTitle
    );

    var introductionText = String.format(
        "%s to your %s, %s.",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_INTRO_TEXT,
        projectTypeLowercaseDisplayName,
        projectTitle
    );

    return new WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties(
        regulatorUpdateReminderEmailPropertiesService.getFormattedDeadlineDate(regulatorUpdateRequest.getDeadlineDate()),
        regulatorUpdateRequest.getUpdateReason(),
        regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest),
        subjectText,
        introductionText
    );
  }
}
