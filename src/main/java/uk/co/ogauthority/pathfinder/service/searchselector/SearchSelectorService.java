package uk.co.ogauthority.pathfinder.service.searchselector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.searchselector.ManualEntryAttribute;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

/**
 * A generic service to provide a list of RestSearchItems for any entities implementing SearchSelectable.
 * An optional {@link #addManualEntry} method is provided if the endpoint requires manually entered text.
 */
@Service
public class SearchSelectorService {

  protected static final String SEARCH_TERM_PARAM_NAME = "term";

  public List<RestSearchItem> search(String searchQuery, Collection<? extends SearchSelectable> selectableList) {
    return search(searchQuery, selectableList, true);
  }

  public List<RestSearchItem> search(String searchQuery,
                                     Collection<? extends SearchSelectable> selectableList,
                                     boolean sortResults) {
    var restSearchResults = selectableList.stream()
        .filter(searchSelectable ->
            searchSelectable.getSelectionText()
                .toLowerCase()
                .contains(StringUtils.defaultIfBlank(searchQuery, "").toLowerCase()))
        .map(item -> new RestSearchItem(item.getSelectionId(), item.getSelectionText()))
        .collect(Collectors.toList());

    if (sortResults) {
      restSearchResults.sort(Comparator.comparing(RestSearchItem::getText));
    }

    return restSearchResults;
  }

  public List<RestSearchItem> searchWithManualEntry(String searchQuery,
                                                    Collection<? extends SearchSelectable> selectableList) {
    return searchWithManualEntry(searchQuery, selectableList, true);
  }

  public List<RestSearchItem> searchWithManualEntry(String searchQuery,
                                                    Collection<? extends SearchSelectable> selectableList,
                                                    boolean sortResults) {
    var restSearchResults = search(searchQuery, selectableList, sortResults);
    addManualEntry(searchQuery, restSearchResults);

    if (sortResults) {
      restSearchResults.sort(Comparator.comparing(RestSearchItem::getText));
    }

    return restSearchResults;
  }

  public List<RestSearchItem> addManualEntry(String searchQuery, List<RestSearchItem> resultList) {
    return addManualEntry(searchQuery, resultList, ManualEntryAttribute.WITH_FREE_TEXT_PREFIX);
  }

  public List<RestSearchItem> addManualEntry(String searchQuery,
                                             List<RestSearchItem> resultList,
                                             ManualEntryAttribute manualEntryAttribute) {
    if (!StringUtils.isBlank(searchQuery)) {
      var entryExists = resultList.stream()
          .anyMatch(restSearchItem -> restSearchItem.getText().equalsIgnoreCase(searchQuery));
      if (!entryExists) {
        if (manualEntryAttribute.equals(ManualEntryAttribute.WITH_FREE_TEXT_PREFIX)) {
          resultList.add(0, new RestSearchItem(SearchSelectablePrefix.FREE_TEXT_PREFIX + searchQuery, searchQuery));
        } else {
          resultList.add(0, new RestSearchItem(searchQuery, searchQuery));
        }
      }
    }
    return resultList;
  }

  public static String route(Object methodCall) {
    return StringUtils.removeEnd(ReverseRouter.route(methodCall), String.format("?%s", SEARCH_TERM_PARAM_NAME));
  }

  /**
   * Build a map of manual entries and linked entries, with the linked entry display text.
   *
   * @param selections             All selected items from a form field.
   * @param resolvedLinkedEntryMap A map of ID (String) -> DisplayText (String).
   * @return A map of selection results to pre-populate the search selector.
   */
  public Map<String, String> buildPrePopulatedSelections(List<String> selections,
                                                         Map<String, String> resolvedLinkedEntryMap) {
    var results = new LinkedHashMap<String, String>();
    for (String s : selections) {
      if (s.startsWith(SearchSelectablePrefix.FREE_TEXT_PREFIX)) {
        results.put(s, removePrefix(s));
      } else {
        results.put(s, resolvedLinkedEntryMap.get(s));
      }
    }
    return results;
  }

  public static String removePrefix(String s) {
    return StringUtils.substring(s, SearchSelectablePrefix.FREE_TEXT_PREFIX.length());
  }

  public static boolean isManualEntry(String s) {
    return s != null && s.startsWith(SearchSelectablePrefix.FREE_TEXT_PREFIX);
  }

  public static String getValueWithManualEntryPrefix(String value) {
    return SearchSelectablePrefix.FREE_TEXT_PREFIX  + value;
  }

  public String getManualOrStandardSelection(String manualSelection, SearchSelectable standardSelection) {
    String output = null;
    if (manualSelection != null) {
      output = getValueWithManualEntryPrefix(manualSelection);
    } else if (standardSelection != null) {
      output = standardSelection.getSelectionId();
    }
    return output;
  }

  /**
   * Utility method to map a search selector form field to either the manual entry of from list
   * fields in an entity.
   * @param formValue The value from the form search selector
   * @param listOptions The list options the user could have selected from
   * @param entityManualEntryField the manual entry field setter on the entity to call if manual entry value
   * @param entityFromListField the from list field setter on the entity to call if from list value
   * @param <T> A class which implements search selectable
   */
  public <T extends SearchSelectable> void mapSearchSelectorFormEntryToEntity(String formValue,
                                                                              List<T> listOptions,
                                                                              Consumer<String> entityManualEntryField,
                                                                              Consumer<T> entityFromListField) {
    // if we have a manual entry value
    if (SearchSelectorService.isManualEntry(formValue)) {
      entityManualEntryField.accept(SearchSelectorService.removePrefix(formValue));
      entityFromListField.accept(null);
    } else if (formValue != null) {
      // if we have a non null entry from the list
      entityFromListField.accept(getListValue(formValue, listOptions));
      entityManualEntryField.accept(null);
    } else {
      // we have no value at all
      entityFromListField.accept(null);
      entityManualEntryField.accept(null);
    }
  }

  public <T extends SearchSelectable> void mapSearchSelectorFormEntryToEntity(String formValue,
                                                                              T[] listOptions,
                                                                              Consumer<String> entityManualEntryField,
                                                                              Consumer<T> entityFromListField) {
    mapSearchSelectorFormEntryToEntity(
        formValue,
        Arrays.asList(listOptions),
        entityManualEntryField,
        entityFromListField
    );
  }

  /**
   * Method to return a pre-selected search selector value (manual entry or from list).
   * @param searchSelectorValue the value of the search selector form field
   * @param listOptions the from list options that could have been selected
   * @param <T> A class which implements search selectable
   * @return A map of the pre-selected search selector value (either manual entry of from list)
   */
  public <T extends SearchSelectable> Map<String, String> getPreSelectedSearchSelectorValue(String searchSelectorValue,
                                                                                            List<T> listOptions) {

    Map<String, String> preSelectedMap = Map.of();

    if (searchSelectorValue != null) {
      if (SearchSelectorService.isManualEntry(searchSelectorValue)) {
        preSelectedMap = buildPrePopulatedSelections(
            Collections.singletonList(searchSelectorValue),
            Map.of(searchSelectorValue, searchSelectorValue)
        );
      } else {
        preSelectedMap = buildPrePopulatedSelections(
            Collections.singletonList(searchSelectorValue),
            Map.of(searchSelectorValue, getListValue(searchSelectorValue, listOptions).getSelectionText())
        );
      }
    }

    return preSelectedMap;
  }

  public <T extends SearchSelectable> Map<String, String> getPreSelectedSearchSelectorValue(String searchSelectorValue,
                                                                                            T[] listOptions) {
    return getPreSelectedSearchSelectorValue(searchSelectorValue, Arrays.asList(listOptions));
  }

  private <T extends SearchSelectable> T getListValue(String searchSelectorValue, List<T> listOptions) {
    return listOptions
        .stream()
        .filter(value -> value.getSelectionId().equals(searchSelectorValue))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(String.format(
            "Could not find matching list value for form value %s", searchSelectorValue
        )));
  }

}
