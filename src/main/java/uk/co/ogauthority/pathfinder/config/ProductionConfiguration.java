package uk.co.ogauthority.pathfinder.config;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.co.ogauthority.pathfinder.service.email.notify.DefaultEmailPersonalisationService;
import uk.co.ogauthority.pathfinder.service.email.notify.NotifyTemplateService;
import uk.co.ogauthority.pathfinder.service.email.notify.ProductionEmailServiceImpl;
import uk.gov.service.notify.NotificationClient;

/**
 * Configuration class for the "production" profile.
 */
@Configuration
@Profile("production")
public class ProductionConfiguration {

  /**
   * Bean to return the production GOV.UK notify implementation if the matches condition
   * inside ProdNotifyCondition evaluates to true
   * @param notifyTemplateService An instance of the template service
   * @param notificationClient A GOV.UK notification client
   * @param emailValidator Email Validator
   * @param defaultEmailPersonalisationService An instance of DefaultEmailPersonalisationService
   * @return an instantiated ProductionNotifyServiceImpl
   */
  @Bean
  @ConditionalOnProperty(name = "email.mode", havingValue = "production")
  public ProductionEmailServiceImpl productionNotifyService(
      NotifyTemplateService notifyTemplateService,
      NotificationClient notificationClient,
      EmailValidator emailValidator,
      DefaultEmailPersonalisationService defaultEmailPersonalisationService
  ) {
    return new ProductionEmailServiceImpl(
        notifyTemplateService,
        notificationClient,
        emailValidator,
        defaultEmailPersonalisationService
    );
  }
}