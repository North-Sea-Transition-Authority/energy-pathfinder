package uk.co.ogauthority.pathfinder.testutil;

import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;

public class ProjectFileTestUtil {
  public static final String FILE_ID = "aFileId";
  public static final String FILE_NAME = "filename";
  public static final byte[] BYTES = "bytesbytesbytes".getBytes();

  public static ProjectDetailFile getProjectDetailFile(ProjectDetail detail) {
    return new ProjectDetailFile(detail, getUploadedFile(), ProjectDetailFilePurpose.UPCOMING_TENDER, FileLinkStatus.FULL);
  }

  public static UploadedFile getUploadedFile() {
    var file = new UploadedFile(FILE_ID, FILE_NAME);
    file.setFileSize(50L);
    file.setContentType("text/plain");
    try {
      file.setFileData(new SerialBlob(BYTES));
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
    }
    return file;
  }
}
