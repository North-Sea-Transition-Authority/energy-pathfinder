package uk.co.ogauthority.pathfinder.repository.wellbore;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.wellbore.Wellbore;

@Repository
public interface WellboreRepository extends CrudRepository<Wellbore, Integer> {

  List<Wellbore> findAllByRegistrationNoContainingIgnoreCase(String searchTerm);

  List<Wellbore> findAllByIdIn(List<Integer> wellboreIds);
}
