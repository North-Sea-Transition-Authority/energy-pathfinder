package uk.co.ogauthority.pathfinder.service.subscription;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.repository.subscription.SubscriberRepository;
import uk.co.ogauthority.pathfinder.testutil.SubscriptionTestUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberAccessorTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  private SubscriberAccessor subscriberAccessor;

  @Before
  public void setup() {
    subscriberAccessor = new SubscriberAccessor(subscriberRepository);
  }

  @Test
  public void getAllSubscribers_whenNoResults_thenEmptyList() {
    when(subscriberRepository.findAll()).thenReturn(Collections.emptyList());
    assertThat(subscriberAccessor.getAllSubscribers()).isEmpty();
  }

  @Test
  public void getAllSubscribers_whenResults_thenPopulatedList() {

    final var subscriber1 = SubscriptionTestUtil.createSubscriber("someone@example.com");
    final var subscriber2 = SubscriptionTestUtil.createSubscriber("someone.else@example.com");
    when(subscriberRepository.findAll()).thenReturn(List.of(subscriber1, subscriber2));
    
    assertThat(subscriberAccessor.getAllSubscribers()).containsExactly(subscriber1, subscriber2);
  }
}