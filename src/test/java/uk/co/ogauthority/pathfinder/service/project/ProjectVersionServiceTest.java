package uk.co.ogauthority.pathfinder.service.project;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectVersionServiceTest {

  private static final int PROJECT_ID = 1;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  private ProjectVersionService projectVersionService;

  private final Project project = ProjectUtil.getProject();

  @Before
  public void setup() {
    projectVersionService = new ProjectVersionService(projectDetailsRepository);

    project.setId(PROJECT_ID);
  }

  @Test
  public void getProjectVersionDtos() {
    var projectVersionDtos = List.of(
        new ProjectVersionDto(1, Instant.now()),
        new ProjectVersionDto(2, Instant.now())
    );

    when(projectDetailsRepository.getProjectVersionDtos(PROJECT_ID)).thenReturn(projectVersionDtos);

    var result = projectVersionService.getProjectVersionDtos(project);
    assertThat(result).isEqualTo(projectVersionDtos);
  }
}
