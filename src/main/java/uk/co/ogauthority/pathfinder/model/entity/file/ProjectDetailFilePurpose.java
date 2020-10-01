package uk.co.ogauthority.pathfinder.model.entity.file;

import uk.co.ogauthority.pathfinder.controller.file.PathfinderFileUploadController;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.controller.test.file.TestFileUploadController;

public enum ProjectDetailFilePurpose {

  UPCOMING_TENDER(UpcomingTendersController.class),
  COLLABORATION_OPPORTUNITY(CollaborationOpportunitiesController.class),
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
