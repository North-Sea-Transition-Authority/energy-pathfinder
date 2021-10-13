package uk.co.ogauthority.pathfinder.model.form.validation.phonenumber;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
  String message() default "{messagePrefix} must be a valid telephone or mobile number. For example: 020 7946 0330";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String messagePrefix();
}
