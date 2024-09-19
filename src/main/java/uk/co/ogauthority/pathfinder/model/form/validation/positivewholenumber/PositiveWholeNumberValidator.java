package uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * If a value is present ensure it's >= 0.
 */
public class PositiveWholeNumberValidator implements ConstraintValidator<PositiveWholeNumber, Integer> {

  @Override
  public void initialize(PositiveWholeNumber value) {
    // simple constraint validator so nothing additional to initialize
  }

  @Override
  public boolean isValid(Integer value,
                         ConstraintValidatorContext cxt) {
    return (value == null || value >= 0);
  }
}
