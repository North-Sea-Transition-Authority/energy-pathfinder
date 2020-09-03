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
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.BeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrAfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.OnOrBeforeDateHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

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

  public static final String DAY_AFTER_DATE_CODE = DAY + FieldValidationErrorCodes.AFTER_SOME_DATE.getCode();
  public static final String MONTH_AFTER_DATE_CODE = MONTH + FieldValidationErrorCodes.AFTER_SOME_DATE.getCode();
  public static final String YEAR_AFTER_DATE_CODE = YEAR + FieldValidationErrorCodes.AFTER_SOME_DATE.getCode();

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

    // should be be small list of hints so this repeated looping over whole list is probably harmless
    var inputLabel = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(FormInputLabel.class))
        .map(hint -> ((FormInputLabel) hint))
        .findFirst()
        .orElse(new FormInputLabel(DEFAULT_INPUT_LABEL_TEXT));

    var dateInput = (DateInput) o;

    Optional<OnOrBeforeDateHint> onOrBeforeDateHint = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(OnOrBeforeDateHint.class))
        .map(hint -> ((OnOrBeforeDateHint) hint))
        .findFirst();

    Optional<BeforeDateHint> beforeDateHint = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(BeforeDateHint.class))
        .map(hint -> ((BeforeDateHint) hint))
        .findFirst();

    Optional<OnOrAfterDateHint> onOrAfterDateHint = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(OnOrAfterDateHint.class))
        .map(hint -> ((OnOrAfterDateHint) hint))
        .findFirst();

    Optional<AfterDateHint> afterDateHint = Arrays.stream(objects)
        .filter(hint -> hint.getClass().equals(AfterDateHint.class))
        .map(hint -> ((AfterDateHint) hint))
        .findFirst();

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
            inputLabel.getLabel() + VALID_DATE_ERROR
        );
      } else {
        // only do additional validation when the date is valid
        afterDateHint.ifPresent(hint -> validateAfterDate(errors, dateInput, inputLabel, hint));
        beforeDateHint.ifPresent(hint -> validateBeforeDate(errors, dateInput, inputLabel, hint));
        onOrAfterDateHint.ifPresent(hint -> validateOnOrAfterDate(errors, dateInput, inputLabel, hint));
        onOrBeforeDateHint.ifPresent(hint -> validateOnOrBeforeDate(errors, dateInput, inputLabel, hint));
        validateHasFourCharYear(errors, dateInput, inputLabel);
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

  // There must be a cleaner way than this to avoid adding new methods per hint type.
  // Maybe put the check on the date hints them selves and loop over any that exist?
  // Revisit if any more get added
  // Things to look at, moving error code onto hint, moving message format string to hint,
  // can we move check itself to hint without having to add explicit input object classes to the generic date hints?
  // ^would that be better or worse than what we have? ie treating hints as validation strategies
  private void validateOnOrAfterDate(Errors errors,
                                     DateInput dateInput,
                                     FormInputLabel inputLabel,
                                     OnOrAfterDateHint testOnOrAfterDate) {
    if (!(dateInput.isAfter(testOnOrAfterDate.getDate()) || dateInput.isEqualTo(testOnOrAfterDate.getDate()))) {
      var afterDateLabel = testOnOrAfterDate.getDateLabel();

      addDateErrors(
          errors,
          dateInput,
          DAY_AFTER_DATE_CODE,
          MONTH_AFTER_DATE_CODE,
          YEAR_AFTER_DATE_CODE,
          inputLabel.getLabel() + " must be the same as or after " + afterDateLabel
      );
    }
  }

  private void validateOnOrBeforeDate(Errors errors,
                                      DateInput dateInput,
                                      FormInputLabel inputLabel,
                                      OnOrBeforeDateHint testOnOrBeforeDate) {
    if (!(dateInput.isBefore(testOnOrBeforeDate.getDate()) || dateInput.isEqualTo(testOnOrBeforeDate.getDate()))) {
      var beforeDateLabel = testOnOrBeforeDate.getDateLabel();

      addDateErrors(
          errors,
          dateInput,
          DAY_BEFORE_DATE_CODE,
          MONTH_BEFORE_DATE_CODE,
          YEAR_BEFORE_DATE_CODE,
          inputLabel.getLabel() + " must be the same as or before " + beforeDateLabel
      );
    }
  }

  private void validateAfterDate(Errors errors,
                                 DateInput dateInput,
                                 FormInputLabel inputLabel,
                                 AfterDateHint testAfterDate) {
    if (!dateInput.isAfter(testAfterDate.getDate())) {
      var afterDateLabel = testAfterDate.getDateLabel();

      addDateErrors(
          errors,
          dateInput,
          DAY_AFTER_DATE_CODE,
          MONTH_AFTER_DATE_CODE,
          YEAR_AFTER_DATE_CODE,
          inputLabel.getLabel() + " must be after " + afterDateLabel
      );
    }
  }

  private void validateBeforeDate(Errors errors,
                                  DateInput dateInput,
                                  FormInputLabel inputLabel,
                                  BeforeDateHint beforeDateHint) {
    if (!dateInput.isBefore(beforeDateHint.getDate())) {
      var beforeDateLabel = beforeDateHint.getDateLabel();

      addDateErrors(
          errors,
          dateInput,
          DAY_BEFORE_DATE_CODE,
          MONTH_BEFORE_DATE_CODE,
          YEAR_BEFORE_DATE_CODE,
          inputLabel.getLabel() + " must be before " + beforeDateLabel
      );
    }
  }

  private void validateHasFourCharYear(Errors errors,
                                       DateInput dateInput,
                                       FormInputLabel inputLabel) {
    if (dateInput.getYear().length() != 4) {

      var errorCode = FieldValidationErrorCodes.MIN_LENGTH_NOT_MET.getCode();

      addDateErrors(
          errors,
          dateInput,
          DAY + errorCode,
          MONTH + errorCode,
          YEAR + errorCode,
          inputLabel.getLabel() + " must have a four character year"
      );
    }
  }

}
