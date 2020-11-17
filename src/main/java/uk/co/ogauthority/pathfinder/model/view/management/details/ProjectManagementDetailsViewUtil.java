package uk.co.ogauthority.pathfinder.model.view.management.details;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.location.ProjectLocation;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class ProjectManagementDetailsViewUtil {

  public static ProjectManagementDetailsView from(ProjectDetail projectDetail,
                                                  ProjectInformation projectInformation,
                                                  ProjectLocation projectLocation,
                                                  WebUserAccount submitterAccount) {
    var projectManagementDetailsView = new ProjectManagementDetailsView();

    projectManagementDetailsView.setFieldStage(projectInformation.getFieldStage().getDisplayName());

    var field = projectLocation.getField() != null
        ? projectLocation.getField().getFieldName()
        : projectLocation.getManualFieldName();
    projectManagementDetailsView.setField(field);

    projectManagementDetailsView.setVersion(projectDetail.getVersion());
    projectManagementDetailsView.setSubmissionDate(DateUtil.formatInstant(projectDetail.getSubmittedInstant()));
    projectManagementDetailsView.setSubmittedByUser(submitterAccount.getFullName());
    projectManagementDetailsView.setSubmittedByUserEmail(submitterAccount.getEmailAddress());

    return projectManagementDetailsView;
  }
}
