package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;

public class UpcomingTenderFileLinkUtil {

  public static UpcomingTenderFileLink createUpcomingTenderFileLink() {
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(ProjectUtil.getProjectDetails());
    var projectDetailFile = new ProjectDetailFile();
    return createUpcomingTenderFileLink(upcomingTender, projectDetailFile);
  }

  public static UpcomingTenderFileLink createUpcomingTenderFileLink(UpcomingTender upcomingTender,
                                                                    ProjectDetailFile projectDetailFile) {
    var upcomingTenderFileLink = new UpcomingTenderFileLink();
    upcomingTenderFileLink.setUpcomingTender(upcomingTender);
    upcomingTenderFileLink.setProjectDetailFile(projectDetailFile);
    return upcomingTenderFileLink;
  }
}
