package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.model.entity.file.FileUploadStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

public class UploadedFileUtil {

  private final static String FILE_ID = "1234abc";
  private final static String FILE_NAME = "file-name";
  private final static Long FILE_SIZE = 100L;
  private final static String FILE_DESCRIPTION = "description";
  private final static Instant FILE_UPLOADED_TIME = Instant.now();
  private final static String FILE_URL = "file-url";

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
    return createUploadedFile(FILE_ID);
  }

  public static UploadedFile createUploadedFile(String fileId) {
    var uploadedFile = new UploadedFile();
    uploadedFile.setFileId(fileId);
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

  public static UploadFileWithDescriptionForm createUploadFileWithDescriptionForm() {
    var form = new UploadFileWithDescriptionForm();
    form.setUploadedFileId(FILE_ID);
    form.setUploadedFileDescription(FILE_DESCRIPTION);
    form.setUploadedFileInstant(FILE_UPLOADED_TIME);
    return form;
  }
}
