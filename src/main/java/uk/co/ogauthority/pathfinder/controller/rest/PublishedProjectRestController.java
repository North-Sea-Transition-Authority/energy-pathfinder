package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.PublishedProjectAccessorService;

@RestController
@RequestMapping("/api/published-projects")
public class PublishedProjectRestController {

  private final PublishedProjectAccessorService publishedProjectAccessorService;

  @Autowired
  public PublishedProjectRestController(PublishedProjectAccessorService publishedProjectAccessorService) {
    this.publishedProjectAccessorService = publishedProjectAccessorService;
  }

  @GetMapping("/infrastructure")
  @ResponseBody
  public RestSearchResult searchPublishedInfrastructureProjects(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(publishedProjectAccessorService.searchProjectsWithDisplayNameContaining(
        searchTerm,
        ProjectType.INFRASTRUCTURE
    ));
  }
}
