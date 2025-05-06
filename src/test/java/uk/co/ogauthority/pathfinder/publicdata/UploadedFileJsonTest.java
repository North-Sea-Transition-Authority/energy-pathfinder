package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;

class UploadedFileJsonTest {

  @Test
  void from() {
    var uploadedFile = UploadedFileUtil.createUploadedFile();

    uploadedFile.setFileName("Test PDF.pdf");
    uploadedFile.setFileId("file-1");

    var uploadedFileJson = UploadedFileJson.from(uploadedFile);

    var expectedUploadedFileJson = new UploadedFileJson(
        "Test PDF.pdf",
        PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile)
    );

    assertThat(uploadedFileJson).isEqualTo(expectedUploadedFileJson);
  }
}
