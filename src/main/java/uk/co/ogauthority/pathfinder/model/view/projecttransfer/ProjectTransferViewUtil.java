package uk.co.ogauthority.pathfinder.model.view.projecttransfer;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projecttransfer.ProjectTransfer;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectTransferViewUtil {

  private ProjectTransferViewUtil() {
    throw new IllegalStateException("ProjectTransferViewUtil is a utility class and should not be instantiated.");
  }

  public static ProjectTransferView from(ProjectTransfer projectTransfer,
                                         WebUserAccount transferredByUser) {
    var projectTransferView = new ProjectTransferView();
    projectTransferView.setOldOperator(projectTransfer.getFromOrganisationGroup().getName());
    projectTransferView.setNewOperator(projectTransfer.getToOrganisationGroup().getName());
    projectTransferView.setTransferReason(projectTransfer.getTransferReason());
    projectTransferView.setTransferDate(DateUtil.formatInstant(projectTransfer.getTransferredInstant()));
    projectTransferView.setTransferredByUserName(transferredByUser.getFullName());
    projectTransferView.setTransferredByUserEmailAddress(transferredByUser.getEmailAddress());
    return projectTransferView;
  }
}
