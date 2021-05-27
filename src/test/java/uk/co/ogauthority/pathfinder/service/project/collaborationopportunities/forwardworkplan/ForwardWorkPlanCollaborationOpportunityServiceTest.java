package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityServiceTest {

  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityService = new ForwardWorkPlanCollaborationOpportunityService();
  }

  @Test
  public void isComplete_assertFalse() {
    final var projectDetail = ProjectUtil.getProjectDetails();
    final var isComplete = forwardWorkPlanCollaborationOpportunityService.isComplete(projectDetail);
    assertThat(isComplete).isFalse();
  }

  @Test
  public void canShowInTaskList_smokeTestProjectTypes_assertOnlyForwardWorkPlanAllowed() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var projectTypesToShowInTaskList = Set.of(ProjectType.FORWARD_WORK_PLAN);

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      final var canShowInTaskList = forwardWorkPlanCollaborationOpportunityService.canShowInTaskList(projectDetail);

      if (projectTypesToShowInTaskList.contains(projectType)) {
        assertThat(canShowInTaskList).isTrue();
      } else {
        assertThat(canShowInTaskList).isFalse();
      }
    });
  }

}