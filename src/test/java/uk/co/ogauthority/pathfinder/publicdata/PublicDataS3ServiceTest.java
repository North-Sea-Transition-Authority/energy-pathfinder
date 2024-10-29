package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static uk.co.ogauthority.pathfinder.publicdata.PublicDataS3Service.PUBLIC_DATA_JSON_FILE_KEY;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class PublicDataS3ServiceTest {

  @Mock
  private S3Client s3Client;

  private final String s3Bucket = "test-s3-bucket";

  private PublicDataS3Service publicDataS3Service;

  @BeforeEach
  void beforeEach() {
    publicDataS3Service = new PublicDataS3Service(
        new PublicDataConfigurationProperties(
            new PublicDataConfigurationProperties.S3(null, null, null, null, s3Bucket),
            null
        ),
        s3Client
    );
  }

  @Test
  void uploadPublicDataJsonFile() throws IOException {
    var publicDataJson = PublicDataJsonTestUtil.newBuilder().build();

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

    var requestBody = requestBodyCaptor.getValue();
    assertThat(requestBody).isNotNull();

    var requestBodyJson = CharStreams.toString(new InputStreamReader(requestBody.contentStreamProvider().newStream(), Charsets.UTF_8));
    var expectedJson = Resources.toString(Resources.getResource("test-public-data.json"), Charsets.UTF_8);

    JSONAssert.assertEquals(requestBodyJson, expectedJson, JSONCompareMode.STRICT);
  }
}
