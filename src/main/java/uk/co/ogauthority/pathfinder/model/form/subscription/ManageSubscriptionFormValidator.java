package uk.co.ogauthority.pathfinder.model.form.subscription;


import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import uk.co.ogauthority.pathfinder.model.enums.subscription.SubscriptionManagementOption;

@Service
public class ManageSubscriptionFormValidator implements Validator {

  static final String INVALID_OPTION_MESSAGE = "Select a valid option for what you would like to change about your subscription";

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ManageSubscriptionForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var form = (ManageSubscriptionForm) target;

    var subscriptionManageOption = form.getSubscriptionManagementOption();
    if (Objects.nonNull(subscriptionManageOption)) {
      try {
        SubscriptionManagementOption.valueOf(subscriptionManageOption);
      } catch (IllegalArgumentException e) {
        errors.rejectValue(
            "subscriptionManagementOption",
            "subscriptionManagementOption.invalid",
            INVALID_OPTION_MESSAGE
        );
      }
    }
  }
}
