package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesService;

@RestController
@RequestMapping("/api/infrastructure/collaboration-opportunity")
public class InfrastructureCollaborationOpportunityRestController {

  private final InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;

  @Autowired
  public InfrastructureCollaborationOpportunityRestController(
      InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService) {
    this.infrastructureCollaborationOpportunitiesService = infrastructureCollaborationOpportunitiesService;
  }

  @GetMapping("/function")
  public RestSearchResult searchFunctions(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(infrastructureCollaborationOpportunitiesService.findFunctionsLikeWithManualEntry(searchTerm));
  }
}
