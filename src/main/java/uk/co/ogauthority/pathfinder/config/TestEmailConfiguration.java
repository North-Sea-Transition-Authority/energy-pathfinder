package uk.co.ogauthority.pathfinder.config;

import java.util.List;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.co.ogauthority.pathfinder.service.email.notify.DefaultEmailPersonalisationService;
import uk.co.ogauthority.pathfinder.service.email.notify.NotifyTemplateService;
import uk.co.ogauthority.pathfinder.service.email.notify.TestEmailServiceImpl;
import uk.gov.service.notify.NotificationClient;

/**
 * Configuration class for non-production profiles that should be using test email functionality.
 */
@Configuration
@Profile({"test-email", "development", "integration-test"})
public class TestEmailConfiguration {

  /**
   * Bean to return the test GOV.UK notify implementation which will send
   * emails to the test recipient instead of the actual recipient
   * @param notifyTemplateService An instance of the template service
   * @param notificationClient A GOV.UK notification client
   * @param emailValidator Email Validator
   * @param testRecipientList List of test recipients to send the emails to
   * @param defaultEmailPersonalisationService An instance of DefaultEmailPersonalisationService
   * @return an instantiated TestNotifyServiceImpl
   */
  @Bean
  @ConditionalOnProperty(name = "email.mode", havingValue = "test")
  public TestEmailServiceImpl testNotifyService(
      NotifyTemplateService notifyTemplateService,
      NotificationClient notificationClient,
      EmailValidator emailValidator,
      // NB: the ":" means the default value will be an empty string when not specified
      @Value("#{'${email.testRecipientList:}'.split(';')}") List<String> testRecipientList,
      DefaultEmailPersonalisationService defaultEmailPersonalisationService
  ) {
    return new TestEmailServiceImpl(
        notifyTemplateService,
        notificationClient,
        emailValidator,
        testRecipientList,
        defaultEmailPersonalisationService
    );
  }
}