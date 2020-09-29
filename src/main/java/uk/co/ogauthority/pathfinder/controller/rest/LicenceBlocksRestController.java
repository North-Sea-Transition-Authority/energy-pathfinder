package uk.co.ogauthority.pathfinder.controller.rest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.portal.LicenceBlocksService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api/licence-blocks")
public class LicenceBlocksRestController {

  private final LicenceBlocksService licenceBlocksService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public LicenceBlocksRestController(LicenceBlocksService licenceBlocksService,
                                     SearchSelectorService searchSelectorService) {
    this.licenceBlocksService = licenceBlocksService;
    this.searchSelectorService = searchSelectorService;
  }

  @GetMapping
  public RestSearchResult searchLicenceBlocks(@RequestParam("term") String searchTerm) {
    var searchableList = licenceBlocksService.findCurrentByReference(searchTerm);
    List<RestSearchItem> results = searchSelectorService.search(searchTerm, searchableList)
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());

    return new RestSearchResult(results);
  }
}
