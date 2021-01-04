package uk.co.ogauthority.pathfinder.model.validation;

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
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.EmptyMinMaxDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class MinMaxDateInputValidatorTest {
  private static final String LABEL_TEXT = "Label text";
  private MinMaxDateInputValidator validator;
  private MinMaxDateInput input;
  private final FormInputLabel inputLabel = new FormInputLabel(LABEL_TEXT);

  @Before
  public void setUp() {
    validator = new MinMaxDateInputValidator();
  }

  @Test
  public void partialValidationEmptyDates_valid() {
    input = new MinMaxDateInput();
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new EmptyMinMaxDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void partialValidationSingleDate_valid() {
    input = new MinMaxDateInput("2020", null);
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new EmptyMinMaxDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void partialValidationBothDates_valid() {
    input = new MinMaxDateInput("2020", "2021");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new EmptyMinMaxDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void partialValidationSingleInvalidDate_inValid() {
    input = new MinMaxDateInput("zzz", null);
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new EmptyMinMaxDateAcceptableHint()};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(DateInputValidator.getIncorrectYearFormatErrorMessage(
                inputLabel.getInitCappedLabel() + " " + MinMaxDateInputValidator.MIN_YEAR_TEXT)
            )
        )
    );
  }

  @Test
  public void partialValidationSingleInvalidDate_customLabel_inValid() {
    input = new MinMaxDateInput("zzz", null);
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new EmptyMinMaxDateAcceptableHint(), new MinMaxYearLabelsHint("earliest", "latest")};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(DateInputValidator.getIncorrectYearFormatErrorMessage(
                inputLabel.getInitCappedLabel() + " " + "earliest")
            )
        )
    );
  }

  @Test
  public void fullValidationEmptyDates_invalid() {
    input = new MinMaxDateInput();
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                inputLabel.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(MinMaxDateInputValidator.MIN_YEAR_TEXT),
                MinMaxDateInputValidator.MIN_YEAR_TEXT,
                MinMaxDateInputValidator.MAX_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationInvalidDates_invalid() {
    input = new MinMaxDateInput("zzz", "abc");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(DateInputValidator.getIncorrectYearFormatErrorMessage(
                inputLabel.getInitCappedLabel() + " " + MinMaxDateInputValidator.MIN_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationInvalidMinDate_invalid() {
    input = new MinMaxDateInput("zzz", "2021");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(DateInputValidator.getIncorrectYearFormatErrorMessage(
                inputLabel.getInitCappedLabel() + " " + MinMaxDateInputValidator.MIN_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationInvalidMaxDate_invalid() {
    input = new MinMaxDateInput("2020", "zzz");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MAX_YEAR, Set.of("maxYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MAX_YEAR,
            Set.of(DateInputValidator.getIncorrectYearFormatErrorMessage(
                inputLabel.getInitCappedLabel() + " " + MinMaxDateInputValidator.MAX_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationMissingMaxDate_errorOnMaxDate_invalid() {
    input = new MinMaxDateInput("2021", null);
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MAX_YEAR, Set.of("maxYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MAX_YEAR,
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                inputLabel.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(MinMaxDateInputValidator.MIN_YEAR_TEXT),
                MinMaxDateInputValidator.MIN_YEAR_TEXT,
                MinMaxDateInputValidator.MAX_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationMissingMinDate_errorOnMinDate_invalid() {
    input = new MinMaxDateInput(null, "2021");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                inputLabel.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(MinMaxDateInputValidator.MIN_YEAR_TEXT),
                MinMaxDateInputValidator.MIN_YEAR_TEXT,
                MinMaxDateInputValidator.MAX_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationInvalidDates_minAfterMax_invalid() {
    input = new MinMaxDateInput("2021", "2020");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MIN_YEAR, Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MIN_YEAR,
            Set.of(String.format(
                MinMaxDateInputValidator.MIN_BEFORE_MAX_YEAR_ERROR,
                inputLabel.getInitCappedLabel(),
                MinMaxDateInputValidator.MIN_YEAR_TEXT,
                MinMaxDateInputValidator.MAX_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationValidDates_maxDateNotInFutureWithHint_invalid() {
    input = new MinMaxDateInput("2019", String.valueOf(LocalDate.now().minusYears(1L).getYear()));
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new MaxYearMustBeInFutureHint(
        inputLabel,
        new MinMaxYearLabelsHint(MinMaxDateInputValidator.MIN_YEAR_TEXT, MinMaxDateInputValidator.MAX_YEAR_TEXT)
    )};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(MinMaxDateInputValidator.MAX_YEAR, Set.of("maxYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            MinMaxDateInputValidator.MAX_YEAR,
            Set.of(String.format(
                MaxYearMustBeInFutureHint.MAX_YEAR_IN_FUTURE_ERROR, inputLabel.getInitCappedLabel(),
                MinMaxDateInputValidator.MAX_YEAR_TEXT
            ))
        )
    );
  }

  @Test
  public void fullValidationValidDates_yearsAreTheSame_valid() {
    input = new MinMaxDateInput("2020", "2020");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void fullValidationValidDates_maxYearIsCurrentYear_maxYearInFutureValidationHint_valid() {
    input = new MinMaxDateInput("2020", String.valueOf(LocalDate.now().getYear()));
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel, new MaxYearMustBeInFutureHint(
        inputLabel,
        new MinMaxYearLabelsHint(MinMaxDateInputValidator.MIN_YEAR_TEXT, MinMaxDateInputValidator.MAX_YEAR_TEXT)
      )};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }


  @Test
  public void fullValidationBothDates_valid() {
    input = new MinMaxDateInput("2020", "2021");
    var errors = new BeanPropertyBindingResult(input, "form");
    Object[] hints = {inputLabel};
    ValidationUtils.invokeValidator(validator, input, errors, hints);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void addEmptyMinMaxDateAcceptableHint_whenPartial_thenEmptyMinMaxDateAcceptableHintAdded() {
    var hints = new ArrayList<>();
    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(ValidationType.PARTIAL, hints);
    assertThat(hints).hasSize(1);
    var hint = hints.get(0);
    assertThat(hint).isInstanceOf(EmptyMinMaxDateAcceptableHint.class);
  }

  @Test
  public void addEmptyMinMaxDateAcceptableHint_whenFull_thenEmptyMinMaxDateAcceptableHintNotAdded() {
    var hints = new ArrayList<>();
    MinMaxDateInputValidator.addEmptyMinMaxDateAcceptableHint(ValidationType.FULL, hints);
    assertThat(hints).isEmpty();
  }
}
