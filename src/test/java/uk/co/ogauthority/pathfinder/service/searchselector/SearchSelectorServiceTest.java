package uk.co.ogauthority.pathfinder.service.searchselector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.searchselector.ManualEntryAttribute;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;

@RunWith(MockitoJUnitRunner.class)
public class SearchSelectorServiceTest {

  private SearchSelectorService searchSelectorService;

  @Before
  public void setUp() {
    searchSelectorService = new SearchSelectorService();
  }

  @Test
  public void search_NoMatch() {
    var searchableResults = List.of(
        new SearchSelectableTestItem(1, "fieldname")
    );
    var result = searchSelectorService.search("should not match", searchableResults);
    assertThat(result).isEmpty();
  }

  @Test
  public void search_SearchableEmpty() {
    List<SearchSelectable> searchableResults = List.of();
    var result = searchSelectorService.search("should not match", searchableResults);
    assertThat(result).isEmpty();
  }
  @Test
  public void search_Match() {
    var searchItem = new SearchSelectableTestItem(1, "fieldname");
    var searchableResults = List.of(searchItem);
    var result = searchSelectorService.search("fie", searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(String.valueOf(searchItem.getId()));
  }

  @Test
  public void search_resultsAreOrdered() {

    var searchItem1 = new SearchSelectableTestItem(1, "value2");
    var searchItem2 = new SearchSelectableTestItem(2, "value1");
    var searchableResults = List.of(searchItem1, searchItem2);

    final String searchTerm = "value";
    var result = searchSelectorService.search(searchTerm, searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(
            String.valueOf(searchItem2.getId()),
            String.valueOf(searchItem1.getId()));
  }

  @Test
  public void search_whenSortIsFalse_resultsAreNotOrdered() {

    var searchItem1 = new SearchSelectableTestItem(1, "value2");
    var searchItem2 = new SearchSelectableTestItem(2, "value1");
    var searchableResults = List.of(searchItem1, searchItem2);

    final String searchTerm = "value";
    var result = searchSelectorService.search(searchTerm, searchableResults, false);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(
            String.valueOf(searchItem1.getId()),
            String.valueOf(searchItem2.getId()));
  }

  @Test
  public void addManualEntry() {
    var searchableResults = searchSelectorService.addManualEntry("free_text", new ArrayList<>());
    assertThat(searchableResults).extracting(RestSearchItem::getId)
        .containsExactly(SearchSelectablePrefix.FREE_TEXT_PREFIX + "free_text");
  }

  @Test
  public void addManualEntry_noFreeText() {
    var searchableResults = searchSelectorService.addManualEntry("free_text", new ArrayList<>(), ManualEntryAttribute.NO_FREE_TEXT_PREFIX);
    assertThat(searchableResults).extracting(RestSearchItem::getId)
        .containsExactly("free_text");
  }

  @Test
  public void buildPrepopulatedSelections() {
    var prefix = SearchSelectablePrefix.FREE_TEXT_PREFIX;
    var selections = List.of(prefix + "Test", "1", "2");
    var resolvedMap = new HashMap<String, String>(){{
      put("1", "One");
      put("2", "Two");
    }};
    var result = searchSelectorService.buildPrePopulatedSelections(selections, resolvedMap);
    assertThat(result).containsExactly(
        entry(prefix + "Test", "Test"),
        entry("1", "One"),
        entry("2", "Two")
    );
  }

  @Test
  public void removePrefix() {
    var str = SearchSelectablePrefix.FREE_TEXT_PREFIX + "Test";
    assertThat(SearchSelectorService.removePrefix(str)).isEqualTo("Test");
  }

  @Test
  public void route() {
    var routeOn = on(DevUkRestController.class).searchFields(null);
    var route = SearchSelectorService.route(routeOn);
    assertThat(route).doesNotEndWith("term");
  }

  @Test
  public void isManualEntry() {
    var manualEntry = SearchSelectablePrefix.FREE_TEXT_PREFIX + "manual_Entry";
    var notManualEntry = "123";
    assertThat(SearchSelectorService.isManualEntry(manualEntry)).isTrue();
    assertThat(SearchSelectorService.isManualEntry(notManualEntry)).isFalse();
  }

  @Test
  public void getValueWithManualEntryPrefix() {
    var manualEntry = "manual entry";
    var result = SearchSelectorService.getValueWithManualEntryPrefix(manualEntry);
    assertThat(result).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX + manualEntry);
  }

  @Test
  public void searchWithManualEntry_whenFromList() {

    var searchItem = new SearchSelectableTestItem(1, "value1");
    var searchableResults = List.of(searchItem);

    var result = searchSelectorService.searchWithManualEntry("value1", searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(String.valueOf(searchItem.getId()));
  }

  @Test
  public void searchWithManualEntry_whenManualEntry() {

    var searchItem = new SearchSelectableTestItem(1, "value1");
    var searchableResults = List.of(searchItem);

    final String searchTerm = "value2";
    var result = searchSelectorService.searchWithManualEntry(searchTerm, searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(SearchSelectablePrefix.FREE_TEXT_PREFIX + searchTerm);
  }

  @Test
  public void searchWithManualEntry_resultsAreOrdered() {

    var searchItem1 = new SearchSelectableTestItem(1, "value2");
    var searchItem2 = new SearchSelectableTestItem(2, "value1");
    var searchableResults = List.of(searchItem1, searchItem2);

    final String searchTerm = "value";
    var result = searchSelectorService.searchWithManualEntry(searchTerm, searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(
            SearchSelectablePrefix.FREE_TEXT_PREFIX + searchTerm,
            String.valueOf(searchItem2.getId()),
            String.valueOf(searchItem1.getId()));
  }

  @Test
  public void searchWithManualEntry_whenSortIsFalse_resultsAreNotOrdered() {

    var searchItem1 = new SearchSelectableTestItem(1, "value2");
    var searchItem2 = new SearchSelectableTestItem(2, "value1");
    var searchableResults = List.of(searchItem1, searchItem2);

    final String searchTerm = "value";
    var result = searchSelectorService.searchWithManualEntry(searchTerm, searchableResults, false);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(
            SearchSelectablePrefix.FREE_TEXT_PREFIX + searchTerm,
            String.valueOf(searchItem1.getId()),
            String.valueOf(searchItem2.getId()));
  }

  @Test
  public void getManualOrStandardSelection_whenBothNull_thenNull() {
    var result = searchSelectorService.getManualOrStandardSelection(null, null);
    assertThat(result).isNull();
  }

  @Test
  public void getManualOrStandardSelection_whenManualEntry_thenValueWithPrefix() {
    var manualSelection = "manual entry";
    var result = searchSelectorService.getManualOrStandardSelection(manualSelection, null);
    assertThat(result).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX + manualSelection);
  }

  @Test
  public void getManualOrStandardSelection_whenFromList_thenValueWithPrefix() {
    var searchItem = new SearchSelectableTestItem(1, "value");
    var result = searchSelectorService.getManualOrStandardSelection(null, searchItem);
    assertThat(result).isEqualTo(searchItem.getSelectionId());
  }

  @Test
  public void mapSearchSelectorFormEntryToEntity_whenFromEnumList_thenSetFromListField() {

    final SearchSelectableTestEnum selectedValue = SearchSelectableTestEnum.VALUE_1;
    var entity = new SearchSelectableTestEntityWithEnum();

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        selectedValue.getSelectionId(),
        SearchSelectableTestEnum.values(),
        entity::setManualEntry,
        entity::setFromListValue
    );
    assertThat(entity.getFromListValue()).isEqualTo(selectedValue);
    assertThat(entity.getManualEntry()).isNull();
  }

  @Test
  public void mapSearchSelectorFormEntryToEntity_whenFromEntityList_thenSetFromListField() {

    final SearchSelectableTestItem selectedValue = new SearchSelectableTestItem(1, "test");
    var entity = new SearchSelectableTestEntityWithEntity();

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        selectedValue.getSelectionId(),
        List.of(selectedValue),
        entity::setManualEntryItem,
        entity::setSearchSelectableTestItem
    );
    assertThat(entity.getSearchSelectableTestItem()).isEqualTo(selectedValue);
    assertThat(entity.getManualEntryItem()).isNull();
  }

  @Test
  public void mapSearchSelectorFormEntryToEntity_whenEnumListManualEntry_thenSetManualEntryField() {

    final String selectedValue = "manual entry";
    var entity = new SearchSelectableTestEntityWithEnum();

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        SearchSelectablePrefix.FREE_TEXT_PREFIX + selectedValue,
        SearchSelectableTestEnum.values(),
        entity::setManualEntry,
        entity::setFromListValue
    );
    assertThat(entity.getFromListValue()).isNull();
    assertThat(entity.getManualEntry()).isEqualTo(selectedValue);
  }

  @Test
  public void mapSearchSelectorFormEntryToEntity_whenEntityListManualEntry_thenSetManualEntryField() {

    final String selectedValue = "manual entry";
    var entity = new SearchSelectableTestEntityWithEntity();

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        SearchSelectablePrefix.FREE_TEXT_PREFIX + selectedValue,
        List.of(),
        entity::setManualEntryItem,
        entity::setSearchSelectableTestItem
    );
    assertThat(entity.getSearchSelectableTestItem()).isNull();
    assertThat(entity.getManualEntryItem()).isEqualTo(selectedValue);
  }

  @Test
  public void mapSearchSelectorFormEntryToEntity_whenEntityListAndNull_thenSetNeither() {

    var entity = new SearchSelectableTestEntityWithEnum();

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        null,
        SearchSelectableTestEnum.values(),
        entity::setManualEntry,
        entity::setFromListValue
    );
    assertThat(entity.getFromListValue()).isNull();
    assertThat(entity.getManualEntry()).isNull();
  }

  @Test
  public void mapSearchSelectorFormEntryToEntity_whenEnumListAndNull_thenSetNeither() {

    var entity = new SearchSelectableTestEntityWithEntity();

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        null,
        List.of(),
        entity::setManualEntryItem,
        entity::setSearchSelectableTestItem
    );
    assertThat(entity.getSearchSelectableTestItem()).isNull();
    assertThat(entity.getManualEntryItem()).isNull();
  }

  @Test
  public void getPreSelectedSearchSelectorValue_whenEnumValueAndValueIsNull() {
    var result = searchSelectorService.getPreSelectedSearchSelectorValue(
        null,
        SearchSelectableTestEnum.values()
    );
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedSearchSelectorValue_whenListValueAndValueIsNull() {
    var result = searchSelectorService.getPreSelectedSearchSelectorValue(
        null,
        List.of()
    );
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedSearchSelectorValue_whenEnumListAndValueIsManualEntry() {
    final String entry = "manual entry";
    final String formValue = SearchSelectablePrefix.FREE_TEXT_PREFIX + entry;

    var result = searchSelectorService.getPreSelectedSearchSelectorValue(
        formValue,
        SearchSelectableTestEnum.values()
    );
    assertThat(result).containsExactly(
        entry(formValue, entry)
    );
  }

  @Test
  public void getPreSelectedSearchSelectorValue_whenEntityListAndValueIsManualEntry() {
    final String entry = "manual entry";
    final String formValue = SearchSelectablePrefix.FREE_TEXT_PREFIX + entry;

    var result = searchSelectorService.getPreSelectedSearchSelectorValue(
        formValue,
        List.of()
    );
    assertThat(result).containsExactly(
        entry(formValue, entry)
    );
  }

  @Test
  public void getPreSelectedSearchSelectorValue_whenEnumListAndValueIsFromList() {
    final SearchSelectableTestEnum entry = SearchSelectableTestEnum.VALUE_1;
    final String formValue = entry.getSelectionId();

    var result = searchSelectorService.getPreSelectedSearchSelectorValue(
        formValue,
        SearchSelectableTestEnum.values()
    );
    assertThat(result).containsExactly(
        entry(formValue, entry.getSelectionText())
    );
  }

  @Test
  public void getPreSelectedSearchSelectorValue_whenEntityListAndValueIsFromList() {
    final SearchSelectableTestItem entry = new SearchSelectableTestItem(1, "test");
    final String formValue = entry.getSelectionId();

    var result = searchSelectorService.getPreSelectedSearchSelectorValue(
        formValue,
        List.of(entry)
    );
    assertThat(result).containsExactly(
        entry(formValue, entry.getSelectionText())
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void getPreSelectedSearchSelectorValue_whenEnumListAndValueIsNotManualAndNotInList() {

    final String formValue = "NOT MANUAL AND NOT IN LIST";

    searchSelectorService.getPreSelectedSearchSelectorValue(
        formValue,
        SearchSelectableTestEnum.values()
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void getPreSelectedSearchSelectorValue_whenEntityListAndValueIsNotManualAndNotInList() {

    final String formValue = "NOT MANUAL AND NOT IN LIST";

    searchSelectorService.getPreSelectedSearchSelectorValue(
        formValue,
        List.of()
    );
  }
}
