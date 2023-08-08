package uk.co.ogauthority.pathfinder.model.form.subscription;

import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class ManageSubscriptionForm {

  @NotNull(message = "Select what you would like to change about your subscription", groups = FullValidation.class)
  private String subscriptionManagementOption;

  public String getSubscriptionManagementOption() {
    return subscriptionManagementOption;
  }

  public void setSubscriptionManagementOption(String subscriptionManagementOption) {
    this.subscriptionManagementOption = subscriptionManagementOption;
  }
}
