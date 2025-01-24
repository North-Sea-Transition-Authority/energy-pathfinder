package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class ForwardWorkPlanJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanUpcomingTender1 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(1, projectDetail);
    var forwardWorkPlanUpcomingTender2 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(2, projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        List.of(forwardWorkPlanUpcomingTender1, forwardWorkPlanUpcomingTender2)
    );

    var expectedForwardWorkPlanJson = new ForwardWorkPlanJson(
        projectDetail.getProject().getId(),
        ForwardWorkPlanDetailsJson.from(projectOperator),
        Set.of(
            ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender1),
            ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender2)
        ),
        LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault())
    );

    assertThat(forwardWorkPlanJson).isEqualTo(expectedForwardWorkPlanJson);
  }

  @Test
  void from_forwardWorkPlanUpcomingTendersIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        null
    );

    assertThat(forwardWorkPlanJson.upcomingTenders()).isNull();
  }

  @Test
  void from_forwardWorkPlanUpcomingTendersIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanUpcomingTender1 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(1, projectDetail);
    var forwardWorkPlanUpcomingTender2 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(2, projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        List.of(forwardWorkPlanUpcomingTender1, forwardWorkPlanUpcomingTender2)
    );

    assertThat(forwardWorkPlanJson.upcomingTenders()).containsExactlyInAnyOrder(
        ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender1),
        ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender2)
    );
  }
}
