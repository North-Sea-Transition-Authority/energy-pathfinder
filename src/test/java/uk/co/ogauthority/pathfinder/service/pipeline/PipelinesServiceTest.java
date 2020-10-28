package uk.co.ogauthority.pathfinder.service.pipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.rest.PipelinesRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;
import uk.co.ogauthority.pathfinder.repository.pipeline.PipelineRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.PipelineTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class PipelinesServiceTest {

  @Mock
  private PipelineRepository pipelineRepository;

  private PipelinesService pipelinesService;

  @Before
  public void setup() {
    var searchSelectorService = new SearchSelectorService();
    pipelinesService = new PipelinesService(
        pipelineRepository,
        searchSelectorService
    );
  }

  @Test
  public void searchPipelinesWithNameContaining() {
    var searchTerm = "pipe";
    var pipeline = PipelineTestUtil.getPipeline();
    when(pipelineRepository.findAllByNameContainingIgnoreCase(searchTerm)).thenReturn(
        Collections.singletonList(pipeline)
    );
    var results = pipelinesService.searchPipelinesWithNameContaining(searchTerm);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getText()).isEqualToIgnoringCase(pipeline.getName());
  }

  @Test
  public void searchPipelinesWithNameContaining_whenNoResults() {
    var searchTerm = "pipe";
    when(pipelineRepository.findAllByNameContainingIgnoreCase(searchTerm)).thenReturn(
        Collections.emptyList()
    );
    var results = pipelinesService.searchPipelinesWithNameContaining(searchTerm);
    assertThat(results.size()).isEqualTo(0);
  }

  @Test
  public void getPipelinesRestUrl() {
    var restUrl = pipelinesService.getPipelinesRestUrl();
    assertThat(restUrl).isEqualTo(
        SearchSelectorService.route(on(PipelinesRestController.class).searchPipelines(null))
    );
  }

  @Test
  public void getPreSelectedPipeline_whenPipelineIsNull_thenEmptyMap() {
    var result = pipelinesService.getPreSelectedPipeline(null);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedPipeline_whenPipelineIsFromListEntry_thenFromListResult() {

    final Integer fromListSelectionId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline(fromListSelectionId, "A pipeline");

    when(pipelineRepository.findById(fromListSelectionId))
        .thenReturn(Optional.of(pipeline));

    var result = pipelinesService.getPreSelectedPipeline(String.valueOf(fromListSelectionId));
    assertThat(result).containsExactly(
        entry(pipeline.getSelectionId(), pipeline.getSelectionText())
    );
  }

  @Test
  public void getPipelineAsList_whenPipelineIsNull_thenEmptyList() {
    var result = pipelinesService.getPipelineAsList(null);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPipelineAsList_whenPipelineIsFromListEntry_thenPopulatedListReturned() {

    final Integer fromListSelectionId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline(fromListSelectionId, "A pipeline");

    when(pipelineRepository.findById(fromListSelectionId))
        .thenReturn(Optional.of(pipeline));

    var result = pipelinesService.getPipelineAsList(String.valueOf(fromListSelectionId));
    assertThat(result).containsExactly(pipeline);
  }

  @Test
  public void getPipelineAsList_whenPipelineIsFromListButNotFound_thenEmptyList() {

    final Integer fromListSelectionId = 1234;

    when(pipelineRepository.findById(fromListSelectionId))
        .thenReturn(Optional.empty());

    var result = pipelinesService.getPipelineAsList(String.valueOf(fromListSelectionId));
    assertThat(result).isEmpty();
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getPipelineByIdOrError_whenNotFound_thenException() {

    final Integer pipelineId = 1234;

    when(pipelineRepository.findById(pipelineId))
        .thenReturn(Optional.empty());

    pipelinesService.getPipelineByIdOrError(pipelineId);
  }

  @Test
  public void getPipelineByIdOrError_whenFound_thenPipelineReturned() {

    final Integer pipelineId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline();

    when(pipelineRepository.findById(pipelineId))
        .thenReturn(Optional.of(pipeline));

    var result = pipelinesService.getPipelineByIdOrError(pipelineId);
    assertThat(result).isEqualTo(pipeline);
  }

  @Test
  public void findById_whenFound_thenEntityReturned() {

    final Integer pipelineId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline();

    when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

    var result = pipelinesService.findById(pipelineId);
    assertThat(result).contains(pipeline);
  }

  @Test
  public void findById_whenNotFound_thenEmptyOptionalReturned() {

    final Integer pipelineId = 1234;

    when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

    var result = pipelinesService.findById(pipelineId);
    assertThat(result).isNotPresent();
  }
}
