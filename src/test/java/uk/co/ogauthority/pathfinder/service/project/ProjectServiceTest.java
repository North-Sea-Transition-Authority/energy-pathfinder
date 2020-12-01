package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

  private static final int PROJECT_ID = 1;
  private static final int PROJECT_VERSION = 2;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  private ProjectService projectService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectService = new ProjectService(projectDetailsRepository);
  }

  @Test
  public void getLatestDetail() {
    var projectDetail = ProjectUtil.getProjectDetails();

    when(projectDetailsRepository.findByProjectIdAndIsCurrentVersionIsTrue(PROJECT_ID)).thenReturn(
        Optional.of(projectDetail)
    );

    var result = projectService.getLatestDetail(PROJECT_ID);
    assertThat(result).contains(projectDetail);
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
  public void setProjectDetailStatus() {
    projectDetail.setStatus(ProjectStatus.DRAFT);
    projectService.setProjectDetailStatus(projectDetail, ProjectStatus.PUBLISHED);
    assertThat(projectDetail.getStatus()).isEqualTo(ProjectStatus.PUBLISHED);
    verify(projectDetailsRepository, times(1)).save(projectDetail);
  }
}
