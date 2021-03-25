package uk.co.ogauthority.pathfinder.repository.devuk;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkField;

@Repository
public interface DevUkFieldRepository extends CrudRepository<DevUkField, Integer> {

  List<DevUkField> findAllByStatusInAndFieldNameContainingIgnoreCase(List<Integer> status, String fieldName);


}
