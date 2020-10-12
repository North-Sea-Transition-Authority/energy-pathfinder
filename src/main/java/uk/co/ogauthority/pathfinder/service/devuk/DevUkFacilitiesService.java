package uk.co.ogauthority.pathfinder.service.devuk;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

  private List<DevUkFacility> findByNameContaining(String searchTerm) {
    return devUkFacilitiesRepository.findAllByFacilityNameContainingIgnoreCase(searchTerm);
  }

  public DevUkFacility getOrError(Integer facilityId) {
    return findById(facilityId).orElseThrow(
        () -> new PathfinderEntityNotFoundException(String.format("unable to find facility with ID %d", facilityId))
    );
  }

  public Optional<DevUkFacility> findById(Integer facilityId) {
    return devUkFacilitiesRepository.findById(facilityId);
  }

}
