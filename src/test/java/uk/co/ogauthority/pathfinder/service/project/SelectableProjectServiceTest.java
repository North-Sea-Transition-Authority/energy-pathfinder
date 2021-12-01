package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.repository.project.SelectableProjectRepository;

@RunWith(MockitoJUnitRunner.class)
public class SelectableProjectServiceTest {

  @Mock
  private SelectableProjectRepository selectableProjectRepository;

  private SelectableProjectService selectableProjectService;

  @Before
  public void setup() {
    selectableProjectService = new SelectableProjectService(selectableProjectRepository);
  }

  @Test
  public void getSelectableProjectsByIdIn_whenNoResults_thenEmptyList() {
    final List<Integer> emptyProjectIdList = Collections.emptyList();
    when(selectableProjectRepository.findAllByProjectIdIn(emptyProjectIdList)).thenReturn(Collections.emptyList());

    final var resultingProjects = selectableProjectService.getSelectableProjectsByIdIn(emptyProjectIdList);
    assertThat(resultingProjects).isEmpty();
  }

  @Test
  public void getSelectableProjectsByIdIn_whenResults_thenPopulatedList() {

    final List<Integer> populatedProjectIdList = List.of(100);
    final var expectedSelectableProjects = List.of(new SelectableProject());

    when(selectableProjectRepository.findAllByProjectIdIn(populatedProjectIdList)).thenReturn(expectedSelectableProjects);

    final var resultingProjects = selectableProjectService.getSelectableProjectsByIdIn(populatedProjectIdList);
    assertThat(resultingProjects).isEqualTo(expectedSelectableProjects);
  }

  @Test
  public void getPublishedSelectableProjects_verifyInteractions() {

    final var searchTerm = "SEARCH TERM";
    final var projectType = ProjectType.INFRASTRUCTURE;

    selectableProjectService.getPublishedSelectableProjects(searchTerm, projectType);

    verify(selectableProjectRepository, times(1)).findAllPublishedProjectsByProjectDisplayNameOrOperatorGroupNameContainingIgnoreCase(
        searchTerm,
        projectType
    );
  }

}