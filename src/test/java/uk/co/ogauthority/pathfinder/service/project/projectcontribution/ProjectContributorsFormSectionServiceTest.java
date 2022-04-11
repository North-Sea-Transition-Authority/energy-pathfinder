package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.ProjectContributorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsFormSectionServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private ProjectContributorsManagementService projectContributorsManagementService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ProjectContributorsFormSectionService projectContributorsFormSectionService;
  private ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectContributorsFormSectionService = new ProjectContributorsFormSectionService(projectSetupService,
        projectContributorsManagementService, entityDuplicationService);
  }

  @Test
  public void isComplete_isValid_assertTrue() {
    when(projectContributorsManagementService.isValid(detail, ValidationType.FULL)).thenReturn(true);

    assertThat(projectContributorsFormSectionService.isComplete(detail)).isTrue();
  }

  @Test
  public void isComplete_isInvalid_assertFalse() {
    when(projectContributorsManagementService.isValid(detail, ValidationType.FULL)).thenReturn(false);

    assertThat(projectContributorsFormSectionService.isComplete(detail)).isFalse();
  }

  @Test
  public void getSupportedProjectTypes_assertInfrastructure() {
    assertThat(projectContributorsFormSectionService.getSupportedProjectTypes())
        .containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void canShowInTaskList_taskValidAndSelected_thenTrue() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.PROJECT_CONTRIBUTORS))
        .thenReturn(true);

    assertThat(projectContributorsFormSectionService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_taskInValidAndNotSelected_thenFalse() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.PROJECT_CONTRIBUTORS))
        .thenReturn(false);

    assertThat(projectContributorsFormSectionService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyMethodCall() {
    projectContributorsFormSectionService.removeSectionData(detail);

    verify(projectContributorsManagementService, times(1)).removeProjectContributorsForDetail(detail);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {
    var projectContributors = List.of(
        ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 1),
        ProjectContributorTestUtil.contributorWithGroupOrgId(detail, 2)
    );
    var newDetail =  ProjectUtil.getProjectDetails();
    when(projectContributorsManagementService.getProjectContributorsForDetail(detail))
        .thenReturn(projectContributors);

    projectContributorsFormSectionService.copySectionData(detail, newDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        projectContributors,
        newDetail,
        ProjectContributor.class
    );
  }
}