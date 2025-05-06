package uk.co.ogauthority.pathfinder.publicdata;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
class PublicDataConfiguration {

  @Bean
  S3Client s3Client(PublicDataConfigurationProperties configurationProperties) {
    var s3ClientBuilder = S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(configurationProperties.s3().accessKeyId(), configurationProperties.s3().secretAccessKey())))
        .region(Region.of(configurationProperties.s3().region()));

    if (configurationProperties.s3().endpointOverride() != null) {
      s3ClientBuilder.endpointOverride(URI.create(configurationProperties.s3().endpointOverride()));
    }

    return s3ClientBuilder.build();
  }
}
