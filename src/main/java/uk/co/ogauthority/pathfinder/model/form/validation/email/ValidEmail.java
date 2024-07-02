package uk.co.ogauthority.pathfinder.model.form.validation.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailAddressValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {

  String message() default "{messagePrefix} must be a valid email address";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String messagePrefix();
}
