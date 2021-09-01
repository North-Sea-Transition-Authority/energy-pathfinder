package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest.weekbefore;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.regualtorupdaterequest.weekbefore.WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;


/**
 * Interface to provide customise the behaviour of the WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties
 * class for a specific project type.
 */
public interface WeekBeforeRegulatorDeadlineUpdatedEmailPropertyProvider {

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
  WeekBeforeDeadlineRegulatorUpdateReminderEmailProperties getEmailProperties(
      RegulatorUpdateRequest regulatorUpdateRequest
  );
}
