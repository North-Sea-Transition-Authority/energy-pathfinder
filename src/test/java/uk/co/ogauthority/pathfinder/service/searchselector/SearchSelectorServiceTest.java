package uk.co.ogauthority.pathfinder.service.searchselector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.searchselector.ManualEntryAttribute;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectable;

public class SearchSelectorServiceTest {
  private SearchSelectorService searchSelectorService;

  @Before
  public void setUp() {
    searchSelectorService = new SearchSelectorService();
  }

  @Test
  public void search_NoMatch() {
    var searchableResults = List.of(
        new DevUkField(1, "fieldname", 600)
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
    var devUkField = new DevUkField(1, "fieldname", 600);
    var searchableResults = List.of(devUkField);
    var result = searchSelectorService.search("fie", searchableResults);
    assertThat(result).extracting(RestSearchItem::getId)
        .containsExactly(String.valueOf(devUkField.getFieldId()));
  }


  @Test
  public void addManualEntry() {
    var searchableResults = searchSelectorService.addManualEntry("free_text", new ArrayList<>());
    assertThat(searchableResults).extracting(RestSearchItem::getId)
        .containsExactly(SearchSelectable.FREE_TEXT_PREFIX + "free_text");
  }

  @Test
  public void addManualEntry_noFreeText() {
    var searchableResults = searchSelectorService.addManualEntry("free_text", new ArrayList<>(), ManualEntryAttribute.NO_FREE_TEXT_PREFIX);
    assertThat(searchableResults).extracting(RestSearchItem::getId)
        .containsExactly("free_text");
  }

  @Test
  public void buildPrepopulatedSelections() {
    var prefix = SearchSelectable.FREE_TEXT_PREFIX;
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
    var str = SearchSelectable.FREE_TEXT_PREFIX + "Test";
    assertThat(searchSelectorService.removePrefix(str)).isEqualTo("Test");
  }

  @Test
  public void route() {
    var routeOn = on(DevUkRestController.class).searchFields(null);
    var route = SearchSelectorService.route(routeOn);
    assertThat(route).doesNotEndWith("term");
  }

  @Test
  public void isManualEntry() {
    var manualEntry = SearchSelectable.FREE_TEXT_PREFIX + "manual_Entry";
    var notManualEntry = "123";
    assertThat(SearchSelectorService.isManualEntry(manualEntry)).isTrue();
    assertThat(SearchSelectorService.isManualEntry(notManualEntry)).isFalse();
  }
}
