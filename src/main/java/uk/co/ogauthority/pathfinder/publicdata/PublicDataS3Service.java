package uk.co.ogauthority.pathfinder.publicdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
class PublicDataS3Service {

  static final String PUBLIC_DATA_JSON_FILE_KEY = "public-data.json";

  private static final Logger LOGGER = LoggerFactory.getLogger(PublicDataS3Service.class);

  private final PublicDataConfigurationProperties publicDataConfigurationProperties;
  private final S3Client s3Client;
  private final ObjectMapper objectMapper;

  PublicDataS3Service(
      PublicDataConfigurationProperties publicDataConfigurationProperties,
      S3Client s3Client,
      ObjectMapper objectMapper
  ) {
    this.publicDataConfigurationProperties = publicDataConfigurationProperties;
    this.s3Client = s3Client;
    this.objectMapper = objectMapper;
  }

  void uploadPublicDataJsonFile(PublicDataJson publicDataJson) {
    var s3Bucket = publicDataConfigurationProperties.s3().bucket();

    LOGGER.info("Uploading public data json file {} to S3 bucket {}", PUBLIC_DATA_JSON_FILE_KEY, s3Bucket);

    try {
      var jsonString = objectMapper.writeValueAsString(publicDataJson);

      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(s3Bucket)
              .key(PUBLIC_DATA_JSON_FILE_KEY)
              .contentType(ContentType.APPLICATION_JSON.getMimeType())
              .build(),
          RequestBody.fromString(jsonString)
      );
    } catch (Exception exception) {
      LOGGER.error("Failed to upload public data json file to S3", exception);
    }
  }
}
