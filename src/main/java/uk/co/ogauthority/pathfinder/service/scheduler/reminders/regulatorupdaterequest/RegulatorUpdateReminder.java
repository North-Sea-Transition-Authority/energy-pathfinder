package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import java.time.LocalDate;
import java.util.Set;
import uk.co.ogauthority.pathfinder.model.email.EmailRecipient;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;

/**
 * Interface to register a reminder for a regulator requested update that has not been submitted.
 */
public interface RegulatorUpdateReminder {

  /**
   * Determines if a reminder is due for the provided deadline date.
   * @param updateDeadlineDate The deadline date to check if the reminder is due.
   * @return true if the reminder is due, false otherwise
   */
  boolean isReminderDue(LocalDate updateDeadlineDate);

  /**
   * Get the reminder email properties specific to this implementation.
   * @param regulatorUpdateRequestProjectDto The regulator update request we are processing
   * @return The email properties required for sending the email specific to this implementation
   */
  EmailProperties getEmailReminderProperties(RegulatorUpdateRequestProjectDto regulatorUpdateRequestProjectDto);

  /**
   * Specify a group of additional email recipients that should receive this reminder.
   * @return a set of additional email recipients that should receive this reminder.
   */
  default Set<EmailRecipient> getAdditionalReminderRecipients() {
    return Set.of();
  }
}
