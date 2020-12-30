package uk.co.ogauthority.pathfinder.model.view.projectarchive;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projectarchive.ProjectArchiveDetail;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectArchiveDetailViewUtil {

  private ProjectArchiveDetailViewUtil() {
    throw new IllegalStateException("ProjectArchiveDetailViewUtil is a utility class and should not be instantiated.");
  }

  public static ProjectArchiveDetailView from(ProjectArchiveDetail projectArchiveDetail,
                                              Instant archivedInstant,
                                              WebUserAccount archivedByUser) {
    var projectArchiveDetailView = new ProjectArchiveDetailView();
    projectArchiveDetailView.setArchiveReason(projectArchiveDetail.getArchiveReason());
    projectArchiveDetailView.setArchivedDate(DateUtil.formatInstant(archivedInstant));
    projectArchiveDetailView.setArchivedByUserName(archivedByUser.getFullName());
    projectArchiveDetailView.setArchivedByUserEmailAddress(archivedByUser.getEmailAddress());
    return projectArchiveDetailView;
  }
}
