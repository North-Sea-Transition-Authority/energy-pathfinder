package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectManagementDetailViewUtil {

  private ProjectManagementDetailViewUtil() {
    throw new IllegalStateException(
        "ProjectManagementDetailViewUtil is a util class and should not be instantiated"
    );
  }

  public static void setProjectManagementDetailViewCommonFields(
      ProjectManagementDetailView projectManagementDetailView,
      ProjectDetail projectDetail,
      WebUserAccount submitterUserAccount
  ) {
    projectManagementDetailView.setStatus(projectDetail.getStatus().getDisplayName());
    projectManagementDetailView.setSubmissionDate(DateUtil.formatInstant(projectDetail.getSubmittedInstant()));
    projectManagementDetailView.setSubmittedByUser(submitterUserAccount.getFullName());
    projectManagementDetailView.setSubmittedByUserEmail(submitterUserAccount.getEmailAddress());
  }
}
