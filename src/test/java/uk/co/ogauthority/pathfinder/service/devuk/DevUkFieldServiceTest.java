package uk.co.ogauthority.pathfinder.service.devuk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFieldRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class DevUkFieldServiceTest {

  @Mock
  private DevUkFieldRepository devUkFieldRepository;

  private DevUkFieldService devUkFieldService;

  @Before
  public void setUp() throws Exception {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    devUkFieldService = new DevUkFieldService(
        devUkFieldRepository,
        searchSelectorService
    );
  }

  @Test
  public void searchFieldsWithNameContainingWithManualEntry_entryAdded() {
    var searchTerm = "field";
    var field = DevUkTestUtil.getDevUkField();
    when(devUkFieldRepository.findAllByStatusInAndFieldNameContainingIgnoreCase(DevUkFieldService.ACTIVE_STATUS_LIST, searchTerm)).thenReturn(
        Collections.singletonList(field)
    );
    var results = devUkFieldService.findActiveByFieldNameWithManualEntry(searchTerm);
    assertThat(results.size()).isEqualTo(2);
    assertThat(results.get(0).getText()).isEqualToIgnoringCase(field.getFieldName());
    assertThat(results.get(1).getText()).isEqualToIgnoringCase(searchTerm);
  }

  @Test
  public void searchFieldsWithNameContainingWithManualEntry_entryAdded_whenNoResults() {
    var searchTerm = "fac";
    when(devUkFieldRepository.findAllByStatusInAndFieldNameContainingIgnoreCase(DevUkFieldService.ACTIVE_STATUS_LIST, searchTerm)).thenReturn(
        Collections.emptyList()
    );
    var results = devUkFieldService.findActiveByFieldNameWithManualEntry(searchTerm);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getText()).isEqualToIgnoringCase(searchTerm);
  }
}
