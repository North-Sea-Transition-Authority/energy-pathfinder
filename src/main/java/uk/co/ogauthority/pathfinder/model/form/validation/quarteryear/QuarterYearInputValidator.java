package uk.co.ogauthority.pathfinder.model.form.validation.quarteryear;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.EmptyQuarterYearAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.OnOrAfterQuarterYearHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class QuarterYearInputValidator implements SmartValidator {

  protected static final String QUARTER = "quarter";
  protected static final String YEAR = "year";
  public static final String QUARTER_INVALID_CODE = QUARTER + FieldValidationErrorCodes.INVALID.getCode();
  public static final String YEAR_INVALID_CODE = YEAR + FieldValidationErrorCodes.INVALID.getCode();
  public static final String EMPTY_QUARTER_YEAR_ERROR = "Enter %s ";
  public static final String VALID_QUARTER_YEAR_ERROR = "%s must have a valid quarter and year";
  public static final String QUARTER_ON_OR_AFTER_DATE_CODE = QUARTER + OnOrAfterQuarterYearHint.ON_OR_AFTER_QUARTER_YEAR_CODE;
  public static final String YEAR_ON_OR_AFTER_DATE_CODE = YEAR + OnOrAfterQuarterYearHint.ON_OR_AFTER_QUARTER_YEAR_CODE;

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(QuarterYearInput.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var quarterYearInput = (QuarterYearInput) target;

    var validationHintList = ValidationUtil.extractImplementedValidationHints(validationHints);
    var formInputLabel = ValidationUtil.extractFormInputLabelFromHints(validationHints);
    var inputLabel = formInputLabel.getLabel();

    var isEmptyQuarterYearAcceptable = validationHintList
        .stream()
        .filter(hint -> hint.getClass().equals(EmptyQuarterYearAcceptableHint.class))
        .map(EmptyQuarterYearAcceptableHint.class::cast)
        .findFirst()
        .isPresent();

    if (!isEmptyQuarterYearAcceptable && isQuarterYearInputEmpty(quarterYearInput)) {

      var errorMessage = String.format(
          EMPTY_QUARTER_YEAR_ERROR,
          StringDisplayUtil.getPrefixForVowelOrConsonant(inputLabel) + inputLabel
      );

      addQuarterYearErrors(errors, QUARTER_INVALID_CODE, YEAR_INVALID_CODE, errorMessage);

    } else if (!isQuarterYearInputEmpty(quarterYearInput)) {

      if (quarterYearInput.create().isEmpty()) {
        addQuarterYearErrors(
            errors,
            QUARTER_INVALID_CODE,
            YEAR_INVALID_CODE,
            String.format(VALID_QUARTER_YEAR_ERROR, formInputLabel.getInitCappedLabel())
        );
      } else {

        ValidationUtil.extractImplementedValidationHints(validationHints)
            .stream()
            .map(ValidationHint.class::cast)
            .filter(hint -> !hint.isValid(quarterYearInput))
            .forEach(hint ->
                addQuarterYearErrors(
                    errors,
                    QUARTER + hint.getCode(),
                    YEAR + hint.getCode(),
                    hint.getErrorMessage()
                )
            );

        if (DateInputValidator.isYearInInvalidFormat(quarterYearInput.getYear())) {

          var errorCode = FieldValidationErrorCodes.MIN_LENGTH_NOT_MET.getCode();

          addQuarterYearErrors(
              errors,
              QUARTER + errorCode,
              YEAR + errorCode,
              DateInputValidator.getIncorrectYearFormatErrorMessage(formInputLabel)
          );
        }
      }
    }
  }

  public static void addEmptyQuarterYearAcceptableHint(ValidationType validationType, List<Object> validationHints) {
    if (validationType.equals(ValidationType.PARTIAL)) {
      validationHints.add(new EmptyQuarterYearAcceptableHint());
    }
  }

  private boolean isQuarterYearInputEmpty(QuarterYearInput quarterYearInput) {
    return (quarterYearInput.getQuarter() == null && quarterYearInput.getYear() == null);
  }

  private void addQuarterYearErrors(Errors errors, String quarterCode, String yearCode, String errorMessage) {
    errors.rejectValue(QUARTER, quarterCode, errorMessage);
    errors.rejectValue(YEAR, yearCode, "");
  }
}
