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
import uk.co.ogauthority.pathfinder.controller.rest.PipelineRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;
import uk.co.ogauthority.pathfinder.repository.pipeline.PipelineRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.PipelineTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class PipelineServiceTest {

  @Mock
  private PipelineRepository pipelineRepository;

  private PipelineService pipelineService;

  @Before
  public void setup() {
    var searchSelectorService = new SearchSelectorService();
    pipelineService = new PipelineService(
        pipelineRepository,
        searchSelectorService
    );
  }

  @Test
  public void searchPipelinesWithNameContaining() {
    var searchTerm = "pipe";
    var pipeline = PipelineTestUtil.getPipeline();
    when(pipelineRepository.findAllByNameContainingIgnoreCaseAndHistoricStatusFalse(searchTerm)).thenReturn(
        Collections.singletonList(pipeline)
    );
    var results = pipelineService.searchPipelinesWithNameContaining(searchTerm);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getText()).isEqualToIgnoringCase(pipeline.getName());
  }

  @Test
  public void searchPipelinesWithNameContaining_whenNoResults() {
    var searchTerm = "pipe";
    when(pipelineRepository.findAllByNameContainingIgnoreCaseAndHistoricStatusFalse(searchTerm)).thenReturn(
        Collections.emptyList()
    );
    var results = pipelineService.searchPipelinesWithNameContaining(searchTerm);
    assertThat(results).isEmpty();
  }

  @Test
  public void getPipelineRestUrl() {
    var restUrl = pipelineService.getPipelineRestUrl();
    assertThat(restUrl).isEqualTo(
        SearchSelectorService.route(on(PipelineRestController.class).searchPipelines(null))
    );
  }

  @Test
  public void getPreSelectedPipeline_whenPipelineIsNull_thenEmptyMap() {
    var result = pipelineService.getPreSelectedPipeline(null);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedPipeline_whenPipelineIsFromListEntry_thenFromListResult() {

    final Integer fromListSelectionId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline(fromListSelectionId, "A pipeline");

    when(pipelineRepository.findById(fromListSelectionId))
        .thenReturn(Optional.of(pipeline));

    var result = pipelineService.getPreSelectedPipeline(String.valueOf(fromListSelectionId));
    assertThat(result).containsExactly(
        entry(pipeline.getSelectionId(), pipeline.getSelectionText())
    );
  }

  @Test
  public void getPipelineAsList_whenPipelineIsFromListEntry_thenPopulatedListReturned() {

    final Integer fromListSelectionId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline(fromListSelectionId, "A pipeline");

    when(pipelineRepository.findById(fromListSelectionId))
        .thenReturn(Optional.of(pipeline));

    var result = pipelineService.getPipelineAsList(String.valueOf(fromListSelectionId));
    assertThat(result).containsExactly(pipeline);
  }

  @Test
  public void getPipelineAsList_whenPipelineIsFromListButNotFound_thenEmptyList() {

    final Integer fromListSelectionId = 1234;

    when(pipelineRepository.findById(fromListSelectionId))
        .thenReturn(Optional.empty());

    var result = pipelineService.getPipelineAsList(String.valueOf(fromListSelectionId));
    assertThat(result).isEmpty();
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getPipelineByIdOrError_whenNotFound_thenException() {

    final Integer pipelineId = 1234;

    when(pipelineRepository.findById(pipelineId))
        .thenReturn(Optional.empty());

    pipelineService.getPipelineByIdOrError(pipelineId);
  }

  @Test
  public void getPipelineByIdOrError_whenFound_thenPipelineReturned() {

    final Integer pipelineId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline();

    when(pipelineRepository.findById(pipelineId))
        .thenReturn(Optional.of(pipeline));

    var result = pipelineService.getPipelineByIdOrError(pipelineId);
    assertThat(result).isEqualTo(pipeline);
  }

  @Test
  public void findById_whenFound_thenEntityReturned() {

    final Integer pipelineId = 1234;
    final Pipeline pipeline = PipelineTestUtil.getPipeline();

    when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));

    var result = pipelineService.findById(pipelineId);
    assertThat(result).contains(pipeline);
  }

  @Test
  public void findById_whenNotFound_thenEmptyOptionalReturned() {

    final Integer pipelineId = 1234;

    when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

    var result = pipelineService.findById(pipelineId);
    assertThat(result).isNotPresent();
  }

  @Test
  public void isPipelineSelectable_whenPipelineDoesNotExist_thenReturnFalse() {

    int pipelineId = 100;

    when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

    var isPipelineSelectable = pipelineService.isPipelineSelectable(pipelineId);

    assertThat(isPipelineSelectable).isFalse();
  }

  @Test
  public void isPipelineSelectable_whenPipelineExistsAndHasHistoricalStatus_thenReturnFalse() {

    var pipeline = PipelineTestUtil.getPipeline();
    pipeline.setIsHistoricStatus(true);

    when(pipelineRepository.findById(pipeline.getId())).thenReturn(Optional.of(pipeline));

    var isPipelineSelectable = pipelineService.isPipelineSelectable(pipeline.getId());

    assertThat(isPipelineSelectable).isFalse();
  }

  @Test
  public void isPipelineSelectable_whenPipelineExistsAndHasNoHistoricalStatus_thenReturnTrue() {

    var pipeline = PipelineTestUtil.getPipeline();
    pipeline.setIsHistoricStatus(false);

    when(pipelineRepository.findById(pipeline.getId())).thenReturn(Optional.of(pipeline));

    var isPipelineSelectable = pipelineService.isPipelineSelectable(pipeline.getId());

    assertThat(isPipelineSelectable).isTrue();
  }
}
