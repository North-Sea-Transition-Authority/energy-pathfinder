package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
class TestDayAfterRegulatorDeadlineUpdateEmailPropertyProvider implements DayAfterRegulatorDeadlineUpdateEmailPropertyProvider {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties getEmailProperties(
      RegulatorUpdateRequest regulatorUpdateRequest) {
    return null;
  }
}
