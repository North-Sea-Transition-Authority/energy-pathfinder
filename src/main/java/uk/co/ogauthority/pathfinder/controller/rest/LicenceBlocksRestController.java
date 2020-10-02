package uk.co.ogauthority.pathfinder.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;

@RestController
@RequestMapping("/api/licence-blocks")
public class LicenceBlocksRestController {

  private final LicenceBlocksService licenceBlocksService;

  @Autowired
  public LicenceBlocksRestController(LicenceBlocksService licenceBlocksService) {
    this.licenceBlocksService = licenceBlocksService;
  }

  @GetMapping
  public RestSearchResult searchLicenceBlocks(@RequestParam("term") String searchTerm) {
    return new RestSearchResult(licenceBlocksService.searchLicenceBlocksWithReferenceContaining(searchTerm));
  }
}
