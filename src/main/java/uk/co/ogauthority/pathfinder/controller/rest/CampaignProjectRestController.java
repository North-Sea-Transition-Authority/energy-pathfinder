package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.campaigninformation.CampaignProjectRestService;

@RestController
@RequestMapping("/api/campaign-projects")
public class CampaignProjectRestController {

  private final CampaignProjectRestService campaignProjectRestService;

  @Autowired
  public CampaignProjectRestController(CampaignProjectRestService campaignProjectRestService) {
    this.campaignProjectRestService = campaignProjectRestService;
  }

  @GetMapping("/infrastructure")
  @ResponseBody
  public RestSearchResult searchCampaignableInfrastructureProjects(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(campaignProjectRestService.searchProjectsWithDisplayNameOrOperatorGroupNameContaining(
        searchTerm,
        ProjectType.INFRASTRUCTURE
    ));
  }
}
