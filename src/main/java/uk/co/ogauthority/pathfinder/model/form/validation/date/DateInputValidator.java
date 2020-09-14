package uk.co.ogauthority.pathfinder.model.form.validation.date;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.DateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.DateInputType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class DateInputValidator implements SmartValidator {

  private static final String DAY = "day";
  private static final String MONTH = "month";
  private static final String YEAR = "year";
  public static final String DEFAULT_INPUT_LABEL_TEXT = "Date";
  public static final String VALID_DATE_ERROR = " must be a valid date";
  public static final String EMPTY_DATE_ERROR = "Enter %s ";

  public static final String DAY_INVALID_CODE = DAY + FieldValidationErrorCodes.INVALID.getCode();
  public static final String MONTH_INVALID_CODE = MONTH + FieldValidationErrorCodes.INVALID.getCode();
  public static final String YEAR_INVALID_CODE = YEAR + FieldValidationErrorCodes.INVALID.getCode();

  public static final String DAY_AFTER_DATE_CODE = DAY + AfterDateHint.AFTER_DATE_CODE;
  public static final String MONTH_AFTER_DATE_CODE = MONTH + AfterDateHint.AFTER_DATE_CODE;
  public static final String YEAR_AFTER_DATE_CODE = YEAR + AfterDateHint.AFTER_DATE_CODE;

  public static final String DAY_BEFORE_DATE_CODE = DAY + FieldValidationErrorCodes.BEFORE_SOME_DATE.getCode();
  public static final String MONTH_BEFORE_DATE_CODE = MONTH + FieldValidationErrorCodes.BEFORE_SOME_DATE.getCode();
  public static final String YEAR_BEFORE_DATE_CODE = YEAR + FieldValidationErrorCodes.BEFORE_SOME_DATE.getCode();

  @Override
  public boolean supports(Class<?> clazz) {
    return Arrays.stream(clazz.getInterfaces()).anyMatch(Predicate.isEqual(DateInput.class));
  }

  @Override
  public void validate(Object o, Errors errors) {
    validate(o, errors, new Object[0]);
  }

  @Override
  public void validate(Object o, Errors errors, Object... objects) {

    var inputLabel = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(FormInputLabel.class))
        .map(hint -> ((FormInputLabel) hint))
        .findFirst()
        .orElse(new FormInputLabel(DEFAULT_INPUT_LABEL_TEXT));

    var dateInput = (DateInput) o;

    Optional<EmptyDateAcceptableHint> emptyDateAcceptableHint = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(EmptyDateAcceptableHint.class))
        .map(hint -> ((EmptyDateAcceptableHint) hint))
        .findFirst();

    //If there's no EmptyDateAcceptableHint then check the date exists
    if (emptyDateAcceptableHint.isEmpty() && isDateInputEmpty(dateInput)) {
      addDateErrors(
          errors,
          dateInput,
          DAY_INVALID_CODE,
          MONTH_INVALID_CODE,
          YEAR_INVALID_CODE,
          String.format(EMPTY_DATE_ERROR, StringDisplayUtil.getPrefixForVowelOrConsonant(inputLabel.getLabel()) +
              inputLabel.getLabel())
      );
      //If a date exists check it meets all requirements.
    } else if (!isDateInputEmpty(dateInput)) {
      if (dateInput.createDate().isEmpty()) {
        addDateErrors(
            errors,
            dateInput,
            DAY_INVALID_CODE,
            MONTH_INVALID_CODE,
            YEAR_INVALID_CODE,
            inputLabel.getInitCappedLabel() + VALID_DATE_ERROR
        );
      } else {
        // only do additional validation when the date is valid
        ValidationUtil.extractImplementedValidationHints(objects)
            .stream()
            .map(hint -> (ValidationHint) hint)
            .filter(hint -> !hint.isValid(dateInput))
            .forEach(hint ->
                addDateErrors(
                    errors,
                    dateInput,
                    DAY + hint.getCode(),
                    MONTH + hint.getCode(),
                    YEAR + hint.getCode(),
                    hint.getErrorMessage()
                )
          );

        validateHasFourNumberYear(errors, dateInput, inputLabel);
      }
    }
  }

  private boolean isThreeFieldDateInput(DateInput dateInput) {
    return dateInput.getType().equals(DateInputType.THREE_FIELD);
  }

  private boolean isDateInputEmpty(DateInput dateInput) {
    boolean isValid;

    if (isThreeFieldDateInput(dateInput)) {
      isValid = (dateInput.getDay() == null && dateInput.getMonth() == null  && dateInput.getYear() == null);
    } else {
      isValid = (dateInput.getMonth() == null && dateInput.getYear() == null);
    }
    return isValid;
  }

  private void addDateErrors(Errors errors,
                               DateInput dateInput,
                               String dayCode,
                               String monthCode,
                               String yearCode,
                               String errorMessage) {
    // Add error message to the first input depending on the type
    // of date input we are processing
    if (isThreeFieldDateInput(dateInput)) {
      errors.rejectValue(DAY, dayCode, errorMessage);
      errors.rejectValue(MONTH, monthCode, "");
    } else {
      errors.rejectValue(MONTH, monthCode, errorMessage);
    }
    errors.rejectValue(YEAR, yearCode, "");
  }

  private void validateHasFourNumberYear(Errors errors,
                                         DateInput dateInput,
                                         FormInputLabel inputLabel) {
    if (isYearInInvalidFormat(dateInput.getYear())) {

      var errorCode = FieldValidationErrorCodes.MIN_LENGTH_NOT_MET.getCode();

      addDateErrors(
          errors,
          dateInput,
          DAY + errorCode,
          MONTH + errorCode,
          YEAR + errorCode,
          getIncorrectYearFormatErrorMessage(inputLabel)
      );
    }
  }

  public static boolean isYearInInvalidFormat(String year) {
    return (year.length() != 4);
  }

  public static String getIncorrectYearFormatErrorMessage(FormInputLabel inputLabel) {
    return inputLabel.getInitCappedLabel() + " must have a four number year. For example 2020";
  }

}
