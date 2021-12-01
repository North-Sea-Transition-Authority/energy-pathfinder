package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.forwardworkplan;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailViewUtil;

public class ForwardWorkPlanManagementDetailViewUtil {

  private ForwardWorkPlanManagementDetailViewUtil() {
    throw new IllegalStateException(
        "ForwardWorkPlanManagementDetailViewUtil is a util class and should not be instantiated"
    );
  }

  public static ForwardWorkPlanManagementDetailView from(ProjectDetail projectDetail,
                                                         WebUserAccount submitterAccount) {

    final var projectManagementDetailView = new ForwardWorkPlanManagementDetailView();

    ProjectManagementDetailViewUtil.setProjectManagementDetailViewCommonFields(
        projectManagementDetailView,
        projectDetail,
        submitterAccount
    );

    return projectManagementDetailView;
  }
}
