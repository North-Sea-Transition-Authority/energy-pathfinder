package uk.co.ogauthority.pathfinder.model.form.validation.positivewholenumber;

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
public class PositiveWholeNumberTest {

  private Validator validator;
  private PositiveWholeNumberTestForm form;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    form = new PositiveWholeNumberTestForm();
  }

  @Test
  public void emptyNumber_isValid() {
    Set<ConstraintViolation<PositiveWholeNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void invalidNumber_isInvalid() {
    form.setNumber(-1);
    Set<ConstraintViolation<PositiveWholeNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            PositiveWholeNumberTestForm.PREFIX + " must be a positive whole number with no decimal places"
        );
  }

  @Test
  public void validNumber_isValid() {
    form.setNumber(12);
    Set<ConstraintViolation<PositiveWholeNumberTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }
}
