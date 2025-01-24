package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectOperatorRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@ExtendWith(MockitoExtension.class)
class ForwardWorkPlanJsonServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectOperatorRepository projectOperatorRepository;

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

    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.FORWARD_WORK_PLAN))
        .thenReturn(List.of(projectDetail1, projectDetail2, projectDetail3));

    when(projectOperatorRepository.findAll()).thenReturn(List.of(projectOperator1, projectOperator2, projectOperator3));

    var forwardWorkPlanJsons = forwardWorkPlanJsonService.getPublishedForwardWorkPlans();

    assertThat(forwardWorkPlanJsons).containsExactlyInAnyOrder(
        ForwardWorkPlanJson.from(
            projectDetail1,
            projectOperator1
        ),
        ForwardWorkPlanJson.from(
            projectDetail2,
            projectOperator2
        ),
        ForwardWorkPlanJson.from(
            projectDetail3,
            projectOperator3
        )
    );
  }
}
