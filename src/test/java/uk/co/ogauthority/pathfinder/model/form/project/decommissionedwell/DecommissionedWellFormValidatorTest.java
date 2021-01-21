package uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedWellFormValidatorTest {

  @Mock
  private QuarterYearInputValidator quarterYearInputValidator;

  private DecommissionedWellFormValidator decommissionedWellFormValidator;

  @Before
  public void setup() {
    decommissionedWellFormValidator = new DecommissionedWellFormValidator(quarterYearInputValidator);
    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any());
    when(quarterYearInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_whenValidFormFullSave_thenNoErrors() {
    var form = DecommissionedWellTestUtil.getCompletedForm();

    var errors = new BeanPropertyBindingResult(form, "form");
    var decommissionedWellValidationHint = new DecommissionedWellValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(decommissionedWellFormValidator, form, errors, decommissionedWellValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenValidFormPartialSave_thenNoErrors() {
    var form = DecommissionedWellTestUtil.getCompletedForm();

    var errors = new BeanPropertyBindingResult(form, "form");
    var decommissionedWellValidationHint = new DecommissionedWellValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(decommissionedWellFormValidator, form, errors, decommissionedWellValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenEmptyPlugAbandonmentDateAndPartialSave_thenNoError() {

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new QuarterYearInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");
    var decommissionedWellValidationHint = new DecommissionedWellValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(decommissionedWellFormValidator, form, errors, decommissionedWellValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenEmptyPlugAbandonmentDateAndFullSave_thenError() {

    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new QuarterYearInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");
    var decommissionedWellValidationHint = new DecommissionedWellValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(decommissionedWellFormValidator, form, errors, decommissionedWellValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("plugAbandonmentDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("plugAbandonmentDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("plugAbandonmentDate.quarter", Set.of(String.format(
            QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR,
            "a " + DecommissionedWellValidationHint.PLUG_ABANDONMENT_DATE_LABEL.getLabel()
        ))),
        entry("plugAbandonmentDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenPartialPlugAbandonmentDateAndFullSave_thenError() {
    validatePartialPlugAbandonmentDate(ValidationType.FULL);
  }

  @Test
  public void validate_whenPartialPlugAbandonmentDateAndPartialSave_thenError() {
    validatePartialPlugAbandonmentDate(ValidationType.PARTIAL);
  }

  private void validatePartialPlugAbandonmentDate(ValidationType validationType) {
    var form = DecommissionedWellTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new QuarterYearInput(Quarter.Q1, null));

    var errors = new BeanPropertyBindingResult(form, "form");
    var decommissionedWellValidationHint = new DecommissionedWellValidationHint(validationType);

    ValidationUtils.invokeValidator(decommissionedWellFormValidator, form, errors, decommissionedWellValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("plugAbandonmentDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("plugAbandonmentDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("plugAbandonmentDate.quarter", Set.of(String.format(
            QuarterYearInputValidator.VALID_QUARTER_YEAR_ERROR,
            DecommissionedWellValidationHint.PLUG_ABANDONMENT_DATE_LABEL.getInitCappedLabel()
        ))),
        entry("plugAbandonmentDate.year", Set.of(""))
    );
  }

}