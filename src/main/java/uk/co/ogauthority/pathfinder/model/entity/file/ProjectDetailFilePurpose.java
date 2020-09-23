package uk.co.ogauthority.pathfinder.model.entity.file;

import uk.co.ogauthority.pathfinder.controller.file.PathfinderFileUploadController;
import uk.co.ogauthority.pathfinder.controller.test.file.TestFileUploadController;

public enum ProjectDetailFilePurpose {

  PLACEHOLDER(TestFileUploadController.class);

  private final Class<? extends PathfinderFileUploadController> fileControllerClass;

  ProjectDetailFilePurpose(
      Class<? extends PathfinderFileUploadController> fileControllerClass) {
    this.fileControllerClass = fileControllerClass;
  }

  public Class<? extends PathfinderFileUploadController> getFileControllerClass() {
    return fileControllerClass;
  }
}
