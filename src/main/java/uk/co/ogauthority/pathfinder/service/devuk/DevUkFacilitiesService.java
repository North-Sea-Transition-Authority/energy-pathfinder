package uk.co.ogauthority.pathfinder.service.devuk;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.rest.DevUkRestController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFacilitiesRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class DevUkFacilitiesService {

  private final DevUkFacilitiesRepository devUkFacilitiesRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public DevUkFacilitiesService(DevUkFacilitiesRepository devUkFacilitiesRepository,
                                SearchSelectorService searchSelectorService) {
    this.devUkFacilitiesRepository = devUkFacilitiesRepository;
    this.searchSelectorService = searchSelectorService;
  }

  public List<RestSearchItem> searchFacilitiesWithNameContainingWithManualEntry(String searchTerm) {
    var searchableList = findByNameContaining(searchTerm);
    return searchSelectorService.searchWithManualEntry(searchTerm, searchableList);
  }

  public String getFacilitiesRestUrl() {
    return SearchSelectorService.route(
        on(DevUkRestController.class).searchFacilitiesWithManualEntry(null)
    );
  }

  public Map<String, String> getPreSelectedFacility(String facilityFromForm) {
    return (facilityFromForm != null)
        ? searchSelectorService.getPreSelectedSearchSelectorValue(
            facilityFromForm,
            getFacilityAsList(facilityFromForm)
        )
        : Map.of();
  }

  public List<DevUkFacility> getFacilityAsList(String facilityFromForm) {

    var facilities = new ArrayList<DevUkFacility>();

    if (facilityFromForm != null && !SearchSelectorService.isManualEntry(facilityFromForm)) {
      var facilityId = Integer.parseInt(facilityFromForm);
      findById(facilityId).ifPresent(facilities::add);
    }

    return facilities;

  }

  public DevUkFacility getOrError(Integer facilityId) {
    return findById(facilityId).orElseThrow(
        () -> new PathfinderEntityNotFoundException(String.format("unable to find facility with ID %d", facilityId))
    );
  }

  public Optional<DevUkFacility> findById(Integer facilityId) {
    return devUkFacilitiesRepository.findById(facilityId);
  }

  private List<DevUkFacility> findByNameContaining(String searchTerm) {
    return devUkFacilitiesRepository.findAllByFacilityNameContainingIgnoreCase(searchTerm);
  }
}
