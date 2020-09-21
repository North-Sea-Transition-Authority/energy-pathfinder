package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.entity.file.FileUploadStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class UploadedFileUtil {

  private static String FILE_ID = "1234abc";
  private static String FILE_NAME = "file-name";
  private static Long FILE_SIZE = 100L;
  private static String FILE_DESCRIPTION = "description";
  private static Instant FILE_UPLOADED_TIME = Instant.now();
  private static String FILE_URL = "file-url";

  private UploadedFileUtil() {
    throw new IllegalStateException("UploadedFileUtil is a utility class and should not be instantiated");
  }

  public static UploadedFileView createUploadedFileView() {
    return new UploadedFileView(
        FILE_ID,
        FILE_NAME,
        FILE_SIZE,
        FILE_DESCRIPTION,
        FILE_UPLOADED_TIME,
        FILE_URL
    );
  }

  public static UploadedFile createUploadedFile() {
    var uploadedFile = new UploadedFile();
    uploadedFile.setFileId(FILE_ID);
    uploadedFile.setFileName(FILE_NAME);
    uploadedFile.setFileData(null);
    uploadedFile.setContentType("CONTENT_TYPE");
    uploadedFile.setFileSize(FILE_SIZE);
    uploadedFile.setUploadDatetime(FILE_UPLOADED_TIME);
    uploadedFile.setUploadedByWuaId(1);
    uploadedFile.setLastUpdatedByWuaId(1);
    uploadedFile.setStatus(FileUploadStatus.CURRENT);
    return uploadedFile;
  }
}
