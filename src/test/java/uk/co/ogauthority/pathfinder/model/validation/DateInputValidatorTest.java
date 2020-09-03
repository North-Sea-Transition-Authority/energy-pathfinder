package uk.co.ogauthority.pathfinder.model.validation;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.time.LocalDate;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.BeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrAfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrBeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.TwoFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class DateInputValidatorTest {

  private DateInputValidator validator;
  private TwoFieldDateInput twoFieldDateInput;
  private ThreeFieldDateInput threeFieldDateInput;

  @Before
  public void setup() {
    validator = new DateInputValidator();
    twoFieldDateInput = new TwoFieldDateInput();
    threeFieldDateInput = new ThreeFieldDateInput();
  }

  @Test
  public void validate_twoFieldWithNoHints_invalid_emptyDate() {
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, new Object[0]);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of(String.format(DateInputValidator.EMPTY_DATE_ERROR, "a "+ DateInputValidator.DEFAULT_INPUT_LABEL_TEXT))),
        entry("year",  Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldWithNoHints_invalid_emptyDate() {
    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, new Object[0]);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.invalid")),
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("day", Set.of(String.format(DateInputValidator.EMPTY_DATE_ERROR, "a "+ DateInputValidator.DEFAULT_INPUT_LABEL_TEXT))),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldWithNoHints_invalid_date() {
    twoFieldDateInput = new TwoFieldDateInput(13, -1);
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, new Object[0]);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of(DateInputValidator.DEFAULT_INPUT_LABEL_TEXT + DateInputValidator.VALID_DATE_ERROR)),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldWithNoHints_invalid_date() {
    threeFieldDateInput = new ThreeFieldDateInput(13, -1, 1);
    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, new Object[0]);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.invalid")),
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("day", Set.of(DateInputValidator.DEFAULT_INPUT_LABEL_TEXT + DateInputValidator.VALID_DATE_ERROR)),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldWithInputLabelHint_invalidDate_emptyDate() {
    var label = new FormInputLabel("Work start date");
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {label};
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);


    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of(String.format(DateInputValidator.EMPTY_DATE_ERROR, "a "+label.getLabel()))),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldWithInputLabelHint_invalidDate_emptyDate() {
    var label = new FormInputLabel("Work start date");
    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {label};
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);


    assertThat(fieldErrorMessages).containsExactly(
        entry("day", Set.of(String.format(DateInputValidator.EMPTY_DATE_ERROR, "a "+label.getLabel()))),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldWithInputLabelHint_emptyDate_emptyDateAcceptableHint() {
    var label = new FormInputLabel("Work start date");
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {label, new EmptyDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_threeFieldWithInputLabelHint_emptyDate_emptyDateAcceptableHint() {
    var label = new FormInputLabel("Work start date");
    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {label, new EmptyDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_twoFieldWithInputLabelHint_invalidDate_invalidDate() {
    var label = new FormInputLabel("Work start date");
    twoFieldDateInput = new TwoFieldDateInput(-1, 22);
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {label};
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);


    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of(label.getLabel() + DateInputValidator.VALID_DATE_ERROR)),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldWithInputLabelHint_invalidDate_invalidDate() {
    var label = new FormInputLabel("Work start date");
    threeFieldDateInput = new ThreeFieldDateInput(-1, 22, 1);
    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {label};
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);


    assertThat(fieldErrorMessages).containsExactly(
        entry("day", Set.of(label.getLabel() + DateInputValidator.VALID_DATE_ERROR)),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldWithInputLabelHint_beforeDateHint_afterDateHint_invalidDate() {
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };

    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    // as date is invalid do not do additional validation
    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );
  }

  @Test
  public void validate_threeFieldWithInputLabelHint_beforeDateHint_afterDateHint_invalidDate() {
    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };

    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    // as date is invalid do not do additional validation
    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.invalid")),
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );
  }

  @Test
  public void validate_twoFieldValidDate() {
    twoFieldDateInput.setMonth(6);
    twoFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };

    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_threeFieldValidDate() {
    threeFieldDateInput.setDay(1);
    threeFieldDateInput.setMonth(6);
    threeFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };

    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_twoFieldAfterDateFail() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2019);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.afterDate")),
        entry("year", Set.of("year.afterDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("month", Set.of("Some date must be after After date")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldAfterDateFail() {
    threeFieldDateInput.setDay(1);
    threeFieldDateInput.setMonth(12);
    threeFieldDateInput.setYear(2019);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.afterDate")),
        entry("month", Set.of("month.afterDate")),
        entry("year", Set.of("year.afterDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("day", Set.of("Some date must be after After date")),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldDateBeforeDateFail() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2021);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.beforeDate")),
        entry("year", Set.of("year.beforeDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("month", Set.of("Some date must be before Before date")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldDateBeforeDateFail() {
    threeFieldDateInput.setDay(1);
    threeFieldDateInput.setMonth(12);
    threeFieldDateInput.setYear(2021);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new BeforeDateHint(LocalDate.of(2020, 12, 31), "Before date"),
        new AfterDateHint(LocalDate.of(2020, 1, 1), "After date")

    };
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.beforeDate")),
        entry("month", Set.of("month.beforeDate")),
        entry("year", Set.of("year.beforeDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("day", Set.of("Some date must be before Before date")),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldDateOnOrBeforeDateFail() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2021);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrBeforeDateHint(LocalDate.of(2020, 12, 31), "On/Before date"),

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.beforeDate")),
        entry("year", Set.of("year.beforeDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("month", Set.of("Some date must be the same as or before On/Before date")),
        entry("year",  Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldDateOnOrBeforeDateFail() {
    threeFieldDateInput.setDay(1);
    threeFieldDateInput.setMonth(12);
    threeFieldDateInput.setYear(2021);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrBeforeDateHint(LocalDate.of(2020, 12, 31), "On/Before date"),

    };
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.beforeDate")),
        entry("month", Set.of("month.beforeDate")),
        entry("year", Set.of("year.beforeDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("day", Set.of("Some date must be the same as or before On/Before date")),
        entry("month",  Set.of("")),
        entry("year",  Set.of(""))
    );
  }

  @Test
  public void validate_twoFieldOnOrBeforeDatePass_sameMonth() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrBeforeDateHint(LocalDate.of(2020, 12, 31), "On/Before date"),

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();

  }

  @Test
  public void validate_threeFieldOnOrBeforeDatePass_sameDate() {
    threeFieldDateInput.setDay(31);
    threeFieldDateInput.setMonth(12);
    threeFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrBeforeDateHint(LocalDate.of(2020, 12, 31), "On/Before date"),

    };
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();

  }

  @Test
  public void validate_twoFieldOnOrAfterDateFail() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrAfterDateHint(LocalDate.of(2021, 12, 31), "On/After date"),

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.afterDate")),
        entry("year", Set.of("year.afterDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("month", Set.of("Some date must be the same as or after On/After date")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldOnOrAfterDateFail() {
    threeFieldDateInput.setDay(31);
    threeFieldDateInput.setMonth(12);
    threeFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrAfterDateHint(LocalDate.of(2021, 12, 31), "On/After date"),

    };
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day.afterDate")),
        entry("month", Set.of("month.afterDate")),
        entry("year", Set.of("year.afterDate"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        // case preserved. up to callers to make sure they provide a good hint
        entry("day", Set.of("Some date must be the same as or after On/After date")),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validate_threeFieldDateOnOrAfterDatePass_sameDate() {
    threeFieldDateInput.setDay(31);
    threeFieldDateInput.setMonth(12);
    threeFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrAfterDateHint(LocalDate.of(2020, 12, 31), "On/After date"),

    };
    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_twoFieldDateOnOrAfterDatePass_sameMonth() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrAfterDateHint(LocalDate.of(2020, 12, 31), "On/After date"),

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validateHasFourCharYearHint_whenThreeFieldAndNotFourCharYear_thenFail() {

    threeFieldDateInput.setDay("01");
    threeFieldDateInput.setMonth("01");
    final var INVALID_YEAR = "20";
    threeFieldDateInput.setYear(INVALID_YEAR);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date")
    };

    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    var errorCode = FieldValidationErrorCodes.MIN_LENGTH_NOT_MET.getCode();

    assertThat(fieldErrors).containsExactly(
        entry("day", Set.of("day" + errorCode)),
        entry("month", Set.of("month" + errorCode)),
        entry("year", Set.of("year" + errorCode))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("day", Set.of("Some date must have a four character year")),
        entry("month", Set.of("")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validateHasFourCharYearHint_whenThreeFieldAndFourCharYear_thenPass() {
    threeFieldDateInput.setDay("01");
    threeFieldDateInput.setMonth("01");
    final var VALID_YEAR = "2020";
    threeFieldDateInput.setYear(VALID_YEAR);

    var errors = new BeanPropertyBindingResult(threeFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date")
    };

    ValidationUtils.invokeValidator(validator, threeFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validateHasFourCharYearHint_whenTwoFieldAndNotFourCharYear_thenFail() {

    twoFieldDateInput.setMonth("01");
    final var INVALID_YEAR = "20";
    twoFieldDateInput.setYear(INVALID_YEAR);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date")
    };

    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    var errorCode = FieldValidationErrorCodes.MIN_LENGTH_NOT_MET.getCode();

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month" + errorCode)),
        entry("year", Set.of("year" + errorCode))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of("Some date must have a four character year")),
        entry("year", Set.of(""))
    );
  }

  @Test
  public void validateHasFourCharYearHint_whenTwoFieldAndFourCharYear_thenPass() {
    twoFieldDateInput.setMonth("01");
    final var VALID_YEAR = "2020";
    twoFieldDateInput.setYear(VALID_YEAR);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date")
    };

    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

}