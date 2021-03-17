package uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class DecommissioningScheduleFormValidator implements SmartValidator {

  public static final String MISSING_DECOMMISSIONING_START_DATE_NOT_PROVIDED_REASON_ERROR =
      "Enter the reason why you are unable to provide the decommissioning start date";
  public static final String MISSING_CESSATION_OF_PRODUCTION_DATE_NOT_PROVIDED_REASON_ERROR =
      "Enter the reason why you are unable to provide the Cessation of Production date";

  private final DateInputValidator dateInputValidator;
  private final QuarterYearInputValidator quarterYearInputValidator;

  @Autowired
  public DecommissioningScheduleFormValidator(
      DateInputValidator dateInputValidator,
      QuarterYearInputValidator quarterYearInputValidator) {
    this.dateInputValidator = dateInputValidator;
    this.quarterYearInputValidator = quarterYearInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (DecommissioningScheduleForm) target;

    var decommissioningScheduleValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(DecommissioningScheduleValidationHint.class))
        .map(DecommissioningScheduleValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected DecommissioningScheduleValidationHint to be provided")
        );

    var decommissioningStartDateType = form.getDecommissioningStartDateType();
    if (DecommissioningStartDateType.EXACT.equals(decommissioningStartDateType)) {
      ValidationUtil.invokeNestedValidator(
          errors,
          dateInputValidator,
          "exactDecommissioningStartDate",
          form.getExactDecommissioningStartDate(),
          decommissioningScheduleValidationHint.getExactDecommissioningStartDateValidationHints()
      );
    } else if (DecommissioningStartDateType.ESTIMATED.equals(decommissioningStartDateType)) {
      ValidationUtil.invokeNestedValidator(
          errors,
          quarterYearInputValidator,
          "estimatedDecommissioningStartDate",
          form.getEstimatedDecommissioningStartDate(),
          decommissioningScheduleValidationHint.getEstimatedDecommissioningStartDateValidationHints()
      );
    } else if (DecommissioningStartDateType.UNKNOWN.equals(decommissioningStartDateType)
        && !ValidationType.PARTIAL.equals(decommissioningScheduleValidationHint.getValidationType())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "decommissioningStartDateNotProvidedReason",
          "decommissioningStartDateNotProvidedReason.invalid",
          MISSING_DECOMMISSIONING_START_DATE_NOT_PROVIDED_REASON_ERROR
      );
    }

    var cessationOfProductionDateType = form.getCessationOfProductionDateType();
    if (CessationOfProductionDateType.EXACT.equals(cessationOfProductionDateType)) {
      ValidationUtil.invokeNestedValidator(
          errors,
          dateInputValidator,
          "exactCessationOfProductionDate",
          form.getExactCessationOfProductionDate(),
          decommissioningScheduleValidationHint.getExactCessationOfProductionDateValidationHints()
      );
    } else if (CessationOfProductionDateType.ESTIMATED.equals(cessationOfProductionDateType)) {
      ValidationUtil.invokeNestedValidator(
          errors,
          quarterYearInputValidator,
          "estimatedCessationOfProductionDate",
          form.getEstimatedCessationOfProductionDate(),
          decommissioningScheduleValidationHint.getEstimatedCessationOfProductionDateValidationHints()
      );
    } else if (CessationOfProductionDateType.UNKNOWN.equals(cessationOfProductionDateType)
        && !ValidationType.PARTIAL.equals(decommissioningScheduleValidationHint.getValidationType())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "cessationOfProductionDateNotProvidedReason",
          "cessationOfProductionDateNotProvidedReason.invalid",
          MISSING_CESSATION_OF_PRODUCTION_DATE_NOT_PROVIDED_REASON_ERROR
      );
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(DecommissioningScheduleForm.class);
  }
}
