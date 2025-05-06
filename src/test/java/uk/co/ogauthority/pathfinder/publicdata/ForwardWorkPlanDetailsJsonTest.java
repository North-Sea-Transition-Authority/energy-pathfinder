package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class ForwardWorkPlanDetailsJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanDetailsJson = ForwardWorkPlanDetailsJson.from(projectOperator);

    var expectedForwardWorkPlanDetailsJson = new ForwardWorkPlanDetailsJson(
        projectOperator.getOrganisationGroup().getName()
    );

    assertThat(forwardWorkPlanDetailsJson).isEqualTo(expectedForwardWorkPlanDetailsJson);
  }
}
