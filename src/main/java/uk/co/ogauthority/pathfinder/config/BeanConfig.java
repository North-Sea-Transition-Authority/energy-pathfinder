package uk.co.ogauthority.pathfinder.config;

import io.micrometer.core.instrument.MeterRegistry;
import java.time.Clock;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import uk.co.ogauthority.pathfinder.auth.FoxLoginCallbackFilter;
import uk.gov.service.notify.NotificationClient;

@Configuration
public class BeanConfig {

  @Bean
  public NotificationClient notificationClient(@Value("${notify.apiKey}") String apiKey) {
    return new NotificationClient(apiKey);
  }

  @Bean
  public EmailValidator emailValidator() {
    return EmailValidator.getInstance();
  }

  @Bean
  public Clock utcClock() {
    return Clock.systemUTC();
  }

  @Bean
  public MetricsProvider metricsProvider(MeterRegistry meterRegistry) {
    return new MetricsProvider(meterRegistry);
  }

  @Bean
  public FilterRegistrationBean<FoxLoginCallbackFilter> foxLoginCallbackFilterRegistration(FoxLoginCallbackFilter foxLoginCallbackFilter) {
    //Disable automatic registration of the security filter - this will be manually registered in security config
    FilterRegistrationBean<FoxLoginCallbackFilter> registration = new FilterRegistrationBean<>(foxLoginCallbackFilter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean("messageSource")
  public MessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
}
