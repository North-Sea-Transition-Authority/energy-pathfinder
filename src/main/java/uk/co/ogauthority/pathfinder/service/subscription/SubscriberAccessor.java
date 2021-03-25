package uk.co.ogauthority.pathfinder.service.subscription;

import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;

@Service
public class SubscriberAccessor {

  private final SubscriberRepository subscriberRepository;

  @Autowired
  public SubscriberAccessor(SubscriberRepository subscriberRepository) {
    this.subscriberRepository = subscriberRepository;
  }

  public List<Subscriber> getAllSubscribers() {
    return IterableUtils.toList(subscriberRepository.findAll());
  }
}
