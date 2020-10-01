package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.project.decommissionedwell.DecommissionedWellService;

@RestController
@RequestMapping("/api/decommissioned-wells")
public class DecommissionedWellRestController {

  private final DecommissionedWellService decommissionedWellService;

  public DecommissionedWellRestController(DecommissionedWellService decommissionedWellService) {
    this.decommissionedWellService = decommissionedWellService;
  }

  @GetMapping("/types")
  public RestSearchResult searchTypes(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(decommissionedWellService.findTypesLikeWithManualEntry(searchTerm));
  }

  @GetMapping("/operational-statuses")
  public RestSearchResult searchOperationalStatuses(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(decommissionedWellService.findOperationalStatusesLikeWithManualEntry(searchTerm));
  }

  @GetMapping("/mechanical-statuses")
  public RestSearchResult searchMechanicalStatuses(@Nullable @RequestParam("term") String searchTerm) {
    return new RestSearchResult(decommissionedWellService.findMechanicalStatusesLikeWithManualEntry(searchTerm));
  }
}
