package uk.co.ogauthority.pathfinder.repository.devuk;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.devuk.DevUkFacility;

@Repository
public interface DevUkFacilitiesRepository extends CrudRepository<DevUkFacility, Integer> {

  List<DevUkFacility> findAllByFacilityNameContainingIgnoreCase(String searchTerm);
}
