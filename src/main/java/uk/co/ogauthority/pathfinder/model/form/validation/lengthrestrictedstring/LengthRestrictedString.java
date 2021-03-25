package uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = LengthRestrictedStringValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LengthRestrictedString {

  String message() default "{messagePrefix} can not be more than {max} character${max > 1 ? 's' : ''} " +
      "(currently ${validatedValue.length()} character${validatedValue.length() > 1 ? 's' : ''})";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int max() default 4000;

  String messagePrefix();

}
