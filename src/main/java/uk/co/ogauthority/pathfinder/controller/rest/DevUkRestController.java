package uk.co.ogauthority.pathfinder.controller.rest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchResult;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@RestController
@RequestMapping("/api/devuk")
public class DevUkRestController {

  private final DevUkFieldService devUkFieldService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public DevUkRestController(DevUkFieldService devUkFieldService,
                             SearchSelectorService searchSelectorService) {
    this.devUkFieldService = devUkFieldService;

    this.searchSelectorService = searchSelectorService;
  }

  @GetMapping("/fields")
  @ResponseBody
  public RestSearchResult searchFields(@RequestParam("term") String searchTerm) {
    var searchableList = devUkFieldService.findActiveByFieldName(searchTerm);
    List<RestSearchItem> results = searchSelectorService.search(searchTerm, searchableList)
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());
    searchSelectorService.addManualEntry(searchTerm, results);
    return new RestSearchResult(results);
  }

}
