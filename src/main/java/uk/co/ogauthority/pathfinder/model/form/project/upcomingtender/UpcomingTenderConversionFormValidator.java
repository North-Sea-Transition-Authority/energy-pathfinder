package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Service
public class UpcomingTenderConversionFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;

  @Autowired
  public UpcomingTenderConversionFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(UpcomingTenderConversionForm.class);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors, @NonNull Object... validationHints) {
    var form = (UpcomingTenderConversionForm) target;

    var awardedContractValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(AwardedContractValidationHint.class))
        .map(AwardedContractValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected AwardedContractValidationHint validation hint to be provided")
        );

    ValidationUtils.rejectIfEmpty(
        errors,
        "dateAwarded",
        "dateAwarded.invalid",
        "Enter a date awarded"
    );

    if (!errors.hasFieldErrors("dateAwarded")) {
      ValidationUtil.invokeNestedValidator(
          errors,
          dateInputValidator,
          "dateAwarded",
          form.getDateAwarded(),
          awardedContractValidationHint.getDateAwardedValidationHints()
      );
    }
  }

  @Override
  public void validate(@NonNull Object target,@NonNull Errors errors) {
    validate(target, errors, (Object) null);
  }

}
