package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;

record UploadedFileJson(
    String name,
    String s3ObjectKey
) {

  static UploadedFileJson from(UploadedFile uploadedFile) {
    var name = uploadedFile.getFileName();
    var s3ObjectKey = PublicDataUploadedFileUtil.getS3ObjectKey(uploadedFile);

    return new UploadedFileJson(name, s3ObjectKey);
  }
}
