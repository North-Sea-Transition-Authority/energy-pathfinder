package uk.co.ogauthority.pathfinder.testutil;

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
    return subscribeForm;
  }
}
