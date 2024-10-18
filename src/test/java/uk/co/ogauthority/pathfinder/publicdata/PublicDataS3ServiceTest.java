package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static uk.co.ogauthority.pathfinder.publicdata.PublicDataS3Service.PUBLIC_DATA_JSON_FILE_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class PublicDataS3ServiceTest {

  @Mock
  private S3Client s3Client;

  private final String s3Bucket = "test-s3-bucket";

  private final ObjectMapper objectMapper = new ObjectMapper();

  private PublicDataS3Service publicDataS3Service;

  @BeforeEach
  void beforeEach() {
    publicDataS3Service = new PublicDataS3Service(
        new PublicDataConfigurationProperties(
            new PublicDataConfigurationProperties.S3(null, null, null, null, s3Bucket),
            null
        ),
        s3Client,
        objectMapper
    );
  }

  @Test
  void uploadPublicDataJsonFile() throws JsonProcessingException {
    var publicDataJson = new PublicDataJson(List.of());

    publicDataS3Service.uploadPublicDataJsonFile(publicDataJson);

    var requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

    verify(s3Client).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(PUBLIC_DATA_JSON_FILE_KEY)
                .contentType(ContentType.APPLICATION_JSON.getMimeType())
                .build()
        ),
        requestBodyCaptor.capture()
    );

    assertThat(requestBodyCaptor.getValue())
        .isNotNull()
        .extracting(requestBody -> IOUtils.toString(requestBody.contentStreamProvider().newStream(), StandardCharsets.UTF_8))
        .isEqualTo(objectMapper.writeValueAsString(publicDataJson));
  }
}
