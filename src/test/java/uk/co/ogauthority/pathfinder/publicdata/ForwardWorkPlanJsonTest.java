package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class ForwardWorkPlanJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator
    );

    var expectedForwardWorkPlanJson = new ForwardWorkPlanJson(
        projectDetail.getProject().getId(),
        ForwardWorkPlanDetailsJson.from(projectOperator),
        LocalDateTime.ofInstant(projectDetail.getSubmittedInstant(), ZoneId.systemDefault())
    );

    assertThat(forwardWorkPlanJson).isEqualTo(expectedForwardWorkPlanJson);
  }
}
