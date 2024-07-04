package uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(LengthRestrictedStringValidator.class)
public class LengthRestrictedStringValidatorTest extends AbstractControllerTest {

  private Validator validator;
  private LengthRestrictedStringTestForm form;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    form = new LengthRestrictedStringTestForm();
  }

  @Test
  public void isValid_whenNull_thenValid() {
    form.setDefaultRestrictedStringValue(null);
    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenLessThanDefaultMax_thenValid() {
    var validValue = ValidatorTestingUtil.getStringOfLength(10);
    form.setDefaultRestrictedStringValue(validValue);

    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenEqualToDefaultMax_thenValid() {
    var invalidValue = ValidatorTestingUtil.exactly4000chars();
    form.setDefaultRestrictedStringValue(invalidValue);

    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenGreaterThanDefaultMax_thenInvalid() {
    var invalidValue = ValidatorTestingUtil.over4000Chars();
    form.setDefaultRestrictedStringValue(invalidValue);

    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            "The string value must be 4000 characters or fewer"
        );
  }

  @Test
  public void isValid_whenGreaterThanDefinedMax_thenInvalid() {
    var invalidValue = ValidatorTestingUtil.getStringOfLength(20);
    form.setRestrictedTo10StringValue(invalidValue);

    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            "The string value must be 10 characters or fewer"
        );
  }

  @Test
  public void isValid_whenEqualToDefinedMax_thenValid() {
    var validValue = ValidatorTestingUtil.getStringOfLength(10);
    form.setRestrictedTo10StringValue(validValue);

    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenGreaterThanDefinedMaxAndMaxIsOne_thenInvalidAndSingularErrorText() {
    var invalidValue = ValidatorTestingUtil.getStringOfLength(2);
    form.setRestrictedTo1StringValue(invalidValue);

    Set<ConstraintViolation<LengthRestrictedStringTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(
            "The string value must be 1 character or fewer"
        );
  }

}