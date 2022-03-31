package uk.co.ogauthority.pathfinder.analytics;

import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "analytics.config")
@ConstructorBinding
@Validated
class AnalyticsConfig {

  @NotNull
  private final boolean enabled;

  private final String appTagApiSecret;

  private final String globalTagApiSecret;

  private final String endpointUrl;

  private final String userAgent;

  private final Integer connectionTimeoutSeconds;

  AnalyticsConfig(
      boolean enabled,
      String appTagApiSecret,
      String globalTagApiSecret,
      String endpointUrl,
      String userAgent,
      Integer connectionTimeoutSeconds) {
    this.enabled = enabled;
    this.appTagApiSecret = appTagApiSecret;
    this.globalTagApiSecret = globalTagApiSecret;
    this.endpointUrl = endpointUrl;
    this.userAgent = userAgent;
    this.connectionTimeoutSeconds = connectionTimeoutSeconds;
  }

  boolean isEnabled() {
    return enabled;
  }

  String getAppTagApiSecret() {
    return appTagApiSecret;
  }

  public String getGlobalTagApiSecret() {
    return globalTagApiSecret;
  }

  String getEndpointUrl() {
    return endpointUrl;
  }

  String getUserAgent() {
    return userAgent;
  }

  Integer getConnectionTimeoutSeconds() {
    return connectionTimeoutSeconds;
  }

}
