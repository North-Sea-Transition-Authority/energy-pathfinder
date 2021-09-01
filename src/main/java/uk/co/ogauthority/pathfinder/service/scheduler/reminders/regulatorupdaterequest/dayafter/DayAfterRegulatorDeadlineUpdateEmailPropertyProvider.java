package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.dayafter;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.dayafter.DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

/**
 * Interface to provide customise the behaviour of the DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties
 * class for a specific project type.
 */
public interface DayAfterRegulatorDeadlineUpdateEmailPropertyProvider {

  /**
   * The project type supported by this implementation.
   * @return The project type supported by this implementation
   */
  ProjectType getSupportedProjectType();


  /**
   * The email properties specific to this implementation.
   * @param regulatorUpdateRequest the regulator update request being processed
   * @return the email properties specific to this implementation
   */
  DayAfterDeadlineRegulatorUpdateEmailReminderEmailProperties getEmailProperties(
      RegulatorUpdateRequest regulatorUpdateRequest
  );
}
