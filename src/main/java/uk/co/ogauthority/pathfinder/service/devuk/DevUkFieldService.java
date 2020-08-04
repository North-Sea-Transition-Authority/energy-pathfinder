package uk.co.ogauthority.pathfinder.service.devuk;

import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;
import uk.co.ogauthority.pathfinder.repository.devuk.DevUkFieldRepository;

@Service
public class DevUkFieldService {

  private final DevUkFieldRepository devUkFieldRepository;

  @Autowired
  public DevUkFieldService(DevUkFieldRepository devUkFieldRepository) {
    this.devUkFieldRepository = devUkFieldRepository;
  }

  /**
   * TODO find out if active correct term for status codes.
   * Find the fields with statuses in 500, 600, 700 matching the search term
   * @param fieldName a whole or partial fieldname
   * @return list of matching DevUkField entities
   */
  public List<DevUkField> findActiveByFieldName(String fieldName) {
    return devUkFieldRepository.findAllByStatusInAndFieldNameContainingIgnoreCase(List.of(500, 600, 700), fieldName);
  }

  public DevUkField findById(int id) {
    return devUkFieldRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(String.format("Couldn't find DEVUK field with ID: %d", id)));
  }

}
