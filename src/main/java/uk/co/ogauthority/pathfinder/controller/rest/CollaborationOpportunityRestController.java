package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.CollaborationOpportunitiesService;

@RestController
@RequestMapping("/api/collaboration-opportunity")
public class CollaborationOpportunityRestController {

  private final CollaborationOpportunitiesService collaborationOpportunitiesService;

  @Autowired
  public CollaborationOpportunityRestController(CollaborationOpportunitiesService collaborationOpportunitiesService) {
    this.collaborationOpportunitiesService = collaborationOpportunitiesService;
  }

  @GetMapping("/function")
  public RestSearchResult searchFunctions(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(collaborationOpportunitiesService.findFunctionsLikeWithManualEntry(searchTerm));
  }
}
