package uk.co.ogauthority.pathfinder.service.subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberFieldStageRepository;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;
import uk.co.ogauthority.pathfinder.util.OraclePartitionUtil;

@ExtendWith(MockitoExtension.class)
class SubscriberAccessorTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  @Mock
  private SubscriberFieldStageRepository subscriberFieldStageRepository;

  @InjectMocks
  private SubscriberAccessor subscriberAccessor;

  @Test
  void getAllSubscribers_whenNoResults_thenEmptyList() {
    when(subscriberRepository.findAll()).thenReturn(Collections.emptyList());
    assertThat(subscriberAccessor.getAllSubscribers()).isEmpty();
  }

  @Test
  void getAllSubscribers_whenResults_thenPopulatedList() {
    final var subscriber1 = SubscriptionTestUtil.createSubscriber("someone@example.com");
    final var subscriber2 = SubscriptionTestUtil.createSubscriber("someone.else@example.com");
    when(subscriberRepository.findAll()).thenReturn(List.of(subscriber1, subscriber2));
    
    assertThat(subscriberAccessor.getAllSubscribers()).containsExactly(subscriber1, subscriber2);
  }

  @Test
  void getSubscriberFieldStages() {
    var subscriber = SubscriptionTestUtil.createSubscriber();
    var subscriberUuid = subscriber.getUuid();
    var subscriberFieldStage = mock(SubscriberFieldStage.class);

    when(subscriberFieldStageRepository.findAllBySubscriberUuidIn(List.of(subscriberUuid))).thenReturn(List.of(subscriberFieldStage));

    var result = subscriberAccessor.getAllSubscriberFieldStages(List.of(subscriber));
    assertThat(result).isEqualTo(List.of(subscriberFieldStage));
  }

  @Test
  void getSubscriberFieldStages_moreThanOracleLimit_verifyMultipleCalls() {
    List<Subscriber> subscribers = new ArrayList<>();
    for (int i = 0; i <= OraclePartitionUtil.ORACLE_LIMIT + 1; i++) {
      subscribers.add(SubscriptionTestUtil.createSubscriber());
    }

    subscriberAccessor.getAllSubscriberFieldStages(subscribers);

    verify(subscriberFieldStageRepository, times(2)).findAllBySubscriberUuidIn(anyList());
  }
}
