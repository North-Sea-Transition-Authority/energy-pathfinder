package uk.co.ogauthority.pathfinder.service.devuk;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    List<RestSearchItem> results = searchSelectorService.search(searchTerm, searchableList)
        .stream()
        .sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());

    return searchSelectorService.addManualEntry(
        searchTerm,
        results
    );
  }

  public List<DevUkFacility> findByNameContaining(String searchTerm) {
    return devUkFacilitiesRepository.findAllByFacilityNameContainingIgnoreCase(searchTerm);
  }

}
