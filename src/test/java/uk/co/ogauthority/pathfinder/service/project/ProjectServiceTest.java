package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.project.ProjectRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

  private static final int PROJECT_ID = 1;
  private static final int PROJECT_VERSION = 2;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  private ProjectService projectService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectService = new ProjectService(projectRepository, projectDetailsRepository);
    when(projectDetailsRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void getLatestDetail_whenFound_thenReturned() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectDetailsRepository.findByProjectIdAndIsCurrentVersionIsTrue(PROJECT_ID)).thenReturn(
        Optional.of(projectDetail)
    );

    var result = projectService.getLatestDetail(PROJECT_ID);
    assertThat(result).contains(projectDetail);
  }

  @Test
  public void getLatestDetailOrError_whenExists_thenReturn() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectDetailsRepository.findByProjectIdAndIsCurrentVersionIsTrue(PROJECT_ID)).thenReturn(
        Optional.of(projectDetail)
    );

    var result = projectService.getLatestDetailOrError(PROJECT_ID);
    assertThat(result).isEqualTo(projectDetail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getLatestDetailOrError_whenNotFound_thenException() {
    when(projectDetailsRepository.findByProjectIdAndIsCurrentVersionIsTrue(PROJECT_ID)).thenReturn(
        Optional.empty()
    );

    projectService.getLatestDetailOrError(PROJECT_ID);
  }

  @Test
  public void getLatestSubmittedDetail_whenFound_thenReturned() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectDetailsRepository.findByProjectIdAndIsLatestSubmittedVersion(PROJECT_ID)).thenReturn(
        Optional.of(projectDetail)
    );

    var result = projectService.getLatestSubmittedDetail(PROJECT_ID);
    assertThat(result).contains(projectDetail);
  }

  @Test
  public void getLatestSubmittedDetailOrError_whenExists_thenReturn() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectDetailsRepository.findByProjectIdAndIsLatestSubmittedVersion(PROJECT_ID)).thenReturn(
        Optional.of(projectDetail)
    );

    var result = projectService.getLatestSubmittedDetailOrError(PROJECT_ID);
    assertThat(result).isEqualTo(projectDetail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getLatestSubmittedDetailOrError_whenNotFound_thenException() {
    when(projectDetailsRepository.findByProjectIdAndIsLatestSubmittedVersion(PROJECT_ID)).thenReturn(
        Optional.empty()
    );

    projectService.getLatestSubmittedDetailOrError(PROJECT_ID);
  }

  @Test
  public void getDetail_whenFound_thenReturn() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(projectDetailsRepository.findByProjectIdAndVersion(project.getId(), version))
        .thenReturn(Optional.of(projectDetail));

    assertThat(projectService.getDetail(project, version)).contains(projectDetail);
  }

  @Test
  public void getDetail_whenNotFound_thenReturnEmpty() {
    var project = projectDetail.getProject();
    var version = projectDetail.getVersion();

    when(projectDetailsRepository.findByProjectIdAndVersion(project.getId(), version))
        .thenReturn(Optional.empty());

    assertThat(projectService.getDetail(project, version)).isEmpty();
  }

  @Test
  public void getDetailOrError_whenExists_thenReturn() {
    when(projectDetailsRepository.findByProjectIdAndVersion(PROJECT_ID, PROJECT_VERSION)).thenReturn(
        Optional.of(projectDetail)
    );

    var result = projectService.getDetailOrError(PROJECT_ID, PROJECT_VERSION);
    assertThat(result).isEqualTo(projectDetail);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getDetailOrError_whenNotFound_thenException() {
    when(projectDetailsRepository.findByProjectIdAndVersion(PROJECT_ID, PROJECT_VERSION)).thenReturn(
        Optional.empty()
    );

    projectService.getDetailOrError(PROJECT_ID, PROJECT_VERSION);
  }

  @Test
  public void updateProjectDetailStatus() {
    projectDetail.setStatus(ProjectStatus.DRAFT);
    projectService.updateProjectDetailStatus(projectDetail, ProjectStatus.PUBLISHED);
    assertThat(projectDetail.getStatus()).isEqualTo(ProjectStatus.PUBLISHED);
    verify(projectDetailsRepository, times(1)).save(projectDetail);
  }

  @Test
  public void updateProjectDetailIsCurrentVersion() {
    projectDetail.setIsCurrentVersion(false);
    projectService.updateProjectDetailIsCurrentVersion(projectDetail, true);
    assertThat(projectDetail.getIsCurrentVersion()).isTrue();
    verify(projectDetailsRepository, times(1)).save(projectDetail);
  }

  @Test
  public void createNewProjectDetailVersion_assertRepositoryInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails();
    fromProjectDetail.setIsCurrentVersion(true);
    fromProjectDetail.setVersion(1);

    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    final var newProjectDetail = projectService.createNewProjectDetailVersion(fromProjectDetail, ProjectStatus.DRAFT, user);

    assertThat(fromProjectDetail.getIsCurrentVersion()).isFalse();
    assertThat(newProjectDetail.getIsCurrentVersion()).isTrue();
    assertThat(newProjectDetail.getVersion()).isEqualTo(fromProjectDetail.getVersion() + 1);
    assertThat(newProjectDetail.getCreatedDatetime()).isNotNull();
    assertThat(newProjectDetail.getProjectType()).isEqualTo(fromProjectDetail.getProjectType());

    verify(projectDetailsRepository, times(1)).save(fromProjectDetail);
    verify(projectDetailsRepository, times(1)).save(newProjectDetail);
  }

  @Test
  public void deleteProjectDetail() {
    projectService.deleteProjectDetail(projectDetail);

    verify(projectDetailsRepository, times(1)).delete(projectDetail);
  }

  @Test
  public void deleteProject() {
    var project = projectDetail.getProject();

    projectService.deleteProject(project);

    verify(projectRepository, times(1)).delete(project);
  }

  @Test
  public void isInfrastructureProject_whenInfrastructure_thenTrue() {
    projectDetail.setProjectType(ProjectType.INFRASTRUCTURE);
    assertThat(ProjectService.isInfrastructureProject(projectDetail)).isTrue();
  }

  @Test
  public void isInfrastructureProject_whenNotInfrastructure_thenFalse() {
    projectDetail.setProjectType(ProjectType.FORWARD_WORK_PLAN);
    assertThat(ProjectService.isInfrastructureProject(projectDetail)).isFalse();
  }

  @Test
  public void isInfrastructureProject_whenNullType_thenFalse() {
    projectDetail.setProjectType(null);
    assertThat(ProjectService.isInfrastructureProject(projectDetail)).isFalse();
  }

  @Test
  public void isForwardWorkPlanProject_whenWorkPlan_thenTrue() {
    projectDetail.setProjectType(ProjectType.FORWARD_WORK_PLAN);
    assertThat(ProjectService.isForwardWorkPlanProject(projectDetail)).isTrue();
  }

  @Test
  public void isForwardWorkPlanProject_whenNotWorkPlan_thenFalse() {
    projectDetail.setProjectType(ProjectType.INFRASTRUCTURE);
    assertThat(ProjectService.isForwardWorkPlanProject(projectDetail)).isFalse();
  }

  @Test
  public void isForwardWorkPlanProject_whenNullType_thenFalse() {
    projectDetail.setProjectType(null);
    assertThat(ProjectService.isForwardWorkPlanProject(projectDetail)).isFalse();
  }

  @Test
  public void getProjectTypeDisplayNameLowercase_smokeTest() {
    Arrays.asList(ProjectType.values()).forEach(projectType -> {
      projectDetail.setProjectType(projectType);
      final var expectedReturnValue = projectType.getLowercaseDisplayName();
      assertThat(ProjectService.getProjectTypeDisplayNameLowercase(projectDetail)).isEqualTo(expectedReturnValue);
    });
  }

  @Test
  public void getProjectTypeDisplayName_smokeTest() {
    Arrays.asList(ProjectType.values()).forEach(projectType -> {
      projectDetail.setProjectType(projectType);
      final var expectedReturnValue = projectType.getDisplayName();
      assertThat(ProjectService.getProjectTypeDisplayName(projectDetail)).isEqualTo(expectedReturnValue);
    });
  }

  @Test
  public void getDetailByIdOrError_whenFound_thenReturned() {
    var projectDetailId = projectDetail.getId();
    when(projectDetailsRepository.findById(projectDetailId)).thenReturn(Optional.of(projectDetail));

    var result = projectService.getDetailByIdOrError(projectDetailId);
    assertThat(result).isEqualTo(projectDetail);
  }

  @Test
  public void getDetailByIdOrError_whenNotFound_thenException() {
    var projectDetailId = 1;
    when(projectDetailsRepository.findById(projectDetailId)).thenReturn(Optional.empty());

    assertThrows(PathfinderEntityNotFoundException.class, () ->  projectService.getDetailByIdOrError(projectDetailId));
  }
}
