package uk.co.ogauthority.pathfinder.service.project;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class FunctionService {

  private final SearchSelectorService searchSelectorService;

  @Autowired
  public FunctionService(SearchSelectorService searchSelectorService) {
    this.searchSelectorService = searchSelectorService;
  }

  /**
   * Search the Function enum displayNames for those that include searchTerm.
   * @param searchTerm Term to match against Function display names
   * @param functionType Filter the options to only return ones which are applicable to this functionType
   * @return return matching results plus manual entry
   */
  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm, FunctionType functionType) {

    // Only search over the functions that are relevant
    var functionsToSearchOver = Arrays.stream(Function.values().clone())
        .filter(function ->  function.getFunctionTypes().contains(functionType))
        .collect(Collectors.toList());

    List<RestSearchItem> results = searchSelectorService.search(
        searchTerm,
        functionsToSearchOver
    )
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());

    searchSelectorService.addManualEntry(searchTerm, results);

    return results;
  }
}
