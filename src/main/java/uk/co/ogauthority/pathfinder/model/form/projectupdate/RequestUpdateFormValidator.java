package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class RequestUpdateFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;

  @Autowired
  public RequestUpdateFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (RequestUpdateForm) target;

    RequestUpdateValidationHint requestUpdateValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(RequestUpdateValidationHint.class))
        .map(RequestUpdateValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected RequestUpdateValidationHint validation hint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        dateInputValidator,
        "deadlineDate",
        form.getDeadlineDate(),
        requestUpdateValidationHint.getDeadlineDateHint()
    );
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(RequestUpdateForm.class);
  }
}
