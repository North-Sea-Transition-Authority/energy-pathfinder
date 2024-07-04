package uk.co.ogauthority.pathfinder.model.form.validation.email;

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

@RunWith(SpringRunner.class)
@WebMvcTest(EmailAddressValidator.class)
public class EmailAddressValidatorTest extends AbstractControllerTest {

  private static final String INVALID_ERROR_MESSAGE = EmailAddressValidatorTestForm.PREFIX + " must be a valid email address";

  private Validator validator;
  private EmailAddressValidatorTestForm form;

  @Before
  public void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    form = new EmailAddressValidatorTestForm();
  }

  @Test
  public void isValid_whenNull_thenValid() {
    form.setEmailAddress(null);
    Set<ConstraintViolation<EmailAddressValidatorTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenValidEmailAddress_thenValid() {
    form.setEmailAddress("someone@example.com");
    Set<ConstraintViolation<EmailAddressValidatorTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenNonStandardDomainExtension_thenValid() {
    form.setEmailAddress("someone@fivium.london");
    Set<ConstraintViolation<EmailAddressValidatorTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  public void isValid_whenInvalidEmailAddress_thenInvalid() {
    form.setEmailAddress("not an email address");
    Set<ConstraintViolation<EmailAddressValidatorTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(INVALID_ERROR_MESSAGE);
  }

  @Test
  public void isValid_whenPartialEmailAddress_thenInvalid() {
    form.setEmailAddress("someone@example");
    Set<ConstraintViolation<EmailAddressValidatorTestForm>> constraintViolations = validator.validate(form);
    assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
        .containsExactly(INVALID_ERROR_MESSAGE);
  }

}