package uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate;

import java.util.List;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.reminder.project.quarterly.QuarterlyUpdateReminderEmailProperties;

/**
 * Interface for provide an implementation for sending quarterly project update reminder emails.
 */
public interface QuarterlyUpdateReminder {

  /**
   * Get all the remindable projects specific to this implementation.
   * @return a list of remindable projects specific to this implementation
   */
  List<RemindableProject> getRemindableProjects();

  /**
   * Get the email properties required for sending an email for this implementation.
   * @param recipientIdentifier the name of the person receiving the email
   * @param operatorName the name of the operator group the recipient is part of
   * @param remindableProjects a list of all the remindable project names to include in the email
   * @param pastUpcomingTenders a list of all the remindable projects with upcoming tenders in the past
   * @return a class containing the required email properties for the implementation
   */
  QuarterlyUpdateReminderEmailProperties getReminderEmailProperties(String recipientIdentifier,
                                                                    String operatorName,
                                                                    List<String> remindableProjects,
                                                                    List<String> pastUpcomingTenders);
}
