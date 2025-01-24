package uk.co.ogauthority.pathfinder.publicdata;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;

record ForwardWorkPlanDetailsJson(
    String operatorName
) {

  static ForwardWorkPlanDetailsJson from(ProjectOperator projectOperator) {
    var operatorName = projectOperator.getOrganisationGroup().getName();

    return new ForwardWorkPlanDetailsJson(operatorName);
  }
}
