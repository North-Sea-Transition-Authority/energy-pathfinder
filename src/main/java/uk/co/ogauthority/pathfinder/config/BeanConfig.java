package uk.co.ogauthority.pathfinder.config;

import io.micrometer.core.instrument.MeterRegistry;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Clock;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import uk.co.ogauthority.pathfinder.auth.FoxLoginCallbackFilter;
import uk.gov.service.notify.NotificationClient;

@Configuration
public class BeanConfig {

  @Bean
  public NotificationClient notificationClient(@Value("${notify.apiKey}") String apiKey,
                                               @Value("${pathfinder.proxy.host:#{null}}") String proxyHost,
                                               @Value("${pathfinder.proxy.port:#{null}}") String proxyPort) {
    var proxy = createProxy(proxyHost, proxyPort);
    return new NotificationClient(apiKey, proxy);
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
  public Clock tzClock() {
    return Clock.systemDefaultZone();
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

  private Proxy createProxy(String proxyHost, String proxyPort) {
    Proxy proxy;
    if (proxyHost == null) {
      proxy = Proxy.NO_PROXY;
    } else {
      proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
    }

    return proxy;
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public ClientHttpRequestFactory requestFactory(@Value("${pathfinder.proxy.host:#{null}}") String proxyHost,
                                                 @Value("${pathfinder.proxy.port:#{null}}") String proxyPort) {
    var httpRequestFactory = new SimpleClientHttpRequestFactory();
    var proxy = createProxy(proxyHost, proxyPort);
    httpRequestFactory.setProxy(proxy);
    return httpRequestFactory;
  }

}
