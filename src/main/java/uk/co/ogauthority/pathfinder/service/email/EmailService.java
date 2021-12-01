package uk.co.ogauthority.pathfinder.service.email;

import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;


public interface EmailService {

  /**
   * Method to email a single recipient.
   * @param emailProperties The properties for the mail merge fields in the email template
   * @param toEmailAddress The email address to send the email to
   */
  void sendEmail(EmailProperties emailProperties, String toEmailAddress);

  /**
   * Method to email a single recipient.
   * @param emailProperties The properties for the mail merge fields in the email template
   * @param toEmailAddress The email address to send the email to
   * @param recipientName The name of the recipient. This should overwrite any recipient name
   *                      provided in the email properties if a non-null value is provided
   */
  void sendEmail(EmailProperties emailProperties, String toEmailAddress, String recipientName);

  /**
   * Method to email a single recipient.
   * @param emailProperties The properties for the mail merge fields in the email template
   * @param toEmailAddress The email address to send the email too
   * @param reference Identifies a single unique application or a batch of applications
   * @param emailReplyToId Specified email ID to receive replies from the users
   */
  void sendEmail(EmailProperties emailProperties,
                 String toEmailAddress,
                 String reference,
                 String emailReplyToId,
                 String recipientName);

}
