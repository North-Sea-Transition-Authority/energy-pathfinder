package uk.co.ogauthority.pathfinder.model.form.validation.email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Ensure if an email address was provided it is valid.
 */
public class EmailAddressValidator implements ConstraintValidator<ValidEmail, String> {
  @Override
  public void initialize(ValidEmail constraintAnnotation) {
    //nothing to do
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null || EmailValidator.getInstance().isValid(value);
  }
}
