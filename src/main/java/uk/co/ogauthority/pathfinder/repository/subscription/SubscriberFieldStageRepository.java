package uk.co.ogauthority.pathfinder.repository.subscription;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;

@Repository
public interface SubscriberFieldStageRepository extends CrudRepository<SubscriberFieldStage, Integer> {

  List<SubscriberFieldStage> findAllBySubscriberUuid(UUID subscriberUuid);

  List<SubscriberFieldStage> findAllBySubscriberUuidIn(List<UUID> subscriberUuids);

  void deleteAllBySubscriberUuid(UUID subscriberUuid);

  void deleteAllBySubscriberUuidIn(List<UUID> subscriberUuids);
}
