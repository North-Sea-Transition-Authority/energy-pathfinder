package uk.co.ogauthority.pathfinder.service.devuk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFacilitiesRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class DevUkFacilitiesServiceTest {

  @Mock
  private DevUkFacilitiesRepository devUkFacilitiesRepository;

  private DevUkFacilitiesService devUkFacilitiesService;

  @Before
  public void setUp() {
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

  @Test
  public void getFacilitiesRestUrl() {
    var restUrl = devUkFacilitiesService.getFacilitiesRestUrl();
    assertThat(restUrl).isEqualTo(
        SearchSelectorService.route(on(DevUkRestController.class).searchFacilitiesWithManualEntry(null))
    );
  }

  @Test
  public void getPreSelectedFacility_whenFacilityIsNull_thenEmptyMap() {
    var result = devUkFacilitiesService.getPreSelectedFacility(null);
    assertThat(result).isEmpty();
  }

  @Test
  public void getPreSelectedFacility_whenFacilityIsManualEntry_thenManualEntryResult() {

    final String manualFormValue = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var result = devUkFacilitiesService.getPreSelectedFacility(manualFormValue);
    assertThat(result).containsExactly(
        entry(manualFormValue, SearchSelectorService.removePrefix(manualFormValue))
    );
  }

  @Test
  public void getPreSelectedFacility_whenFacilityIsFromListEntry_thenFromListResult() {

    final Integer fromListSelectionId = 1234;
    final DevUkFacility facility = DevUkTestUtil.getDevUkFacility(fromListSelectionId, "A DevUK facility");

    when(devUkFacilitiesRepository.findById(fromListSelectionId))
        .thenReturn(Optional.of(facility));

    var result = devUkFacilitiesService.getPreSelectedFacility(String.valueOf(fromListSelectionId));
    assertThat(result).containsExactly(
        entry(facility.getSelectionId(), facility.getSelectionText())
    );
  }

  @Test
  public void getFacilityAsList_whenFacilityIsManualEntry_thenEmptyList() {

    final String manualFormValue = SearchSelectablePrefix.FREE_TEXT_PREFIX + "my manual facility";

    var result = devUkFacilitiesService.getFacilityAsList(manualFormValue);
    assertThat(result).isEmpty();
  }

  @Test
  public void getFacilityAsList_whenFacilityIsNull_thenEmptyList() {
    var result = devUkFacilitiesService.getFacilityAsList(null);
    assertThat(result).isEmpty();
  }

  @Test
  public void getFacilityAsList_whenFacilityIsFromListEntry_thenPopulatedListReturned() {

    final Integer fromListSelectionId = 1234;
    final DevUkFacility facility = DevUkTestUtil.getDevUkFacility(fromListSelectionId, "A DevUK facility");

    when(devUkFacilitiesRepository.findById(fromListSelectionId))
        .thenReturn(Optional.of(facility));

    var result = devUkFacilitiesService.getFacilityAsList(String.valueOf(fromListSelectionId));
    assertThat(result).containsExactly(facility);
  }

  @Test
  public void getFacilityAsList_whenFacilityIsFromListButNotFound_thenEmptyList() {

    final Integer fromListSelectionId = 1234;

    when(devUkFacilitiesRepository.findById(fromListSelectionId))
        .thenReturn(Optional.empty());

    var result = devUkFacilitiesService.getFacilityAsList(String.valueOf(fromListSelectionId));
    assertThat(result).isEmpty();
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getOrError_whenNotFound_thenException() {

    final Integer facilityId = 1234;

    when(devUkFacilitiesRepository.findById(facilityId))
        .thenReturn(Optional.empty());

    devUkFacilitiesService.getOrError(facilityId);
  }

  @Test
  public void getOrError_whenFound_thenFacilityReturned() {

    final Integer facilityId = 1234;
    final DevUkFacility facility = DevUkTestUtil.getDevUkFacility();

    when(devUkFacilitiesRepository.findById(facilityId))
        .thenReturn(Optional.of(facility));

    var result = devUkFacilitiesService.getOrError(facilityId);
    assertThat(result).isEqualTo(facility);
  }

  @Test
  public void findById_whenFound_thenEntityReturned() {

    final Integer facilityId = 1234;
    final DevUkFacility facility = DevUkTestUtil.getDevUkFacility();

    when(devUkFacilitiesRepository.findById(facilityId)).thenReturn(Optional.of(facility));

    var result = devUkFacilitiesService.findById(facilityId);
    assertThat(result).contains(facility);
  }

  @Test
  public void findById_whenNotFound_thenEmptyOptionalReturned() {

    final Integer facilityId = 1234;

    when(devUkFacilitiesRepository.findById(facilityId)).thenReturn(Optional.empty());

    var result = devUkFacilitiesService.findById(facilityId);
    assertThat(result).isNotPresent();
  }

}
