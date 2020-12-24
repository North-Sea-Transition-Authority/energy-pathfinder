package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectarchive.ProjectArchiveDetail;

public class ProjectArchiveTestUtil {

  private static final String ARCHIVE_REASON = "Archive reason";

  private ProjectArchiveTestUtil() {
    throw new IllegalStateException("ProjectArchiveTestUtil is a utility class and should not be instantiated");
  }

  public static ProjectArchiveDetail createProjectArchiveDetail(ProjectDetail projectDetail) {
    var projectArchiveDetail = new ProjectArchiveDetail();
    projectArchiveDetail.setProjectDetail(projectDetail);
    projectArchiveDetail.setArchiveReason(ARCHIVE_REASON);
    return projectArchiveDetail;
  }
}
