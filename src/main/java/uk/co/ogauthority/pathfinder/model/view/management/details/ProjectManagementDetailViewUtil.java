package uk.co.ogauthority.pathfinder.model.view.management.details;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectManagementDetailViewUtil {

  public static ProjectManagementDetailView from(ProjectDetail projectDetail,
                                                 ProjectInformation projectInformation,
                                                 ProjectLocation projectLocation,
                                                 WebUserAccount submitterAccount) {
    var projectManagementDetailView = new ProjectManagementDetailView();

    projectManagementDetailView.setFieldStage(projectInformation.getFieldStage().getDisplayName());

    var field = projectLocation.getField() != null
        ? projectLocation.getField().getFieldName()
        : projectLocation.getManualFieldName();
    projectManagementDetailView.setField(field);

    projectManagementDetailView.setStatus(projectDetail.getStatus().getDisplayName());
    projectManagementDetailView.setSubmissionDate(DateUtil.formatInstant(projectDetail.getSubmittedInstant()));
    projectManagementDetailView.setSubmittedByUser(submitterAccount.getFullName());
    projectManagementDetailView.setSubmittedByUserEmail(submitterAccount.getEmailAddress());

    return projectManagementDetailView;
  }
}
