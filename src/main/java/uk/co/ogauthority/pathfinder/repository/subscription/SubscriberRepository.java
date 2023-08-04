package uk.co.ogauthority.pathfinder.repository.subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;

@Repository
public interface SubscriberRepository extends CrudRepository<Subscriber, Integer> {

  boolean existsByEmailAddress(String emailAddress);

  void deleteByUuid(UUID uuid);

  void deleteByEmailAddress(String emailAddress);

  Optional<Subscriber> findByUuid(UUID uuid);

  List<Subscriber> findAllByEmailAddress(String emailAddress);
}
