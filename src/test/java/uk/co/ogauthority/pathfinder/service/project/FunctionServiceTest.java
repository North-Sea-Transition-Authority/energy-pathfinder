package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@RunWith(MockitoJUnitRunner.class)
public class FunctionServiceTest {

  private FunctionService functionService;

  @Before
  public void setup() {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    functionService = new FunctionService(searchSelectorService);
  }

  @Test
  public void findFunctionsLikeWithManualEntry_whenNotManualEntry() {
    var function = Function.DRILLING;
    var results = functionService.findFunctionsLikeWithManualEntry(
        function.getDisplayName(),
        FunctionType.AWARDED_CONTRACT
    );
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(function.getSelectionId());
  }

  @Test
  public void findFunctionsLikeWithManualEntry_whenManualEntry() {
    var function = "manual entry";
    var results = functionService.findFunctionsLikeWithManualEntry(
        function,
        FunctionType.AWARDED_CONTRACT
    );
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX + function);
  }

}