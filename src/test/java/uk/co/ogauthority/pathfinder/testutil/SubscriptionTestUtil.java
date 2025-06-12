package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.subscription.Subscriber;
import uk.co.ogauthority.pathfinder.model.entity.subscription.SubscriberFieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;
import uk.co.ogauthority.pathfinder.model.form.subscription.SubscribeForm;

public class SubscriptionTestUtil {

  public static final String FORENAME = "Forename";
  public static final String SURNAME = "Surname";
  public static final String EMAIL_ADDRESS = "test@test.com";
  public static final RelationToPathfinder RELATION_TO_PATHFINDER = RelationToPathfinder.OTHER;
  public static final String SUBSCRIBE_REASON = "Subscribe reason";

  private SubscriptionTestUtil() {
    throw new IllegalStateException("SubscriptionTestUtil is a utility class and should not be instantiated");
  }

  public static SubscribeForm createSubscribeForm() {
    var subscribeForm = new SubscribeForm();
    subscribeForm.setForename(FORENAME);
    subscribeForm.setSurname(SURNAME);
    subscribeForm.setEmailAddress(EMAIL_ADDRESS);
    subscribeForm.setRelationToPathfinder(RELATION_TO_PATHFINDER);
    subscribeForm.setSubscribeReason(SUBSCRIBE_REASON);
    subscribeForm.setInterestedInAllProjects(false);
    subscribeForm.setFieldStages(List.of("OIL_AND_GAS", "WIND_ENERGY"));
    return subscribeForm;
  }

  public static Subscriber createSubscriber() {
    final var subscriber = new Subscriber();
    subscriber.setForename(FORENAME);
    subscriber.setSurname(SURNAME);
    subscriber.setEmailAddress(EMAIL_ADDRESS);
    subscriber.setRelationToPathfinder(RELATION_TO_PATHFINDER);
    subscriber.setSubscribeReason(SUBSCRIBE_REASON);
    subscriber.setUuid(UUID.randomUUID());
    subscriber.setSubscribedInstant(Instant.now());
    return subscriber;
  }

  public static Subscriber createSubscriber(String emailAddress) {
    final var subscriber = createSubscriber();
    subscriber.setEmailAddress(emailAddress);
    return subscriber;
  }

  public static List<SubscriberFieldStage> createSubscriberFieldStages(List<FieldStage> fieldStages, UUID subscriberUuid) {
    return fieldStages
        .stream()
        .map(fieldStage -> {
          var subscriberFieldStage = new SubscriberFieldStage();
          subscriberFieldStage.setFieldStage(fieldStage);
          subscriberFieldStage.setSubscriberUuid(subscriberUuid);
          return subscriberFieldStage;
        })
        .collect(Collectors.toList());
  }
}
