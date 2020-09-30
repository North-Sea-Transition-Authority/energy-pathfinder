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
        new SearchItem(1, "fieldname")
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
    var searchItem = new SearchItem(1, "fieldname");
    var searchableResults = List.of(searchItem);
    var result = searchSelectorService.search("fie", searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(String.valueOf(searchItem.getId()));
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

  private static class SearchItem implements SearchSelectable {
    private final Integer id;
    private final String name;

    public SearchItem(Integer id,
                      String name) {
      this.id = id;
      this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }

    @Override
    public String getSelectionId() {
      return id.toString();
    }

    @Override
    public String getSelectionText() {
      return name;
    }
  }
}
