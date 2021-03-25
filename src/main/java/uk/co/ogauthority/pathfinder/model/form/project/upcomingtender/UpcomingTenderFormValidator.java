package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.file.FileUploadUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class UpcomingTenderFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;

  @Autowired
  public UpcomingTenderFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (UpcomingTenderForm) target;

    UpcomingTenderValidationHint upcomingTenderValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(UpcomingTenderValidationHint.class))
        .map(UpcomingTenderValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected UpcomingTenderValidationHint validation hint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        dateInputValidator,
        "estimatedTenderDate",
        form.getEstimatedTenderDate(),
        upcomingTenderValidationHint.getEstimatedTenderDateHint()
    );

    FileUploadUtil.validateMaxFileLimit(
        form,
        errors,
        upcomingTenderValidationHint.getFileUploadLimit(),
        UpcomingTenderValidationHint.TOO_MANY_FILES_ERROR_MESSAGE
    );
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(UpcomingTenderForm.class);
  }
}
