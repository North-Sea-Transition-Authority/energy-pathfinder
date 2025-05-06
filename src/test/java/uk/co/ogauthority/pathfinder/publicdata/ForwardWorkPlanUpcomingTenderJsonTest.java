package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class ForwardWorkPlanUpcomingTenderJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var forwardWorkPlanUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    var forwardWorkPlanUpcomingTenderJson = ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender);

    var expectedForwardWorkPlanUpcomingTenderJson = new ForwardWorkPlanUpcomingTenderJson(
        forwardWorkPlanUpcomingTender.getId(),
        forwardWorkPlanUpcomingTender.getDepartmentType().name(),
        forwardWorkPlanUpcomingTender.getManualDepartmentType(),
        forwardWorkPlanUpcomingTender.getDescriptionOfWork(),
        QuarterYearJson.from(
            forwardWorkPlanUpcomingTender.getEstimatedTenderDateQuarter(),
            forwardWorkPlanUpcomingTender.getEstimatedTenderDateYear()
        ),
        forwardWorkPlanUpcomingTender.getContractBand().name(),
        forwardWorkPlanUpcomingTender.getContractTermDurationPeriod().name(),
        forwardWorkPlanUpcomingTender.getContractTermDuration(),
        ContactJson.from(forwardWorkPlanUpcomingTender)
    );

    assertThat(forwardWorkPlanUpcomingTenderJson).isEqualTo(expectedForwardWorkPlanUpcomingTenderJson);
  }

  @Test
  void from_departmentTypeIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var forwardWorkPlanUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    forwardWorkPlanUpcomingTender.setDepartmentType(Function.DRILLING);

    var forwardWorkPlanUpcomingTenderJson = ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender);

    assertThat(forwardWorkPlanUpcomingTenderJson.department()).isEqualTo(Function.DRILLING.name());
  }

  @Test
  void from_departmentTypeIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var forwardWorkPlanUpcomingTender = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(projectDetail);

    forwardWorkPlanUpcomingTender.setDepartmentType(null);

    var forwardWorkPlanUpcomingTenderJson = ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender);

    assertThat(forwardWorkPlanUpcomingTenderJson.department()).isNull();
  }
}
