package uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class PlugAbandonmentScheduleFormValidator implements SmartValidator {

  private final MinMaxDateInputValidator minMaxDateInputValidator;

  @Autowired
  public PlugAbandonmentScheduleFormValidator(MinMaxDateInputValidator minMaxDateInputValidator) {
    this.minMaxDateInputValidator = minMaxDateInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(PlugAbandonmentScheduleForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var form = (PlugAbandonmentScheduleForm) target;

    var plugAbandonmentScheduleValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(PlugAbandonmentScheduleValidationHint.class))
        .map(PlugAbandonmentScheduleValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException(
                "Expected PlugAbandonmentScheduleValidationHint to be provided"
            )
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        minMaxDateInputValidator,
        "plugAbandonmentDate",
        form.getPlugAbandonmentDate(),
        plugAbandonmentScheduleValidationHint.getPlugAbandonmentDateHints()
    );
  }
}
