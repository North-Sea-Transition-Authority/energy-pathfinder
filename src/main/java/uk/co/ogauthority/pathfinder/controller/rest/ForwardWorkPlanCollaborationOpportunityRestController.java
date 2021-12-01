package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityService;

@RestController
@RequestMapping("/api/forward-work-plan/collaboration-opportunity")
public class ForwardWorkPlanCollaborationOpportunityRestController {

  private final ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityRestController(
      ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService
  ) {
    this.forwardWorkPlanCollaborationOpportunityService = forwardWorkPlanCollaborationOpportunityService;
  }

  @GetMapping("/function")
  public RestSearchResult searchFunctions(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(forwardWorkPlanCollaborationOpportunityService.findFunctionsLikeWithManualEntry(
        searchTerm
    ));
  }
}
