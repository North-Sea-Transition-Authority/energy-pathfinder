package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.DayAfterRegulatorDeadlineUpdateEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter.common.DayAfterRegulatorUpdateCommonEmailPropertyProvider;

@Service
class ForwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider implements DayAfterRegulatorDeadlineUpdateEmailPropertyProvider {

  private static final ProjectType SUPPORTED_PROJECT_TYPE = ProjectType.FORWARD_WORK_PLAN;

  private final RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  private final DayAfterRegulatorUpdateCommonEmailPropertyProvider dayAfterRegulatorUpdateCommonEmailPropertyProvider;

  @Autowired
  ForwardWorkPlanDayAfterRegulatorUpdateEmailPropertyProvider(
      RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService,
      DayAfterRegulatorUpdateCommonEmailPropertyProvider dayAfterRegulatorUpdateCommonEmailPropertyProvider
  ) {
    this.regulatorUpdateReminderEmailPropertiesService = regulatorUpdateReminderEmailPropertiesService;
    this.dayAfterRegulatorUpdateCommonEmailPropertyProvider = dayAfterRegulatorUpdateCommonEmailPropertyProvider;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return SUPPORTED_PROJECT_TYPE;
  }

  @Override
  public DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties getEmailProperties(RegulatorUpdateRequest regulatorUpdateRequest) {

    var projectTypeLowercaseDisplayName = getSupportedProjectType().getLowercaseDisplayName();

    var subjectText = String.format(
        "%s to your organisation's %s",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        projectTypeLowercaseDisplayName
    );

    var introductionText = String.format(
        "%s to your organisation's %s was due by %s.",
        dayAfterRegulatorUpdateCommonEmailPropertyProvider.getDefaultIntroductionTextPrefix(),
        projectTypeLowercaseDisplayName,
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
