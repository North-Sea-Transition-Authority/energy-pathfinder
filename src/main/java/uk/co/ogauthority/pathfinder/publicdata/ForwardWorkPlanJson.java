package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;

record ForwardWorkPlanJson(
    Integer id,
    ForwardWorkPlanDetailsJson details,
    LocalDateTime submittedOn
) {

  static ForwardWorkPlanJson from(
      ProjectDetail projectDetail,
      ProjectOperator projectOperator
  ) {
    var id = projectDetail.getProject().getId();

    var details = ForwardWorkPlanDetailsJson.from(projectOperator);

    var submittedOn = LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault());

    return new ForwardWorkPlanJson(
        id,
        details,
        submittedOn
    );
  }
}
