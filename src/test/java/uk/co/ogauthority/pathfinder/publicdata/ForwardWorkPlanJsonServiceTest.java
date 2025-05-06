package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.utils.MapUtils;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityRepository;
import uk.co.ogauthority.pathfinder.repository.project.workplanupcomingtender.ForwardWorkPlanUpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanUpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanJsonServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectOperatorRepository projectOperatorRepository;

  @Mock
  private ForwardWorkPlanUpcomingTenderRepository forwardWorkPlanUpcomingTenderRepository;

  @Mock
  private ForwardWorkPlanAwardedContractRepository forwardWorkPlanAwardedContractRepository;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFileLinkRepository forwardWorkPlanCollaborationOpportunityFileLinkRepository;

  @InjectMocks
  private ForwardWorkPlanJsonService forwardWorkPlanJsonService;

  @Test
  void getPublishedForwardWorkPlans() {
    var projectDetail1 = ProjectUtil.getPublishedProjectDetails();
    projectDetail1.setId(1);
    projectDetail1.getProject().setId(2);

    var projectDetail2 = ProjectUtil.getPublishedProjectDetails();
    projectDetail2.setId(3);
    projectDetail2.getProject().setId(4);

    var projectDetail3 = ProjectUtil.getPublishedProjectDetails();
    projectDetail2.setId(5);
    projectDetail2.getProject().setId(6);

    var projectOperator1 = ProjectOperatorTestUtil.getOperator(projectDetail1);
    projectOperator1.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(1, "A Org Grp", "AOrgGrp"));

    var projectOperator2 = ProjectOperatorTestUtil.getOperator(projectDetail2);
    projectOperator2.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(2, "b Org Grp", "brgGrp"));

    var projectOperator3 = ProjectOperatorTestUtil.getOperator(projectDetail3);
    projectOperator3.setOrganisationGroup(
        TeamTestingUtil.generateOrganisationGroup(3, "C Org Grp", "COrgGrp"));

    var forwardWorkPlanUpcomingTender1 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(1, projectDetail1);
    var forwardWorkPlanUpcomingTender2 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(2, projectDetail1);
    var forwardWorkPlanUpcomingTender3 = ForwardWorkPlanUpcomingTenderUtil.getUpcomingTender(3, projectDetail2);

    var forwardWorkPlanAwardedContract1 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(1, projectDetail1);
    var forwardWorkPlanAwardedContract2 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(2, projectDetail1);
    var forwardWorkPlanAwardedContract3 = AwardedContractTestUtil.createForwardWorkPlanAwardedContract(3, projectDetail2);

    var forwardWorkPlanCollaborationOpportunity1 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(1, projectDetail1);
    var forwardWorkPlanCollaborationOpportunity2 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(2, projectDetail1);
    var forwardWorkPlanCollaborationOpportunity3 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(3, projectDetail2);

    var forwardWorkPlanCollaborationOpportunityFileLink1 = ForwardWorkPlanCollaborationOpportunityTestUtil
        .createCollaborationOpportunityFileLink(1, forwardWorkPlanCollaborationOpportunity1);
    var forwardWorkPlanCollaborationOpportunityFileLink2 = ForwardWorkPlanCollaborationOpportunityTestUtil
        .createCollaborationOpportunityFileLink(2, forwardWorkPlanCollaborationOpportunity2);

    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectTypes(EnumSet.of(ProjectType.FORWARD_WORK_PLAN)))
        .thenReturn(List.of(projectDetail1, projectDetail2, projectDetail3));

    when(projectOperatorRepository.findAll()).thenReturn(List.of(projectOperator1, projectOperator2, projectOperator3));

    when(forwardWorkPlanUpcomingTenderRepository.findAll())
        .thenReturn(List.of(forwardWorkPlanUpcomingTender1, forwardWorkPlanUpcomingTender2, forwardWorkPlanUpcomingTender3));

    when(forwardWorkPlanAwardedContractRepository.findAll())
        .thenReturn(List.of(forwardWorkPlanAwardedContract1, forwardWorkPlanAwardedContract2, forwardWorkPlanAwardedContract3));

    when(forwardWorkPlanCollaborationOpportunityRepository.findAll())
        .thenReturn(List.of(forwardWorkPlanCollaborationOpportunity1, forwardWorkPlanCollaborationOpportunity2, forwardWorkPlanCollaborationOpportunity3));

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAll())
        .thenReturn(List.of(forwardWorkPlanCollaborationOpportunityFileLink1, forwardWorkPlanCollaborationOpportunityFileLink2));

    var forwardWorkPlanJsons = forwardWorkPlanJsonService.getPublishedForwardWorkPlans();

    assertThat(forwardWorkPlanJsons).containsExactlyInAnyOrder(
        ForwardWorkPlanJson.from(
            projectDetail1,
            projectOperator1,
            List.of(forwardWorkPlanUpcomingTender1, forwardWorkPlanUpcomingTender2),
            List.of(forwardWorkPlanAwardedContract1, forwardWorkPlanAwardedContract2),
            Map.of(
                forwardWorkPlanCollaborationOpportunity1, forwardWorkPlanCollaborationOpportunityFileLink1,
                forwardWorkPlanCollaborationOpportunity2, forwardWorkPlanCollaborationOpportunityFileLink2
            )
        ),
        ForwardWorkPlanJson.from(
            projectDetail2,
            projectOperator2,
            List.of(forwardWorkPlanUpcomingTender3),
            List.of(forwardWorkPlanAwardedContract3),
            MapUtils.of(
                forwardWorkPlanCollaborationOpportunity3, null
            )
        ),
        ForwardWorkPlanJson.from(
            projectDetail3,
            projectOperator3,
            null,
            null,
            null
        )
    );
  }
}
