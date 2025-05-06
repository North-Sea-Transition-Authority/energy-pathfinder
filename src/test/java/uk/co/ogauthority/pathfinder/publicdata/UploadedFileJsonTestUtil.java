package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;

class UploadedFileJsonTestUtil {

  static Builder newBuilder() {
    return new Builder();
  }

  static class Builder {

    private String name = "Test PDF.pdf";
    private String s3ObjectKey = PublicDataUploadedFileUtil.getS3ObjectKey(UploadedFileUtil.createUploadedFile());

    private Builder() {
    }

    Builder withName(String name) {
      this.name = name;
      return this;
    }

    Builder withS3ObjectKey(String s3ObjectKey) {
      this.s3ObjectKey = s3ObjectKey;
      return this;
    }

    UploadedFileJson build() {
      return new UploadedFileJson(name, s3ObjectKey);
    }
  }
}
