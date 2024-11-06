package uk.co.ogauthority.pathfinder.publicdata;

import org.apache.commons.lang3.BooleanUtils;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;

record InfrastructureProjectDetailsJson(
    String operatorName,
    String title,
    String summary,
    String projectStage,
    String projectStageSubCategory
) {

  static InfrastructureProjectDetailsJson from(
      ProjectOperator projectOperator,
      ProjectInformation projectInformation
  ) {
    var operatorName = BooleanUtils.isFalse(projectOperator.isPublishedAsOperator())
        ? projectOperator.getPublishableOrganisationUnit().getName()
        : projectOperator.getOrganisationGroup().getName();

    var title = projectInformation.getProjectTitle();
    var summary = projectInformation.getProjectSummary();
    var projectStage = projectInformation.getFieldStage().name();

    var projectStageSubCategory = projectInformation.getFieldStageSubCategory() != null
        ? projectInformation.getFieldStageSubCategory().name()
        : null;

    return new InfrastructureProjectDetailsJson(
        operatorName,
        title,
        summary,
        projectStage,
        projectStageSubCategory
    );
  }
}
