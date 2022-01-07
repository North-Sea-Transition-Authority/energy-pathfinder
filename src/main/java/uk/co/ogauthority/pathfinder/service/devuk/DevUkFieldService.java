package uk.co.ogauthority.pathfinder.service.devuk;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFieldRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class DevUkFieldService {

  private final DevUkFieldRepository devUkFieldRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public DevUkFieldService(DevUkFieldRepository devUkFieldRepository,
                           SearchSelectorService searchSelectorService) {
    this.devUkFieldRepository = devUkFieldRepository;
    this.searchSelectorService = searchSelectorService;
  }

  /**
   * Find the fields matching the search term.
   * @param fieldName a whole or partial field name
   * @return list of matching DevUkField entities
   */
  private List<DevUkField> findSelectableFieldsByName(String fieldName) {
    return devUkFieldRepository.findAllByFieldNameContainingIgnoreCaseAndIsLandwardFalseAndIsActiveTrue(fieldName);
  }

  /**
   * Find the fields with statuses in ACTIVE_STATUS_LIST matching the search term.
   * Include the searchTerm param as a manual entry
   * @param searchTerm a whole or partial fieldname
   * @return list of matching DevUkField entities
   */
  public List<RestSearchItem> searchFieldsWithNameContaining(String searchTerm) {
    return searchSelectorService.search(
        searchTerm,
        findSelectableFieldsByName(searchTerm)
    );
  }

  public DevUkField findByIdOrError(int id) {
    return findById(id)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format("Couldn't find DEVUK field with ID: %d", id)));
  }

  public Optional<DevUkField> findById(int id) {
    return devUkFieldRepository.findById(id);
  }

}
