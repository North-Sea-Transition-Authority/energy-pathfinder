package uk.co.ogauthority.pathfinder.model.view.projectmanagement.details;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;

public class InfrastructureProjectManagementDetailViewUtil {

  private InfrastructureProjectManagementDetailViewUtil() {
    throw new IllegalStateException(
        "InfrastructureProjectManagementDetailViewUtil is a util class and should not be instantiated"
    );
  }

  public static InfrastructureProjectManagementDetailView from(ProjectDetail projectDetail,
                                                               FieldStage fieldStage,
                                                               DevUkField field,
                                                               boolean isEnergyTransitionProject,
                                                               WebUserAccount submitterAccount) {

    final var projectManagementDetailView = new InfrastructureProjectManagementDetailView();

    projectManagementDetailView.setFieldStage(fieldStage.getDisplayName());

    final var fieldName = field != null
        ? field.getFieldName()
        : null;
    projectManagementDetailView.setField(fieldName);

    projectManagementDetailView.setIsEnergyTransitionProject(isEnergyTransitionProject);

    ProjectManagementDetailViewUtil.setProjectManagementDetailViewCommonFields(
        projectManagementDetailView,
        projectDetail,
        submitterAccount
    );

    return projectManagementDetailView;
  }
}
