package uk.co.ogauthority.pathfinder.service.wellbore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.controller.rest.WellboreRestController;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.wellbore.WellboreRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class WellboreService {

  private final WellboreRepository wellboreRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public WellboreService(WellboreRepository wellboreRepository,
                         SearchSelectorService searchSelectorService) {
    this.wellboreRepository = wellboreRepository;
    this.searchSelectorService = searchSelectorService;
  }

  public List<RestSearchItem> searchWellboresWithWellRegistrationNoContaining(String searchTerm) {
    var searchableList = findByWellRegistrationNoContaining(searchTerm);
    return searchSelectorService.search(searchTerm, searchableList);
  }

  private List<Wellbore> findByWellRegistrationNoContaining(String searchTerm) {
    return wellboreRepository.findAllByRegistrationNoContainingIgnoreCase(searchTerm);
  }

  public List<Wellbore> getWellboresByIdsIn(List<Integer> wellboreIds) {
    return wellboreRepository.findAllByIdIn(wellboreIds);
  }

  public String getWellboreRestUrl() {
    return SearchSelectorService.route(on(WellboreRestController.class).searchWellbores(null));
  }
}
