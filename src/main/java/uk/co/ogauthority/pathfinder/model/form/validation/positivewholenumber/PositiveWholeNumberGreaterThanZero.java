package uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PositiveWholeNumberGreaterThanZeroValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveWholeNumberGreaterThanZero {

  String message() default "{messagePrefix} must be a positive whole number greater than zero with no decimal places";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String messagePrefix();

}