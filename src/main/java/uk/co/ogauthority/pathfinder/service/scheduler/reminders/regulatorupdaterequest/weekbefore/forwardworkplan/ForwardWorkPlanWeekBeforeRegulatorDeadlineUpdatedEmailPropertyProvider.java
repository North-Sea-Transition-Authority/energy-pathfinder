package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.RegulatorUpdateRequestReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore.WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.RegulatorUpdateReminderEmailPropertiesService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore.WeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider;

@Service
class ForwardWorkPlanWeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider
    implements WeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider {

  private static final ProjectType SUPPORTED_PROJECT_TYPE = ProjectType.FORWARD_WORK_PLAN;

  private final RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Autowired
  ForwardWorkPlanWeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider(
      RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService
  ) {
    this.regulatorUpdateReminderEmailPropertiesService = regulatorUpdateReminderEmailPropertiesService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return SUPPORTED_PROJECT_TYPE;
  }

  @Override
  public WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties getEmailProperties(RegulatorUpdateRequest regulatorUpdateRequest) {

    var projectTypeLowercaseDisplayName = getSupportedProjectType().getLowercaseDisplayName();

    var subjectText = String.format(
        "%s to your organisation's %s",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_SUBJECT_TEXT,
        projectTypeLowercaseDisplayName
    );

    var introductionText = String.format(
        "%s to your organisation's %s.",
        RegulatorUpdateRequestReminderEmailProperties.DEFAULT_UPDATE_REMINDER_INTRO_TEXT,
        projectTypeLowercaseDisplayName
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
