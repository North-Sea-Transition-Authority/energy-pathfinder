package uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

public class LengthRestrictedStringValidator implements ConstraintValidator<LengthRestrictedString, String> {

  @Override
  public void initialize(LengthRestrictedString value) {
    // simple constraint validator so nothing additional to initialize
  }

  @Override
  public boolean isValid(String value,
                         ConstraintValidatorContext cxt) {
    var maxValue = (Integer) ((ConstraintValidatorContextImpl) cxt).getConstraintDescriptor()
        .getAttributes()
        .get("max");
    return (value == null || value.length() <= maxValue);
  }
}
