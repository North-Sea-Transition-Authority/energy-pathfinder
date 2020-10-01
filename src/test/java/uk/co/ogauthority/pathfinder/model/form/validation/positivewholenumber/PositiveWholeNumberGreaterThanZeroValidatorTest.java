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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;

@RunWith(SpringRunner.class)
@WebMvcTest(PositiveWholeNumberGreaterThanZero.class)
public class PositiveWholeNumberGreaterThanZeroValidatorTest extends AbstractControllerTest {

  private static final String INVALID_ERROR_MESSAGE = PositiveWholeNumberGreaterThanZeroTestForm.PREFIX
      + " must be a positive whole number greater than zero with no decimal places";

  private Validator validator;
  private PositiveWholeNumberGreaterThanZeroTestForm form;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    form = new PositiveWholeNumberGreaterThanZeroTestForm();
  }

  @Test
  public void isValid_whenNull_thenValid() {
    form.setNumber(null);
    Set<ConstraintViolation<PositiveWholeNumberGreaterThanZeroTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenZero_thenInvalid() {
    form.setNumber(0);
    Set<ConstraintViolation<PositiveWholeNumberGreaterThanZeroTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(INVALID_ERROR_MESSAGE);
  }

  @Test
  public void isValid_whenNegative_thenInvalid() {
    form.setNumber(-1);
    Set<ConstraintViolation<PositiveWholeNumberGreaterThanZeroTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(INVALID_ERROR_MESSAGE);
  }

  @Test
  public void isValid_whenPositive_thenValid() {
    form.setNumber(1);
    Set<ConstraintViolation<PositiveWholeNumberGreaterThanZeroTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

}