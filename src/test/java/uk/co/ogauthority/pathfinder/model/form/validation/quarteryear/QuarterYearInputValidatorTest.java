package uk.co.ogauthority.pathfinder.model.form.validation.quarteryear;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class QuarterYearInputValidatorTest {

  private QuarterYearInputValidator quarterYearInputValidator;
  private QuarterYearInput quarterYearInput;
  private FormInputLabel formInputLabel;
  private EmptyQuarterYearAcceptableHint emptyQuarterYearAcceptableHint;

  @Before
  public void setup() {
    quarterYearInputValidator = new QuarterYearInputValidator();
    quarterYearInput = new QuarterYearInput(null, null);
    formInputLabel = new FormInputLabel("Some date");
    emptyQuarterYearAcceptableHint = new EmptyQuarterYearAcceptableHint();
  }

  private BindingResult getErrors(QuarterYearInput quarterYearInput, Object[] validationHints) {
    var errors = new BeanPropertyBindingResult(quarterYearInput, "form");
    ValidationUtils.invokeValidator(quarterYearInputValidator, quarterYearInput, errors, validationHints);
    return errors;
  }

  @Test
  public void validate_whenEmptyWithNoHints_thenInvalid() {

    Object[] validationHints = {formInputLabel};
    var errors = getErrors(quarterYearInput, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("quarter", Set.of("quarter.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("quarter", Set.of(String.format(QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR, "a " + formInputLabel.getLabel()))),
        entry("year",  Set.of(""))
    );
  }

  @Test
  public void validate_whenEmptyWithEmptyQuarterYearAcceptableHint_thenValid() {

    Object[] validationHints = {formInputLabel, emptyQuarterYearAcceptableHint};
    var errors = getErrors(quarterYearInput, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenInvalidQuarter_thenInvalid() {

    Object[] validationHints = {formInputLabel};

    quarterYearInput = new QuarterYearInput(null, "2020");
    var errors = getErrors(quarterYearInput, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("quarter", Set.of("quarter.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("quarter", Set.of(formInputLabel.getLabel() + QuarterYearInputValidator.VALID_QUARTER_YEAR_ERROR)),
        entry("year",  Set.of(""))
    );
  }

  @Test
  public void validate_whenInvalidYear_thenInvalid() {

    Object[] validationHints = {formInputLabel};

    quarterYearInput = new QuarterYearInput(Quarter.Q1, null);
    var errors = getErrors(quarterYearInput, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("quarter", Set.of("quarter.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("quarter", Set.of(formInputLabel.getLabel() + QuarterYearInputValidator.VALID_QUARTER_YEAR_ERROR)),
        entry("year",  Set.of(""))
    );
  }

  @Test
  public void validate_whenValidInput_thenValid() {

    Object[] validationHints = {formInputLabel, emptyQuarterYearAcceptableHint};

    quarterYearInput = new QuarterYearInput(Quarter.Q1, "2020");
    var errors = getErrors(quarterYearInput, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenNotFourNumberYear_thenInvalid() {

    Object[] validationHints = {formInputLabel};

    quarterYearInput = new QuarterYearInput(Quarter.Q1, "20");
    var errors = getErrors(quarterYearInput, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("quarter", Set.of("quarter.minLengthNotMet")),
        entry("year", Set.of("year.minLengthNotMet"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("quarter", Set.of(DateInputValidator.getIncorrectYearFormatErrorMessage(formInputLabel))),
        entry("year",  Set.of(""))
    );
  }

}