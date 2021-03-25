package uk.co.ogauthority.pathfinder.energyportal.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {
  List<Person> findAllByIdIn(Collection<Integer> personIds);
}