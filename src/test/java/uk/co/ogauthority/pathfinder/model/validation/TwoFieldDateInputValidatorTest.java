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
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.BeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.OnOrAfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.OnOrBeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.TwoFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.twofielddate.TwoFieldDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class TwoFieldDateInputValidatorTest {

  private TwoFieldDateInputValidator validator;
  private TwoFieldDateInput twoFieldDateInput;

  @Before
  public void setup() {
    validator = new TwoFieldDateInputValidator();
    twoFieldDateInput = new TwoFieldDateInput();
  }

  @Test
  public void validate_noHints_invalid_emptyDate() {
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, new Object[0]);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("month", Set.of("month.invalid")),
        entry("year", Set.of("year.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of("")),
        entry("year", Set.of(String.format(TwoFieldDateInputValidator.EMPTY_DATE_ERROR, "a "+ TwoFieldDateInputValidator.DEFAULT_INPUT_LABEL_TEXT)))
    );
  }

  @Test
  public void validate_noHints_invalid_date() {
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
        entry("month", Set.of("")),
        entry("year", Set.of(TwoFieldDateInputValidator.DEFAULT_INPUT_LABEL_TEXT + TwoFieldDateInputValidator.VALID_DATE_ERROR))
    );
  }

  @Test
  public void validate_inputLabelHint_invalidDate_emptyDate() {
    var label = new FormInputLabel("Work start date");
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {label};
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);


    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of("")),
        entry("year", Set.of(String.format(TwoFieldDateInputValidator.EMPTY_DATE_ERROR, "a "+label.getLabel())))
    );
  }

  @Test
  public void validate_inputLabelHint_emptyDate_emptyDateAcceptableHint() {
    var label = new FormInputLabel("Work start date");
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {label, new EmptyDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inputLabelHint_invalidDate_invalidDate() {
    var label = new FormInputLabel("Work start date");
    twoFieldDateInput = new TwoFieldDateInput(-1, 22);
    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {label};
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);


    assertThat(fieldErrorMessages).containsExactly(
        entry("month", Set.of("")),
        entry("year", Set.of(label.getLabel() + TwoFieldDateInputValidator.VALID_DATE_ERROR))
    );
  }

  @Test
  public void validate_inputLabelHint_beforeDateHint_afterDateHint_invalidDate() {
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
  public void validate_validDate() {
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
  public void validate_afterDateFail() {
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
        entry("month", Set.of("")),
        // case preserved. up to callers to make sure they provide a good hint
        entry("year", Set.of("Some date must be after After date"))
    );
  }

  @Test
  public void validate_beforeDateFail() {
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
        entry("month", Set.of("")),
        // case preserved. up to callers to make sure they provide a good hint
        entry("year", Set.of("Some date must be before Before date"))
    );
  }

  @Test
  public void validate_onOrBeforeDateFail() {
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
        entry("month", Set.of("")),
        // case preserved. up to callers to make sure they provide a good hint
        entry("year", Set.of("Some date must be the same as or before On/Before date"))
    );
  }

  @Test
  public void validate_onOrBeforeDatePass_sameMonth() {
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
  public void validate_onOrAfterDateFail() {
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
        entry("month", Set.of("")),
        // case preserved. up to callers to make sure they provide a good hint
        entry("year", Set.of("Some date must be the same as or after On/After date"))
    );
  }

  @Test
  public void validate_onOrAfterDatePass_sameMonth() {
    twoFieldDateInput.setMonth(12);
    twoFieldDateInput.setYear(2020);

    var errors = new BeanPropertyBindingResult(twoFieldDateInput, "form");
    Object[] hints = {
        new FormInputLabel("Some date"),
        new OnOrAfterDateHint(LocalDate.of(2020, 12, 31), "On/After date"),

    };
    ValidationUtils.invokeValidator(validator, twoFieldDateInput, errors, hints);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).isEmpty();
  }

}