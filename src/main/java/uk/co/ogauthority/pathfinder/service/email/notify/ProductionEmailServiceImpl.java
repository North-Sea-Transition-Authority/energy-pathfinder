package uk.co.ogauthority.pathfinder.service.email.notify;

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
 * Implementation for the Production GOV.UK Notify service
 * The production implementation will send to the actual recipient
 */
public class ProductionEmailServiceImpl implements EmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductionEmailServiceImpl.class);

  private final NotificationClient notificationClient;
  private final NotifyTemplateService notifyTemplateService;
  private final EmailValidator emailValidator;
  private final DefaultEmailPersonalisationService defaultEmailPersonalisationService;

  public ProductionEmailServiceImpl(NotifyTemplateService notifyTemplateService,
                                    NotificationClient notificationClient,
                                    EmailValidator emailValidator,
                                    DefaultEmailPersonalisationService defaultEmailPersonalisationService) {
    this.notificationClient = notificationClient;
    this.notifyTemplateService = notifyTemplateService;
    this.emailValidator = emailValidator;
    this.defaultEmailPersonalisationService = defaultEmailPersonalisationService;
  }

  @Override
  public void sendEmail(EmailProperties emailProperties, String toEmailAddress) {
    sendEmail(emailProperties, toEmailAddress, null, null, null);
  }

  @Override
  public void sendEmail(EmailProperties emailProperties, String toEmailAddress, String recipientName) {
    sendEmail(emailProperties, toEmailAddress, null, null, recipientName);
  }

  @Override
  public void sendEmail(EmailProperties emailProperties,
                        String toEmailAddress,
                        String reference,
                        String emailReplyToId,
                        String recipientName) {

    try {

      Optional<String> templateId = notifyTemplateService.getTemplateIdFromName(emailProperties.getTemplateName());

      if (templateId.isPresent()) {

        var personalisation = defaultEmailPersonalisationService.getDefaultEmailPersonalisation();
        personalisation.putAll(emailProperties.getEmailPersonalisation());
        personalisation.put(CommonEmailMergeField.TEST_EMAIL, "no");
        personalisation.put(CommonEmailMergeField.SUBJECT_PREFIX, "");

        if (recipientName != null) {
          personalisation.put(CommonEmailMergeField.RECIPIENT_IDENTIFIER, recipientName);
        }

        if (emailValidator.isValid(toEmailAddress)) {
          notificationClient.sendEmail(templateId.get(), toEmailAddress, personalisation, reference, emailReplyToId);
        } else {
          // TODO PAT-28 metric logging for email failures
          LOGGER.error("Email validation prevented email being sent to: {}", toEmailAddress);
        }

      } else {
        LOGGER.error("Could not find template ID for template with name {}", emailProperties.getTemplateName());
      }
    } catch (NotificationClientException e) {
      LOGGER.error("Error occurred in NotificationClient: {}", ExceptionUtils.getStackTrace(e));
    }

  }
}
