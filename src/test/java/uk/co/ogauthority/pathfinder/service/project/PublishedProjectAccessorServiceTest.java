package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.rest.PublishedProjectRestController;
import uk.co.ogauthority.pathfinder.model.entity.project.PublishedProject;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.project.PublishedProjectRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@RunWith(MockitoJUnitRunner.class)
public class PublishedProjectAccessorServiceTest {

  private static final String SEARCH_TERM = "search term";
  private static final ProjectType PROJECT_TYPE = ProjectType.INFRASTRUCTURE;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private PublishedProjectRepository publishedProjectRepository;

  private PublishedProjectAccessorService publishedProjectAccessorService;

  @Before
  public void setup() {
    publishedProjectAccessorService = new PublishedProjectAccessorService(
        searchSelectorService,
        publishedProjectRepository
    );
  }

  @Test
  public void searchProjectsWithDisplayNameContaining_verifyInteractions() {

    final var publishedProjectList = List.of(new PublishedProject());

    when(publishedProjectRepository.findAllByProjectDisplayNameContainingIgnoreCaseAndProjectTypeOrderByProjectDisplayName(
        SEARCH_TERM,
        PROJECT_TYPE
    )).thenReturn(publishedProjectList);

    publishedProjectAccessorService.searchProjectsWithDisplayNameContaining(
        SEARCH_TERM,
        PROJECT_TYPE
    );

    verify(searchSelectorService, times(1)).search(SEARCH_TERM, publishedProjectList);
  }

  @Test
  public void searchProjectsWithDisplayNameContaining_whenNoResults_thenEmptyListReturned() {

    when(searchSelectorService.search(eq(SEARCH_TERM), any())).thenReturn(Collections.emptyList());

    final var resultingRestSearchItems = publishedProjectAccessorService.searchProjectsWithDisplayNameContaining(
        SEARCH_TERM,
        PROJECT_TYPE
    );

    assertThat(resultingRestSearchItems).isEmpty();
  }

  @Test
  public void searchProjectsWithDisplayNameContaining_whenResults_thenPopulatedListReturned() {

    final var expectedRestSearchItems = List.of(new RestSearchItem("1", "value"));

    when(searchSelectorService.search(eq(SEARCH_TERM), any())).thenReturn(expectedRestSearchItems);

    final var resultingRestSearchItems = publishedProjectAccessorService.searchProjectsWithDisplayNameContaining(
        SEARCH_TERM,
        PROJECT_TYPE
    );

    assertThat(resultingRestSearchItems).isEqualTo(expectedRestSearchItems);
  }

  @Test
  public void getPublishedInfrastructureProjectsRestUrl() {

    final var expectedRestUrl = SearchSelectorService.route(on(PublishedProjectRestController.class)
        .searchPublishedInfrastructureProjects(null));

    final var resultingRestUrl = publishedProjectAccessorService.getPublishedInfrastructureProjectsRestUrl();

    assertThat(resultingRestUrl).isEqualTo(expectedRestUrl);
  }

}