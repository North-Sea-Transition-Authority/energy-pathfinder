package uk.co.ogauthority.pathfinder.service.email.notify;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.service.email.EmailService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

/**
 * Implementation for the Test GOV.UK Notify service
 * The test implementation will not send to the actual recipient and instead send to ${email.testRecipientList}
 * if set. All emails will include "TEST EMAIL" as the first part of the subject.
 */
public class TestEmailServiceImpl implements EmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestEmailServiceImpl.class);

  private final NotificationClient notificationClient;
  private final NotifyTemplateService notifyTemplateService;
  private final List<String> testRecipientList;
  private final EmailValidator emailValidator;
  private final DefaultEmailPersonalisationService defaultEmailPersonalisationService;

  public TestEmailServiceImpl(NotifyTemplateService notifyTemplateService,
                              NotificationClient notificationClient,
                              EmailValidator emailValidator,
                              List<String> testRecipientList,
                              DefaultEmailPersonalisationService defaultEmailPersonalisationService) {
    this.notificationClient = notificationClient;
    this.notifyTemplateService = notifyTemplateService;
    this.emailValidator = emailValidator;
    this.testRecipientList = testRecipientList;
    this.defaultEmailPersonalisationService = defaultEmailPersonalisationService;
  }

  @Override
  public void sendEmail(EmailProperties emailProperties, String toEmailAddress) {
    sendEmail(emailProperties, toEmailAddress, null, null);
  }

  @Override
  public void sendEmail(EmailProperties emailProperties,
                        String toEmailAddress,
                        String reference,
                        String emailReplyToId) {

    Optional<String> templateId = notifyTemplateService.getTemplateIdFromName(emailProperties.getTemplateName());

    if (templateId.isPresent()) {

      // Set the TEST_EMAIL personalisation when in the development service
      var personalisation = defaultEmailPersonalisationService.getDefaultEmailPersonalisation();
      personalisation.putAll(emailProperties.getEmailPersonalisation());
      personalisation.put(CommonEmailMergeField.TEST_EMAIL, "yes");
      personalisation.put(CommonEmailMergeField.SUBJECT_PREFIX, "**TEST EMAIL**");

      // If we have test recipients send the email to each
      testRecipientList.stream()
          .filter(testRecipient -> testRecipient.length() > 0)
          .forEach(testRecipient -> {
            if (emailValidator.isValid(testRecipient)) {
              try {
                notificationClient.sendEmail(templateId.get(), testRecipient, personalisation, reference, emailReplyToId);
              } catch (NotificationClientException e) {
                LOGGER.error("Error occurred in NotificationClient: {}", ExceptionUtils.getStackTrace(e));
              }
            } else {
              // TODO PAT-28 metric logging for email failures
              LOGGER.error("Email validation prevented email being sent to: {}", testRecipient);
            }
          });

    } else {
      LOGGER.error("Could not find template ID for template with name {}", emailProperties.getTemplateName());
    }

  }
}