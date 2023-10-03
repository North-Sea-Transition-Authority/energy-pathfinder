package uk.co.ogauthority.pathfinder.service.subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberFieldStageRepository;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.util.OraclePartitionUtil;

@Service
public class SubscriberAccessor {

  private final SubscriberRepository subscriberRepository;
  private final SubscriberFieldStageRepository subscriberFieldStageRepository;

  @Autowired
  public SubscriberAccessor(SubscriberRepository subscriberRepository,
                            SubscriberFieldStageRepository subscriberFieldStageRepository) {
    this.subscriberRepository = subscriberRepository;
    this.subscriberFieldStageRepository = subscriberFieldStageRepository;
  }

  public List<Subscriber> getAllSubscribers() {
    return IterableUtils.toList(subscriberRepository.findAll());
  }

  public List<SubscriberFieldStage> getAllSubscriberFieldStages(List<Subscriber> subscriber) {
    var subscriberUuids = subscriber.stream()
        .map(Subscriber::getUuid)
        .collect(Collectors.toList());

    List<SubscriberFieldStage> subscriberFieldStages = new ArrayList<>();

    OraclePartitionUtil.partitionedList(subscriberUuids).forEach(
        subscriberUuidsList -> {
          var subscriberFieldStagesSubList = subscriberFieldStageRepository.findAllBySubscriberUuidIn(subscriberUuidsList);
          subscriberFieldStages.addAll(subscriberFieldStagesSubList);
        }
    );

    return subscriberFieldStages;
  }

}
