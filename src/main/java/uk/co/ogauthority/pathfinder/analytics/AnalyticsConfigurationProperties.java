package uk.co.ogauthority.pathfinder.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyticsConfigurationProperties {

  private final AnalyticsProperties properties;

  private final AnalyticsConfig config;

  @Autowired
  public AnalyticsConfigurationProperties(AnalyticsProperties properties,
                                          AnalyticsConfig config) {
    this.properties = properties;
    this.config = config;
  }

  public AnalyticsProperties getProperties() {
    return properties;
  }

  AnalyticsConfig getConfig() {
    return config;
  }

}
