package uk.co.ogauthority.pathfinder.model.form.subscription;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.enums.subscription.SubscriptionManagementOption;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ManageSubscriptionForm {

  @NotNull(message = "Select what you would like to change about your subscription", groups = FullValidation.class)
  private SubscriptionManagementOption subscriptionManagementOption;

  public SubscriptionManagementOption getSubscriptionManagementOption() {
    return subscriptionManagementOption;
  }

  public void setSubscriptionManagementOption(
      SubscriptionManagementOption subscriptionManagementOption) {
    this.subscriptionManagementOption = subscriptionManagementOption;
  }
}
