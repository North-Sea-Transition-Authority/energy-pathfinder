package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;

@RestController
@RequestMapping("/api/tender")
public class TenderFunctionRestController {

  private final UpcomingTenderService upcomingTenderService;

  @Autowired
  public TenderFunctionRestController(UpcomingTenderService upcomingTenderService) {
    this.upcomingTenderService = upcomingTenderService;
  }


  @GetMapping("/function")
  public RestSearchResult searchTenderFunctions(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(upcomingTenderService.findTenderFunctionsLikeWithManualEntry(searchTerm));
  }
}
