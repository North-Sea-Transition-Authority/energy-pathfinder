package uk.co.ogauthority.pathfinder.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceTest {

  private ValidationService validationService;

  @Before
  public void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    validationService = new ValidationService(validator);
  }

  static class TestForm {

    @NotNull(message = "Cannot be null", groups = FullValidation.class)
    private final String field1;

    @NotNull(message = "Cannot be null", groups = FullValidation.class)
    private final String field2;

    TestForm(String field1, String field2) {
      this.field1 = field1;
      this.field2 = field2;
    }
  }

  @Test
  public void validate_whenFullValidationAndEmptyForm_errorsExist() {
    var form = new TestForm(null, null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    validationService.validate(form, bindingResult, ValidationType.FULL);
    var fieldErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(fieldErrors).containsExactly(
        entry("field1", Set.of("NotNull")),
        entry("field2", Set.of("NotNull"))
    );
  }

  @Test
  public void validate_whenFullValidationAndPartiallyEmptyForm_errorExists() {
    var form = new TestForm(null, "value");
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    validationService.validate(form, bindingResult, ValidationType.FULL);
    var fieldErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(fieldErrors).containsExactly(
        entry("field1", Set.of("NotNull"))
    );
  }

  @Test
  public void validate_whenFullValidationAndCompleteForm_noErrorsExist() {
    var form = new TestForm("value1", "value2");
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    validationService.validate(form, bindingResult, ValidationType.FULL);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

  @Test
  public void validate_whenPartialValidationAndEmptyForm_noErrorsExist() {
    var form = new TestForm(null, null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    validationService.validate(form, bindingResult, ValidationType.PARTIAL);
    assertThat(bindingResult.hasErrors()).isFalse();
  }

}