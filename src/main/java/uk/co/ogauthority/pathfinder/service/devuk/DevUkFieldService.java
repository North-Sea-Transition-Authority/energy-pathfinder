package uk.co.ogauthority.pathfinder.service.devuk;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFieldRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;

@Service
public class DevUkFieldService {

  public static final List<Integer> ACTIVE_STATUS_LIST = List.of(500, 600, 700);
  private final DevUkFieldRepository devUkFieldRepository;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public DevUkFieldService(DevUkFieldRepository devUkFieldRepository,
                           SearchSelectorService searchSelectorService) {
    this.devUkFieldRepository = devUkFieldRepository;
    this.searchSelectorService = searchSelectorService;
  }

  /**
   * Find the fields with statuses in ACTIVE_STATUS_LIST matching the search term.
   * @param fieldName a whole or partial fieldname
   * @return list of matching DevUkField entities
   */
  public List<DevUkField> findActiveByFieldName(String fieldName) {
    return devUkFieldRepository.findAllByStatusInAndFieldNameContainingIgnoreCase(ACTIVE_STATUS_LIST, fieldName);
  }

  /**
   * Find the fields with statuses in ACTIVE_STATUS_LIST matching the search term.
   * Include the searchTerm param as a manual entry
   * @param fieldName a whole or partial fieldname
   * @return list of matching DevUkField entities
   */
  public List<RestSearchItem> findActiveByFieldNameWithManualEntry(String fieldName) {
    return searchSelectorService.searchWithManualEntry(
        fieldName,
        findActiveByFieldName(fieldName)
    );
  }

  public DevUkField findById(int id) {
    return devUkFieldRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Couldn't find DEVUK field with ID: %d", id)));
  }

}
