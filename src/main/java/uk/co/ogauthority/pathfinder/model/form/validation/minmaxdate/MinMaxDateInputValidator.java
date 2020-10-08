package uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.EmptyMinMaxDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MinMaxYearLabelsHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@Component
public class MinMaxDateInputValidator implements SmartValidator {

  public static final String MIN_YEAR = "minYear";
  public static final String MAX_YEAR = "maxYear";
  public static final String MIN_YEAR_TEXT = "minimum";
  public static final String MAX_YEAR_TEXT = "maximum";
  public static final String DEFAULT_INPUT_LABEL_TEXT = "Minimum and maximum years";
  public static final String ENTER_BOTH_YEARS_ERROR = "%s requires %s %s and %s year";
  public static final String MIN_BEFORE_MAX_YEAR_ERROR = "%s's %s year must be before or the same as the %s year";
  public static final String MAX_YEAR_IN_FUTURE_ERROR = "%s's %s year must be in the future";

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var minMaxDateInput = (MinMaxDateInput) target;

    var inputLabel = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(FormInputLabel.class))
        .map(hint -> ((FormInputLabel) hint))
        .findFirst()
        .orElse(new FormInputLabel(DEFAULT_INPUT_LABEL_TEXT));

    var yearLabels = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(MinMaxYearLabelsHint.class))
        .map(hint -> ((MinMaxYearLabelsHint) hint))
        .findFirst()
        .orElse(new MinMaxYearLabelsHint(MIN_YEAR_TEXT, MAX_YEAR_TEXT));

    //Check validation hint
    Optional<EmptyMinMaxDateAcceptableHint> emptyMinMaxDateAcceptableHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(EmptyMinMaxDateAcceptableHint.class))
        .map(hint -> ((EmptyMinMaxDateAcceptableHint) hint))
        .findFirst();

    Optional<MaxYearMustBeInFutureHint> maxYearMustBeInFutureHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(MaxYearMustBeInFutureHint.class))
        .map(hint -> ((MaxYearMustBeInFutureHint) hint))
        .findFirst();

    //Can fill in either of the year fields (or none) if doing partial validation
    if (emptyMinMaxDateAcceptableHint.isPresent()) {
      if (minMaxDateInput.getMinYear() != null) {
        rejectIfInvalidYear(errors, MIN_YEAR, inputLabel, yearLabels.getMinYearLabel(), minMaxDateInput.getMinYear());
      }

      if (minMaxDateInput.getMaxYear() != null) {
        rejectIfInvalidYear(errors, MAX_YEAR, inputLabel, yearLabels.getMaxYearLabel(), minMaxDateInput.getMaxYear());
      }
    } else { //Full validation

      //check values present
      if (!bothDatesArePresent(minMaxDateInput)) {
        var field = minMaxDateInput.getMinYear() != null ? MAX_YEAR : MIN_YEAR;
        errors.rejectValue(
            field,
            getInvalidErrorCode(field),
            getBothYearsRequiredErrorMessage(inputLabel, yearLabels)
        );
      }

      if (bothDatesArePresent(minMaxDateInput)) {
        //Check both years are valid - Only add the error once
        if (isInvalidYear(minMaxDateInput.getMinYear())) {
          addInvalidYearError(errors, MIN_YEAR, inputLabel, yearLabels.getMinYearLabel());
        } else if (isInvalidYear(minMaxDateInput.getMaxYear()) && !isInvalidYear(minMaxDateInput.getMinYear())) {
          addInvalidYearError(errors, MAX_YEAR, inputLabel, yearLabels.getMaxYearLabel());
        }
      }
    }
    //do for both partial and full
    //check min year is before max year
    if (bothDatesArePresentAndValid(minMaxDateInput) && !minMaxDateInput.minIsBeforeOrEqualToMax()) {
      errors.rejectValue(
          MIN_YEAR,
          getInvalidErrorCode(MIN_YEAR),
          getMinYearBeforeMaxYearErrorMessage(inputLabel, yearLabels)
      );
    }


    //check max year is in future
    if (maxYearMustBeInFutureHint.isPresent()
        &&
        (minMaxDateInput.getMaxYear() != null && !minMaxDateInput.maxYearIsInFuture())
    ) {
      errors.rejectValue(
          MAX_YEAR,
          getInvalidErrorCode(MAX_YEAR),
          getMaxYearMustBeInFutureErrorMessage(inputLabel, yearLabels)
      );
    }


  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(MinMaxDateInput.class);
  }

  private boolean bothDatesArePresent(MinMaxDateInput minMaxDateInput) {
    return minMaxDateInput.getMinYear() != null && minMaxDateInput.getMaxYear() != null;
  }

  private boolean bothDatesArePresentAndValid(MinMaxDateInput minMaxDateInput) {
    return bothDatesArePresent(minMaxDateInput)
            &&
          (!isInvalidYear(minMaxDateInput.getMinYear()) && !isInvalidYear(minMaxDateInput.getMaxYear()));
  }

  private String getBothYearsRequiredErrorMessage(FormInputLabel inputLabel, MinMaxYearLabelsHint yearLabels) {
    return String.format(
        ENTER_BOTH_YEARS_ERROR,
        inputLabel.getInitCappedLabel(),
        StringDisplayUtil.getPrefixForVowelOrConsonant(yearLabels.getMinYearLabel()),
        yearLabels.getMinYearLabel(),
        yearLabels.getMaxYearLabel()
    );
  }

  private String getMinYearBeforeMaxYearErrorMessage(FormInputLabel inputLabel, MinMaxYearLabelsHint yearLabels) {
    return String.format(
        MIN_BEFORE_MAX_YEAR_ERROR,
        inputLabel.getInitCappedLabel(),
        yearLabels.getMinYearLabel(),
        yearLabels.getMaxYearLabel()
    );
  }

  private String getMaxYearMustBeInFutureErrorMessage(FormInputLabel inputLabel, MinMaxYearLabelsHint yearLabels) {
    return String.format(MAX_YEAR_IN_FUTURE_ERROR, inputLabel.getInitCappedLabel(), yearLabels.getMaxYearLabel());
  }

  private String getInvalidErrorCode(String prefix) {
    return prefix + FieldValidationErrorCodes.INVALID.getCode();
  }

  private void rejectIfInvalidYear(Errors errors, String field, FormInputLabel inputLabel, String minOrMax, String year) {
    if (isInvalidYear(year)) {
      addInvalidYearError(
          errors,
          field,
          inputLabel,
          minOrMax
      );
    }
  }

  private void addInvalidYearError(Errors errors, String field, FormInputLabel inputLabel, String minOrMax) {
    errors.rejectValue(
        field,
        getInvalidErrorCode(field),
        DateInputValidator.getIncorrectYearFormatErrorMessage(inputLabel.getInitCappedLabel() + " " + minOrMax)
    );
  }

  /**
   * Convert the year into an integer and check it has 4 digits.
   * @param year Year value as a string
   * @return true if the year cannot be turned into a number or if it does not have 4 digits
   */
  private boolean isInvalidYear(String year) {
    try {
      Integer.parseInt(year);
      return DateInputValidator.isYearInInvalidFormat(year);
    } catch (NumberFormatException e) {
      return true;
    }
  }

  public static void addEmptyMinMaxDateAcceptableHint(ValidationType validationType, List<Object> validationHints) {
    if (validationType.equals(ValidationType.PARTIAL)) {
      validationHints.add(new EmptyMinMaxDateAcceptableHint());
    }
  }
}
