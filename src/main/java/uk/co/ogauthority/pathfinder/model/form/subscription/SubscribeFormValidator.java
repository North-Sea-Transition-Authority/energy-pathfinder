package uk.co.ogauthority.pathfinder.model.form.subscription;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.subscription.RelationToPathfinder;

@Component
public class SubscribeFormValidator implements SmartValidator {

  public static final String MISSING_SUBSCRIBE_REASON_ERROR = "Enter a reason for subscribing";

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubscribeForm) target;

    if (RelationToPathfinder.OTHER.equals(form.getRelationToPathfinder())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "subscribeReason",
          "subscribeReason.invalid",
          MISSING_SUBSCRIBE_REASON_ERROR
      );
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(SubscribeForm.class);
  }
}
