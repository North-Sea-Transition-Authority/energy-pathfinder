package uk.co.ogauthority.pathfinder.model.form.validation.validphonenumber;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ValidPhoneNumberTest {

  private Validator validator;
  private ValidPhoneNumberTestForm form;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    form = new ValidPhoneNumberTestForm();
  }

  @Test
  public void emptyNumber_isValid() {
    Set<ConstraintViolation<ValidPhoneNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void invalidNumber_isInvalid() {
    form.setPhoneNumber("bad number");
    Set<ConstraintViolation<ValidPhoneNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            ValidPhoneNumberTestForm.PREFIX + " must be a valid telephone or mobile number. For example: 020 7946 0330"
        );
  }

  @Test
  public void validNumber_isValid() {
    form.setPhoneNumber("01303 123 456");
    Set<ConstraintViolation<ValidPhoneNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void internationalNumber_missingCountryCode_invalid() {
    form.setPhoneNumber("044 668 18 00");
    Set<ConstraintViolation<ValidPhoneNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            ValidPhoneNumberTestForm.PREFIX + " must be a valid telephone or mobile number. For example: 020 7946 0330"
        );
  }

  @Test
  public void internationNumber_withCountryCode_isValid() {
    form.setPhoneNumber("+41 044 668 18 00");
    Set<ConstraintViolation<ValidPhoneNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }
}
