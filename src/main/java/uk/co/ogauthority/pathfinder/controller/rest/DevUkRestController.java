package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFacilitiesService;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;

@RestController
@RequestMapping("/api/devuk")
public class DevUkRestController {

  private final DevUkFieldService devUkFieldService;
  private final DevUkFacilitiesService devUkFacilitiesService;

  @Autowired
  public DevUkRestController(DevUkFieldService devUkFieldService,
                             DevUkFacilitiesService devUkFacilitiesService) {
    this.devUkFieldService = devUkFieldService;
    this.devUkFacilitiesService = devUkFacilitiesService;
  }

  @GetMapping("/fields")
  @ResponseBody
  public RestSearchResult searchFields(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(devUkFieldService.findActiveByFieldNameWithManualEntry(searchTerm));
  }

  @GetMapping("/facilities")
  @ResponseBody
  public RestSearchResult searchFacilitiesWithManualEntry(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(devUkFacilitiesService.searchFacilitiesWithNameContainingWithManualEntry(
        searchTerm
      )
    );
  }

}
