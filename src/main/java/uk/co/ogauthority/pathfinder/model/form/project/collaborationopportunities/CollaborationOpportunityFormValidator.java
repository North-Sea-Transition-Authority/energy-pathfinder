package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

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
public class CollaborationOpportunityFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;

  @Autowired
  public CollaborationOpportunityFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (CollaborationOpportunityForm) target;

    CollaborationOpportunityValidationHint collaborationOpportunityValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(CollaborationOpportunityValidationHint.class))
        .map(hint -> ((CollaborationOpportunityValidationHint) hint))
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected CollaborationOpportunityValidationHint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        dateInputValidator,
        "estimatedServiceDate",
        form.getEstimatedServiceDate(),
        collaborationOpportunityValidationHint.getEstimatedServiceDateHints()
    );

    FileUploadUtil.validateMaxFileLimit(
        form,
        errors,
        collaborationOpportunityValidationHint.getFileUploadLimit(),
        CollaborationOpportunityValidationHint.TOO_MANY_FILES_ERROR_MESSAGE
    );
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(CollaborationOpportunityForm.class);
  }
}
