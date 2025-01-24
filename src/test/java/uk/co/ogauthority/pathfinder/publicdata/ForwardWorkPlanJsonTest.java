package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
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

    var forwardWorkPlanAwardedContract1 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(1, projectDetail);
    var forwardWorkPlanAwardedContract2 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(2, projectDetail);

    var forwardWorkPlanCollaborationOpportunity1 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail);
    var forwardWorkPlanCollaborationOpportunity2 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        List.of(forwardWorkPlanUpcomingTender1, forwardWorkPlanUpcomingTender2),
        List.of(forwardWorkPlanAwardedContract1, forwardWorkPlanAwardedContract2),
        List.of(forwardWorkPlanCollaborationOpportunity1, forwardWorkPlanCollaborationOpportunity2)
    );

    var expectedForwardWorkPlanJson = new ForwardWorkPlanJson(
        projectDetail.getProject().getId(),
        ForwardWorkPlanDetailsJson.from(projectOperator),
        Set.of(
            ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender1),
            ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender2)
        ),
        Set.of(
            AwardedContractJson.from(forwardWorkPlanAwardedContract1),
            AwardedContractJson.from(forwardWorkPlanAwardedContract2)
        ),
        Set.of(
            CollaborationOpportunityJson.from(forwardWorkPlanCollaborationOpportunity1),
            CollaborationOpportunityJson.from(forwardWorkPlanCollaborationOpportunity2)
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
        null,
        null,
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
        List.of(forwardWorkPlanUpcomingTender1, forwardWorkPlanUpcomingTender2),
        null,
        null
    );

    assertThat(forwardWorkPlanJson.upcomingTenders()).containsExactlyInAnyOrder(
        ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender1),
        ForwardWorkPlanUpcomingTenderJson.from(forwardWorkPlanUpcomingTender2)
    );
  }

  @Test
  void from_forwardWorkPlanAwardedContractsIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        null,
        null,
        null
    );

    assertThat(forwardWorkPlanJson.awardedContracts()).isNull();
  }

  @Test
  void from_forwardWorkPlanAwardedContractsIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanAwardedContract1 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(1, projectDetail);
    var forwardWorkPlanAwardedContract2 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(2, projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        null,
        List.of(forwardWorkPlanAwardedContract1, forwardWorkPlanAwardedContract2),
        null
    );

    assertThat(forwardWorkPlanJson.awardedContracts()).containsExactlyInAnyOrder(
        AwardedContractJson.from(forwardWorkPlanAwardedContract1),
        AwardedContractJson.from(forwardWorkPlanAwardedContract2)
    );
  }

  @Test
  void from_forwardWorkPlanCollaborationOpportunitiesIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        null,
        null,
        null
    );

    assertThat(forwardWorkPlanJson.collaborationOpportunities()).isNull();
  }

  @Test
  void from_forwardWorkPlanCollaborationOpportunitiesIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectOperator = ProjectOperatorTestUtil.getOperator(projectDetail);

    var forwardWorkPlanCollaborationOpportunity1 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail);
    var forwardWorkPlanCollaborationOpportunity2 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail);

    var forwardWorkPlanJson = ForwardWorkPlanJson.from(
        projectDetail,
        projectOperator,
        null,
        null,
        List.of(forwardWorkPlanCollaborationOpportunity1, forwardWorkPlanCollaborationOpportunity2)
    );

    assertThat(forwardWorkPlanJson.collaborationOpportunities()).containsExactlyInAnyOrder(
        CollaborationOpportunityJson.from(forwardWorkPlanCollaborationOpportunity1),
        CollaborationOpportunityJson.from(forwardWorkPlanCollaborationOpportunity2)
    );
  }
}
