package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@ExtendWith(MockitoExtension.class)
class InfrastructureProjectJsonServiceTest {

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @InjectMocks
  private InfrastructureProjectJsonService infrastructureProjectJsonService;

  private ProjectDetail projectDetail1;
  private ProjectDetail projectDetail2;

  @BeforeEach
  void setUp() {
    projectDetail1 = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);
    projectDetail2 = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);
    var project2 = new Project();
    project2.setId(2);
    projectDetail2.setProject(project2);
  }

  @Test
  void getPublishedInfrastructureProjects_whenManyProject() {
    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.INFRASTRUCTURE))
        .thenReturn(List.of(projectDetail2, projectDetail1));

    var expectedInfrastructureProjectJsons = List.of(
        InfrastructureProjectJson.from(projectDetail2),
        InfrastructureProjectJson.from(projectDetail1)
    );

    assertThat(infrastructureProjectJsonService.getPublishedInfrastructureProjects())
        .isEqualTo(expectedInfrastructureProjectJsons);
  }

  @Test
  void getPublishedInfrastructureProjects_whenOneProject() {
    when(projectDetailsRepository.getAllPublishedProjectDetailsByProjectType(ProjectType.INFRASTRUCTURE))
        .thenReturn(List.of(projectDetail1));

    var expectedInfrastructureProjectJsons = List.of(InfrastructureProjectJson.from(projectDetail1));

    assertThat(infrastructureProjectJsonService.getPublishedInfrastructureProjects())
        .isEqualTo(expectedInfrastructureProjectJsons);
  }
}
