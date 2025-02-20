package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;

public class UpcomingTenderFileLinkUtil {

  public static UpcomingTenderFileLink createUpcomingTenderFileLink() {
    var projectDetail = ProjectUtil.getProjectDetails();
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    var projectDetailFile = ProjectFileTestUtil.getProjectDetailFile(projectDetail);
    return createUpcomingTenderFileLink(upcomingTender, projectDetailFile);
  }

  public static UpcomingTenderFileLink createUpcomingTenderFileLink(UpcomingTender upcomingTender) {
    return createUpcomingTenderFileLink(null, upcomingTender);
  }

  public static UpcomingTenderFileLink createUpcomingTenderFileLink(Integer id, UpcomingTender upcomingTender) {
    return createUpcomingTenderFileLink(id, upcomingTender, ProjectFileTestUtil.getProjectDetailFile(upcomingTender.getProjectDetail()));
  }

  public static UpcomingTenderFileLink createUpcomingTenderFileLink(
      UpcomingTender upcomingTender,
      ProjectDetailFile projectDetailFile
  ) {
    return createUpcomingTenderFileLink(null, upcomingTender, projectDetailFile);
  }

  public static UpcomingTenderFileLink createUpcomingTenderFileLink(
      Integer id,
      UpcomingTender upcomingTender,
      ProjectDetailFile projectDetailFile
  ) {
    var upcomingTenderFileLink = new UpcomingTenderFileLink(id);
    upcomingTenderFileLink.setUpcomingTender(upcomingTender);
    upcomingTenderFileLink.setProjectDetailFile(projectDetailFile);
    return upcomingTenderFileLink;
  }
}
