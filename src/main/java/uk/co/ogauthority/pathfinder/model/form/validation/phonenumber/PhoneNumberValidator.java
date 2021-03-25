package uk.co.ogauthority.pathfinder.model.form.validation.phonenumber;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * If a value is provided ensure it is a valid UK phone number.
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
  @Override
  public void initialize(ValidPhoneNumber constraintAnnotation) {
    // nothing to do
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    var util = PhoneNumberUtil.getInstance();
    try {
      var phoneNo = util.parse(value, "GB");
      return util.isValidNumber(phoneNo);
    } catch (NumberParseException e) {
      return false;
    }

  }
}
