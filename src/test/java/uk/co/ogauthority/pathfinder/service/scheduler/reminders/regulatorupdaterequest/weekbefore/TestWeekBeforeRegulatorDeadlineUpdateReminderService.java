package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore.WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
class TestWeekBeforeRegulatorDeadlineUpdateReminderService implements WeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties getEmailProperties(
      RegulatorUpdateRequest regulatorUpdateRequest) {
    return null;
  }
}
