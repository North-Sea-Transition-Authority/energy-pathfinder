package uk.co.ogauthority.pathfinder.model.form.validation.validemailaddress;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValidEmailAddressTest {

  private Validator validator;

  private ValidEmailAddressTestForm form;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    form = new ValidEmailAddressTestForm();
  }

  @Test
  public void emptyEmail_isValid() {
    Set<ConstraintViolation<ValidEmailAddressTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void invalidEmail_isInvalid() {
    form.setEmailAddress("bad email");
    Set<ConstraintViolation<ValidEmailAddressTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            ValidEmailAddressTestForm.PREFIX + " must be a valid email address"
        );
  }

  @Test
  public void validEmail_isValid() {
    form.setEmailAddress("a@b.com");
    Set<ConstraintViolation<ValidEmailAddressTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }
}
