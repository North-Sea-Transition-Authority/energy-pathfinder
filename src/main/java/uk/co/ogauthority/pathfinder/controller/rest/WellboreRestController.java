package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.wellbore.WellboreService;

@RestController
@RequestMapping("/api/wellbores")
public class WellboreRestController {

  private final WellboreService wellboreService;

  @Autowired
  public WellboreRestController(WellboreService wellboreService) {
    this.wellboreService = wellboreService;
  }

  @GetMapping
  @ResponseBody
  public RestSearchResult searchWellbores(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(wellboreService.searchWellboresWithWellRegistrationNoContaining(searchTerm));
  }
}
