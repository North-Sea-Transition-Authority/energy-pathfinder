package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.ogauthority.pathfinder.publicdata.PublicDataS3Service.PUBLIC_DATA_JSON_FILE_KEY;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
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
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;

@ExtendWith(MockitoExtension.class)
class PublicDataS3ServiceTest {

  @Mock
  private S3Client s3Client;

  private final String s3Bucket = "test-s3-bucket";

  private PublicDataS3Service publicDataS3Service;

  @BeforeEach
  void beforeEach() {
    publicDataS3Service = spy(new PublicDataS3Service(
        new PublicDataConfigurationProperties(
            new PublicDataConfigurationProperties.S3(null, null, null, null, s3Bucket),
            null
        ),
        s3Client
    ));
  }

  @Test
  void uploadPublicData() {
    var publicDataJson = PublicDataJsonTestUtil.newBuilder().build();
    var uploadedFiles = List.of(UploadedFileUtil.createUploadedFile(), UploadedFileUtil.createUploadedFile());

    doNothing().when(publicDataS3Service).uploadPublicDataJsonFile(any(), any());
    doNothing().when(publicDataS3Service).uploadUploadedFiles(any(), any());

    publicDataS3Service.uploadPublicData(publicDataJson, uploadedFiles);

    verify(publicDataS3Service).uploadPublicDataJsonFile(s3Bucket, publicDataJson);
    verify(publicDataS3Service).uploadUploadedFiles(s3Bucket, uploadedFiles);
  }

  @Test
  void uploadPublicDataJsonFile() throws IOException {
    var publicDataJson = PublicDataJsonTestUtil.newBuilder().build();

    publicDataS3Service.uploadPublicDataJsonFile(s3Bucket, publicDataJson);

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

    JSONAssert.assertEquals(requestBodyJson, expectedJson, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  void uploadUploadedFiles_bothFilesDoNotExistInS3() throws Exception {
    var uploadedFile1 = UploadedFileUtil.createUploadedFile();
    var uploadedFile2 = UploadedFileUtil.createUploadedFile();

    uploadedFile1.setFileId("test-uploaded-file-id-1");
    uploadedFile1.setFileName("test-uploaded-file-name-1");
    uploadedFile1.setFileData(new SerialBlob("apple".getBytes(StandardCharsets.UTF_8)));

    uploadedFile2.setFileId("test-uploaded-file-id-2");
    uploadedFile2.setFileName("test-uploaded-file-name-2");
    uploadedFile2.setFileData(new SerialBlob("orange".getBytes(StandardCharsets.UTF_8)));

    var uploadedFile1Key = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile1);
    var uploadedFile2Key = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile2);

    when(publicDataS3Service.doesObjectExist(s3Bucket, uploadedFile1Key))
        .thenReturn(false);
    when(publicDataS3Service.doesObjectExist(s3Bucket, uploadedFile2Key))
        .thenReturn(false);

    publicDataS3Service.uploadUploadedFiles(s3Bucket, List.of(uploadedFile1, uploadedFile2));

    var requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

    verify(s3Client).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(uploadedFile1Key)
                .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile1.getFileName()))
                .contentLength(uploadedFile1.getFileSize())
                .contentType(uploadedFile1.getContentType())
                .build()
        ),
        requestBodyCaptor.capture()
    );
    verify(s3Client).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(uploadedFile2Key)
                .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile2.getFileName()))
                .contentLength(uploadedFile1.getFileSize())
                .contentType(uploadedFile1.getContentType())
                .build()
        ),
        requestBodyCaptor.capture()
    );

    assertThat(requestBodyCaptor.getAllValues())
        .extracting(body -> CharStreams.toString(new InputStreamReader(body.contentStreamProvider().newStream(), Charsets.UTF_8)))
        .containsExactly("apple", "orange");
  }

  @Test
  void uploadUploadedFiles_firstFileDoesNotExistInS3() throws Exception {
    var uploadedFile1 = UploadedFileUtil.createUploadedFile();
    var uploadedFile2 = UploadedFileUtil.createUploadedFile();

    uploadedFile1.setFileId("test-uploaded-file-id-1");
    uploadedFile1.setFileName("test-uploaded-file-name-1");
    uploadedFile1.setFileData(new SerialBlob("apple".getBytes(StandardCharsets.UTF_8)));

    uploadedFile2.setFileId("test-uploaded-file-id-2");
    uploadedFile2.setFileName("test-uploaded-file-name-2");
    uploadedFile2.setFileData(new SerialBlob("orange".getBytes(StandardCharsets.UTF_8)));

    var uploadedFile1Key = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile1);
    var uploadedFile2Key = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile2);

    when(publicDataS3Service.doesObjectExist(s3Bucket, uploadedFile1Key))
        .thenReturn(true);
    when(publicDataS3Service.doesObjectExist(s3Bucket, uploadedFile2Key))
        .thenReturn(false);

    publicDataS3Service.uploadUploadedFiles(s3Bucket, List.of(uploadedFile1, uploadedFile2));

    var requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

    verify(s3Client, never()).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(uploadedFile1Key)
                .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile1.getFileName()))
                .contentLength(uploadedFile1.getFileSize())
                .contentType(uploadedFile1.getContentType())
                .build()
        ),
        requestBodyCaptor.capture()
    );
    verify(s3Client).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(uploadedFile2Key)
                .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile2.getFileName()))
                .contentLength(uploadedFile1.getFileSize())
                .contentType(uploadedFile1.getContentType())
                .build()
        ),
        requestBodyCaptor.capture()
    );

    assertThat(requestBodyCaptor.getAllValues())
        .extracting(body -> CharStreams.toString(new InputStreamReader(body.contentStreamProvider().newStream(), Charsets.UTF_8)))
        .containsExactly("orange");
  }

  @Test
  void uploadUploadedFiles_secondFileUploadedWhenFirstUploadThrowsException() throws Exception {
    var uploadedFile1 = UploadedFileUtil.createUploadedFile();
    var uploadedFile2 = UploadedFileUtil.createUploadedFile();

    uploadedFile1.setFileId("test-uploaded-file-id-1");
    uploadedFile1.setFileName("test-uploaded-file-name-1");
    uploadedFile1.setFileData(new SerialBlob("apple".getBytes(StandardCharsets.UTF_8)));

    uploadedFile2.setFileId("test-uploaded-file-id-2");
    uploadedFile2.setFileName("test-uploaded-file-name-2");
    uploadedFile2.setFileData(new SerialBlob("orange".getBytes(StandardCharsets.UTF_8)));

    var uploadedFile1Key = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile1);
    var uploadedFile2Key = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile2);

    when(publicDataS3Service.doesObjectExist(s3Bucket, uploadedFile1Key))
        .thenReturn(false);
    when(publicDataS3Service.doesObjectExist(s3Bucket, uploadedFile2Key))
        .thenReturn(false);

    when(
        s3Client.putObject(
            eq(
                PutObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(uploadedFile1Key)
                    .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile1.getFileName()))
                    .contentLength(uploadedFile1.getFileSize())
                    .contentType(uploadedFile1.getContentType())
                    .build()
            ),
            any(RequestBody.class)
        )
    ).thenThrow(S3Exception.class);

    publicDataS3Service.uploadUploadedFiles(s3Bucket, List.of(uploadedFile1, uploadedFile2));

    var requestBodyCaptor = ArgumentCaptor.forClass(RequestBody.class);

    verify(s3Client).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(uploadedFile1Key)
                .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile1.getFileName()))
                .contentLength(uploadedFile1.getFileSize())
                .contentType(uploadedFile1.getContentType())
                .build()
        ),
        requestBodyCaptor.capture()
    );
    verify(s3Client).putObject(
        eq(
            PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(uploadedFile2Key)
                .contentDisposition("attachment; filename=\"%s\"".formatted(uploadedFile2.getFileName()))
                .contentLength(uploadedFile1.getFileSize())
                .contentType(uploadedFile1.getContentType())
                .build()
        ),
        requestBodyCaptor.capture()
    );

    assertThat(requestBodyCaptor.getAllValues())
        .extracting(body -> CharStreams.toString(new InputStreamReader(body.contentStreamProvider().newStream(), Charsets.UTF_8)))
        .containsExactly("apple", "orange");
  }

  @Test
  void doesObjectExist_doesNotThrowNoSuchKeyException() {
    var key = "test-key";

    when(
        s3Client.headObject(
            HeadObjectRequest.builder()
                .bucket(s3Bucket)
                .key(key)
                .build()
        )
    ).thenReturn(HeadObjectResponse.builder().build());

    var result = publicDataS3Service.doesObjectExist(s3Bucket, key);

    assertThat(result).isTrue();
  }

  @Test
  void doesObjectExist_throwsNoSuchKeyException() {
    var key = "test-key";

    when(
        s3Client.headObject(
            HeadObjectRequest.builder()
                .bucket(s3Bucket)
                .key(key)
                .build()
        )
    ).thenThrow(NoSuchKeyException.class);

    var result = publicDataS3Service.doesObjectExist(s3Bucket, key);

    assertThat(result).isFalse();
  }
}
