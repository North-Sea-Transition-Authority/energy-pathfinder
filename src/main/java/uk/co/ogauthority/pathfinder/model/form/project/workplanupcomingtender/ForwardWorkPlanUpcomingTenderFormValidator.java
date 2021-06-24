package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ForwardWorkPlanUpcomingTenderFormValidator implements SmartValidator {

  protected static final String INVALID_CONTRACT_DURATION_PREFIX = "Enter the length of the contract in";
  protected static final String INVALID_CONTRACT_DURATION_ERROR_CODE = ".invalid";

  private final QuarterYearInputValidator quarterYearInputValidator;

  @Autowired
  public ForwardWorkPlanUpcomingTenderFormValidator(QuarterYearInputValidator quarterYearInputValidator) {
    this.quarterYearInputValidator = quarterYearInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (ForwardWorkPlanUpcomingTenderForm) target;

    ForwardWorkPlanUpcomingTenderValidationHint workPlanUpcomingTenderValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ForwardWorkPlanUpcomingTenderValidationHint.class))
        .map(ForwardWorkPlanUpcomingTenderValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected WorkPlanUpcomingTenderValidationHint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        quarterYearInputValidator,
        "estimatedTenderStartDate",
        form.getEstimatedTenderStartDate(),
        workPlanUpcomingTenderValidationHint.getEstimatedTenderDateHint()
    );

    final var contractTermDurationPeriod = form.getContractTermDurationPeriod();

    if (
        contractTermDurationPeriod != null
        && isFullValidation(workPlanUpcomingTenderValidationHint.getValidationType())
    ) {

      if (BooleanUtils.isTrue(DurationPeriod.DAYS.equals(contractTermDurationPeriod))) {
        validateContractTermDuration(
            errors,
            "contractTermDayDuration",
            DurationPeriod.DAYS.getDisplayNamePlural().toLowerCase()
        );
      } else if (BooleanUtils.isTrue(DurationPeriod.WEEKS.equals(contractTermDurationPeriod))) {
        validateContractTermDuration(
            errors,
            "contractTermWeekDuration",
            DurationPeriod.WEEKS.getDisplayNamePlural().toLowerCase()
        );
      } else if (BooleanUtils.isTrue(DurationPeriod.MONTHS.equals(contractTermDurationPeriod))) {
        validateContractTermDuration(
            errors,
            "contractTermMonthDuration",
            DurationPeriod.MONTHS.getDisplayNamePlural().toLowerCase()
        );
      } else if (BooleanUtils.isTrue(DurationPeriod.YEARS.equals(contractTermDurationPeriod))) {
        validateContractTermDuration(
            errors,
            "contractTermYearDuration",
            DurationPeriod.YEARS.getDisplayNamePlural().toLowerCase()
        );
      }
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ForwardWorkPlanUpcomingTenderForm.class);
  }

  private boolean isFullValidation(ValidationType validationType) {
    return ValidationType.FULL.equals(validationType);
  }

  private void validateContractTermDuration(
      Errors errors,
      String fieldToValidate,
      String errorMessagePeriodPrefix
  ) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        fieldToValidate,
        String.format("%s%s", fieldToValidate, INVALID_CONTRACT_DURATION_ERROR_CODE),
        String.format("%s %s", INVALID_CONTRACT_DURATION_PREFIX, errorMessagePeriodPrefix)
    );
  }
}