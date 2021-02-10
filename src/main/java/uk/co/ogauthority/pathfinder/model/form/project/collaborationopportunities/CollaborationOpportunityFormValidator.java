package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities;

import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.util.file.FileUploadUtil;

@Component
public class CollaborationOpportunityFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (CollaborationOpportunityForm) target;

    CollaborationOpportunityValidationHint collaborationOpportunityValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(CollaborationOpportunityValidationHint.class))
        .map(CollaborationOpportunityValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected CollaborationOpportunityValidationHint to be provided")
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
