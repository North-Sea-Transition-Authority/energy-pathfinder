package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;

class PublicDataUploadedFileUtil {

  private PublicDataUploadedFileUtil() {
    throw new IllegalStateException("PublicDataUploadedFileUtil is a utility class and should not be instantiated");
  }

  static String getS3ObjectKey(UploadedFile uploadedFile) {
    return "uploaded-files/%s".formatted(uploadedFile.getFileId());
  }
}
