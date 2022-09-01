package uk.co.ogauthority.pathfinder.model.form.project.commissionedwell;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class CommissionedWellFormValidator implements SmartValidator {

  static final String NO_WELLS_SELECTED_ERROR_MESSAGE = "Select at least one well to be commissioned";

  private final MinMaxDateInputValidator minMaxDateInputValidator;

  @Autowired
  public CommissionedWellFormValidator(MinMaxDateInputValidator minMaxDateInputValidator) {
    this.minMaxDateInputValidator = minMaxDateInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(CommissionedWellForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var form = (CommissionedWellForm) target;

    var commissionedWellValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(CommissionedWellValidatorHint.class))
        .map(CommissionedWellValidatorHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException(
                "Expected CommissionedWellValidatorHint to be provided"
            )
        );

    if (ValidationType.FULL.equals(commissionedWellValidationHint.getValidationType()) && form.getWells().isEmpty()) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "wellSelected",
          "wellSelected.invalid",
          NO_WELLS_SELECTED_ERROR_MESSAGE
      );
    }

    ValidationUtil.invokeNestedValidator(
        errors,
        minMaxDateInputValidator,
        "commissioningSchedule",
        form.getCommissioningSchedule(),
        commissionedWellValidationHint.getCommissioningScheduleHints()
    );

  }
}
