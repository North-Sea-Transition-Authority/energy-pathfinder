package uk.co.ogauthority.pathfinder.publicdata;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "pathfinder.public-data")
@Validated
record PublicDataConfigurationProperties(
    S3 s3,
    @NotNull Integer uploadIntervalSeconds
) {

  record S3(
      @NotNull String accessKeyId,
      @NotNull String secretAccessKey,
      @NotNull String region,
      String endpointOverride,
      @NotNull String bucket
  ) {
  }
}
