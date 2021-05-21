package uk.co.ogauthority.pathfinder.model.form.validation.quarteryear;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.OnOrAfterQuarterYearHint;
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
        entry("quarter", Set.of(String.format(QuarterYearInputValidator.VALID_QUARTER_YEAR_ERROR, formInputLabel.getLabel()))),
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
        entry("quarter", Set.of(String.format(QuarterYearInputValidator.VALID_QUARTER_YEAR_ERROR, formInputLabel.getLabel()))),
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

  @Test
  public void addEmptyQuarterYearAcceptableHint_whenPartial_thenEmptyQuarterYearAcceptableHintAdded() {
    var hints = new ArrayList<>();
    QuarterYearInputValidator.addEmptyQuarterYearAcceptableHint(ValidationType.PARTIAL, hints);
    assertThat(hints).hasSize(1);
    var hint = hints.get(0);
    assertThat(hint).isInstanceOf(EmptyQuarterYearAcceptableHint.class);
  }

  @Test
  public void addEmptyQuarterYearAcceptableHint_whenFull_thenEmptyQuarterYearAcceptableHintNotAdded() {
    var hints = new ArrayList<>();
    QuarterYearInputValidator.addEmptyQuarterYearAcceptableHint(ValidationType.FULL, hints);
    assertThat(hints).isEmpty();
  }

  @Test
  public void validate_whenOnOrAfterQuarterYearHintAndQuarterIsBeforeAndYearIsSame_thenError() {

    final var quarter = Quarter.Q2;
    final var beforeQuarter = Quarter.Q1;

    final var onOrAfterQuarterYear = new QuarterYearInput(
        quarter,
        "2020"
    );

    final var pastQuarterYear = new QuarterYearInput(
        beforeQuarter,
        onOrAfterQuarterYear.getYear()
    );

    final var quarterYearInputToValidateAgainstLabel = "example label";

    final Object[] validationHints = {
        formInputLabel,
        new OnOrAfterQuarterYearHint(formInputLabel, onOrAfterQuarterYear, quarterYearInputToValidateAgainstLabel)
    };

    final var expectedErrorMessage = String.format(
        OnOrAfterQuarterYearHint.ERROR_MESSAGE_TEXT,
        formInputLabel.getInitCappedLabel(),
        quarterYearInputToValidateAgainstLabel
    );

    validateAndAssertExpectedErrors(
        pastQuarterYear,
        validationHints,
        QuarterYearInputValidator.QUARTER_ON_OR_AFTER_DATE_CODE,
        QuarterYearInputValidator.YEAR_ON_OR_AFTER_DATE_CODE,
        expectedErrorMessage
    );
  }

  @Test
  public void validate_whenOnOrAfterQuarterYearHintAndQuarterIsSameAndYearIsBefore_thenError() {

    final var year = LocalDate.now();
    final var beforeYear = LocalDate.now().minusYears(1);

    final var onOrAfterQuarterYear = new QuarterYearInput(
        Quarter.Q2,
        String.valueOf(year.getYear())
    );

    final var pastQuarterYear = new QuarterYearInput(
        onOrAfterQuarterYear.getQuarter(),
        String.valueOf(beforeYear.getYear())
    );

    final var quarterYearInputToValidateAgainstLabel = "example label";

    final Object[] validationHints = {
        formInputLabel,
        new OnOrAfterQuarterYearHint(formInputLabel, onOrAfterQuarterYear, quarterYearInputToValidateAgainstLabel)
    };

    final var expectedErrorMessage = String.format(
        OnOrAfterQuarterYearHint.ERROR_MESSAGE_TEXT,
        formInputLabel.getInitCappedLabel(),
        quarterYearInputToValidateAgainstLabel
    );

    validateAndAssertExpectedErrors(
        pastQuarterYear,
        validationHints,
        QuarterYearInputValidator.QUARTER_ON_OR_AFTER_DATE_CODE,
        QuarterYearInputValidator.YEAR_ON_OR_AFTER_DATE_CODE,
        expectedErrorMessage
    );
  }

  @Test
  public void validate_whenOnOrAfterQuarterYearHintAndQuarterAndYearIsBefore_thenError() {

    final var year = LocalDate.now();
    final var beforeYear = LocalDate.now().minusYears(1);

    final var quarter = Quarter.Q2;
    final var beforeQuarter = Quarter.Q1;

    final var onOrAfterQuarterYear = new QuarterYearInput(
        quarter,
        String.valueOf(year.getYear())
    );

    final var pastQuarterYear = new QuarterYearInput(
        beforeQuarter,
        String.valueOf(beforeYear.getYear())
    );

    final var quarterYearInputToValidateAgainstLabel = "example label";

    final Object[] validationHints = {
        formInputLabel,
        new OnOrAfterQuarterYearHint(formInputLabel, onOrAfterQuarterYear, quarterYearInputToValidateAgainstLabel)
    };

    final var expectedErrorMessage = String.format(
        OnOrAfterQuarterYearHint.ERROR_MESSAGE_TEXT,
        formInputLabel.getInitCappedLabel(),
        quarterYearInputToValidateAgainstLabel
    );

    validateAndAssertExpectedErrors(
        pastQuarterYear,
        validationHints,
        QuarterYearInputValidator.QUARTER_ON_OR_AFTER_DATE_CODE,
        QuarterYearInputValidator.YEAR_ON_OR_AFTER_DATE_CODE,
        expectedErrorMessage
    );
  }

  @Test
  public void validate_whenOnOrAfterQuarterYearHintAndInputIsSame_thenNoError() {

    final var onOrAfterQuarterYear = new QuarterYearInput(
        Quarter.Q1,
        "2020"
    );

    final var quarterYearToValidate = new QuarterYearInput(
        onOrAfterQuarterYear.getQuarter(),
        onOrAfterQuarterYear.getYear()
    );

    final var quarterYearInputToValidateAgainstLabel = "example label";

    final Object[] validationHints = {
        formInputLabel,
        new OnOrAfterQuarterYearHint(formInputLabel, onOrAfterQuarterYear, quarterYearInputToValidateAgainstLabel)
    };

    validateAndAssertNoErrors(
        quarterYearToValidate,
        validationHints
    );
  }

  @Test
  public void validate_whenOnOrAfterQuarterYearHintAndInputQuarterIsAfterAndYearIsSame_thenNoError() {

    final var onOrAfterQuarterYear = new QuarterYearInput(
        Quarter.Q1,
        "2020"
    );

    final var quarterYearToValidate = new QuarterYearInput(
        Quarter.Q2,
        onOrAfterQuarterYear.getYear()
    );

    final var quarterYearInputToValidateAgainstLabel = "example label";

    final Object[] validationHints = {
        formInputLabel,
        new OnOrAfterQuarterYearHint(formInputLabel, onOrAfterQuarterYear, quarterYearInputToValidateAgainstLabel)
    };

    validateAndAssertNoErrors(
        quarterYearToValidate,
        validationHints
    );
  }

  @Test
  public void validate_whenOnOrAfterQuarterYearHintAndInputQuarterIsSameAndYearIsAfter_thenNoError() {

    final var onOrAfterQuarterYear = new QuarterYearInput(
        Quarter.Q1,
        "2020"
    );

    final var quarterYearToValidate = new QuarterYearInput(
        onOrAfterQuarterYear.getQuarter(),
        "2021"
    );

    final var quarterYearInputToValidateAgainstLabel = "example label";

    final Object[] validationHints = {
        formInputLabel,
        new OnOrAfterQuarterYearHint(formInputLabel, onOrAfterQuarterYear, quarterYearInputToValidateAgainstLabel)
    };

    validateAndAssertNoErrors(
        quarterYearToValidate,
        validationHints
    );
  }

  private void validateAndAssertExpectedErrors(QuarterYearInput quarterYearInputToValidate,
                                               Object[] validationHints,
                                               String expectedQuarterErrorCode,
                                               String expectedYearErrorCode,
                                               String expectedErrorMessage) {

    final var errors = getErrors(quarterYearInputToValidate, validationHints);

    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    final var quarterPrefix = QuarterYearInputValidator.QUARTER;
    final var yearPrefix = QuarterYearInputValidator.YEAR;

    assertThat(fieldErrors).containsExactly(
        entry(quarterPrefix, Set.of(expectedQuarterErrorCode)),
        entry(yearPrefix, Set.of(expectedYearErrorCode))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(quarterPrefix, Set.of(expectedErrorMessage)),
        entry(yearPrefix,  Set.of(""))
    );
  }

  private void validateAndAssertNoErrors(QuarterYearInput quarterYearInputToValidate,
                                         Object[] validationHints) {
    var errors = getErrors(quarterYearInputToValidate, validationHints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).isEmpty();
    assertThat(fieldErrorMessages).isEmpty();
  }

}