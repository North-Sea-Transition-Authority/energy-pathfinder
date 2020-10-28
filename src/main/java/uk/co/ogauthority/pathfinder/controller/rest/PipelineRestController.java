package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.pipeline.PipelineService;

@RestController
@RequestMapping("/api/pipelines")
public class PipelineRestController {

  private final PipelineService pipelineService;

  @Autowired
  public PipelineRestController(PipelineService pipelineService) {
    this.pipelineService = pipelineService;
  }

  @GetMapping
  @ResponseBody
  public RestSearchResult searchPipelines(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(pipelineService.searchPipelinesWithNameContaining(searchTerm));
  }
}
