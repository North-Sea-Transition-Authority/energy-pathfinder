package uk.co.ogauthority.pathfinder.service.pipeline;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.rest.PipelinesRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.pipeline.Pipeline;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.pipeline.PipelineRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class PipelinesService {

  private final PipelineRepository pipelineRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public PipelinesService(PipelineRepository pipelineRepository,
                          SearchSelectorService searchSelectorService) {
    this.pipelineRepository = pipelineRepository;
    this.searchSelectorService = searchSelectorService;
  }

  public List<RestSearchItem> searchPipelinesWithNameContaining(String searchTerm) {
    var searchableList = findByNameContaining(searchTerm);
    return searchSelectorService.search(searchTerm, searchableList);
  }

  public String getPipelinesRestUrl() {
    return SearchSelectorService.route(
        on(PipelinesRestController.class).searchPipelines(null)
    );
  }

  public Map<String, String> getPreSelectedPipeline(String pipelineFromForm) {
    return (pipelineFromForm != null)
        ? searchSelectorService.getPreSelectedSearchSelectorValue(
            pipelineFromForm,
            getPipelineAsList(pipelineFromForm)
        )
        : Map.of();
  }

  public List<Pipeline> getPipelineAsList(String pipelineFromForm) {
    var pipelines = new ArrayList<Pipeline>();

    if (pipelineFromForm != null) {
      var pipelineId = Integer.parseInt(pipelineFromForm);
      findById(pipelineId).ifPresent(pipelines::add);
    }

    return pipelines;
  }

  public Pipeline getPipelineByIdOrError(Integer pipelineId) {
    return findById(pipelineId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format("Pipeline with id %d was not found.", pipelineId)));
  }

  public Optional<Pipeline> findById(Integer pipelineId) {
    return pipelineRepository.findById(pipelineId);
  }

  private List<Pipeline> findByNameContaining(String searchTerm) {
    return pipelineRepository.findAllByNameContainingIgnoreCase(searchTerm);
  }
}
