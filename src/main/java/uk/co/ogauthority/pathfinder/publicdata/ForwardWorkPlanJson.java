package uk.co.ogauthority.pathfinder.publicdata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanupcomingtender.ForwardWorkPlanUpcomingTender;

record ForwardWorkPlanJson(
    Integer id,
    ForwardWorkPlanDetailsJson details,
    Set<ForwardWorkPlanUpcomingTenderJson> upcomingTenders,
    LocalDateTime submittedOn
) {

  static ForwardWorkPlanJson from(
      ProjectDetail projectDetail,
      ProjectOperator projectOperator,
      Collection<ForwardWorkPlanUpcomingTender> forwardWorkPlanUpcomingTenders
  ) {
    var id = projectDetail.getProject().getId();

    var details = ForwardWorkPlanDetailsJson.from(projectOperator);

    var upcomingTenders = forwardWorkPlanUpcomingTenders != null
        ? forwardWorkPlanUpcomingTenders.stream().map(ForwardWorkPlanUpcomingTenderJson::from).collect(Collectors.toSet())
        : null;

    var submittedOn = LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault());

    return new ForwardWorkPlanJson(
        id,
        details,
        upcomingTenders,
        submittedOn
    );
  }
}
