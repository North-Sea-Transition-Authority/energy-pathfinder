package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution.ForwardWorkPlanContributorDetails;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanProjectContributorFormSectionServiceTest {

  private final ProjectDetail detail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

  @Mock
  private ForwardWorkPlanProjectContributorManagementService forwardWorkPlanProjectContributorManagementService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ProjectContributorsCommonService projectContributorsCommonService;

  private ForwardWorkPlanProjectContributorFormSectionService forwardWorkPlanProjectContributorFormSectionService;

  @Before
  public void setup() {
    forwardWorkPlanProjectContributorFormSectionService =
        new ForwardWorkPlanProjectContributorFormSectionService(
            forwardWorkPlanProjectContributorManagementService,
            entityDuplicationService,
            projectContributorsCommonService
        );
  }

  @Test
  public void isComplete_isValid_assertTrue() {
    when(forwardWorkPlanProjectContributorManagementService.isValid(detail, ValidationType.FULL)).thenReturn(true);

    assertThat(forwardWorkPlanProjectContributorFormSectionService.isComplete(detail)).isTrue();
  }

  @Test
  public void isComplete_isNotValid_assertFalse() {
    when(forwardWorkPlanProjectContributorManagementService.isValid(detail, ValidationType.FULL)).thenReturn(false);

    assertThat(forwardWorkPlanProjectContributorFormSectionService.isComplete(detail)).isFalse();
  }

  @Test
  public void getSupportedProjectTypes_assertForwardWorkPlan() {
    assertThat(forwardWorkPlanProjectContributorFormSectionService.getSupportedProjectTypes())
        .containsExactly(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void removeSectionData_isForwardWorkPlan_verifyMethodCall() {
    forwardWorkPlanProjectContributorFormSectionService.removeSectionData(detail);

    verify(forwardWorkPlanProjectContributorManagementService, times(1))
        .removeForwardProjectContributorsForDetail(detail);
  }

  @Test
  public void removeSectionData_isNotForwardWorkPlan_verifyMethodCall() {
    var notForwardWorkPlanDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);
    forwardWorkPlanProjectContributorFormSectionService.removeSectionData(notForwardWorkPlanDetail);

    verify(forwardWorkPlanProjectContributorManagementService, never())
        .removeForwardProjectContributorsForDetail(notForwardWorkPlanDetail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {
    var projectContributors = List.of(
        ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1),
        ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2)
    );
    var forwardWorkPlanContributor = new ForwardWorkPlanContributorDetails(detail, false);
    var newDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);
    when(projectContributorsCommonService.getProjectContributorsForDetail(detail))
        .thenReturn(projectContributors);
    when(forwardWorkPlanProjectContributorManagementService.getForwardProjectContributorForDetail(detail))
        .thenReturn(forwardWorkPlanContributor);

    forwardWorkPlanProjectContributorFormSectionService.copySectionData(detail, newDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        projectContributors,
        newDetail,
        ProjectContributor.class
    );

    verify(entityDuplicationService, times(1)).duplicateEntityAndSetNewParent(
        forwardWorkPlanContributor,
        newDetail,
        ForwardWorkPlanContributorDetails.class
    );
  }
}