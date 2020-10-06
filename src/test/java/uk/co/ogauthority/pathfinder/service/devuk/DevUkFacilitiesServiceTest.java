package uk.co.ogauthority.pathfinder.service.devuk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFacilitiesRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class DevUkFacilitiesServiceTest {

  @Mock
  private DevUkFacilitiesRepository devUkFacilitiesRepository;

  private DevUkFacilitiesService devUkFacilitiesService;

  @Before
  public void setUp() throws Exception {
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    devUkFacilitiesService = new DevUkFacilitiesService(
        devUkFacilitiesRepository,
        searchSelectorService
    );
  }

  @Test
  public void searchFacilitiesWithNameContainingWithManualEntry_entryAdded() {
    var searchTerm = "fac";
    var facility = DevUkTestUtil.getDevUkFacility();
    when(devUkFacilitiesRepository.findAllByFacilityNameContainingIgnoreCase(searchTerm)).thenReturn(
        Collections.singletonList(facility)
    );
    var results = devUkFacilitiesService.searchFacilitiesWithNameContainingWithManualEntry(searchTerm);
    assertThat(results.size()).isEqualTo(2);
    assertThat(results.get(0).getText()).isEqualToIgnoringCase(facility.getFacilityName());
    assertThat(results.get(1).getText()).isEqualToIgnoringCase(searchTerm);
  }

  @Test
  public void searchFacilitiesWithNameContainingWithManualEntry_entryAdded_whenNoResults() {
    var searchTerm = "fac";
    when(devUkFacilitiesRepository.findAllByFacilityNameContainingIgnoreCase(searchTerm)).thenReturn(
        Collections.emptyList()
    );
    var results = devUkFacilitiesService.searchFacilitiesWithNameContainingWithManualEntry(searchTerm);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getText()).isEqualToIgnoringCase(searchTerm);
  }

}
