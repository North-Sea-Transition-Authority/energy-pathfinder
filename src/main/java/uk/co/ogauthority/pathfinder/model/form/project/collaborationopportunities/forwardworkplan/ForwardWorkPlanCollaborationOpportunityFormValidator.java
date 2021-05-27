package uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan;

import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityValidationHintCommon;
import uk.co.ogauthority.pathfinder.util.file.FileUploadUtil;

@Component
public class ForwardWorkPlanCollaborationOpportunityFormValidator implements SmartValidator {

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    final var form = (ForwardWorkPlanCollaborationOpportunityForm) target;

    ForwardWorkPlanCollaborationOpportunityValidationHint collaborationOpportunityValidationHint =
        Arrays.stream(validationHints)
            .filter(hint -> hint.getClass().equals(ForwardWorkPlanCollaborationOpportunityValidationHint.class))
            .map(ForwardWorkPlanCollaborationOpportunityValidationHint.class::cast)
            .findFirst()
            .orElseThrow(
                () -> new ActionNotAllowedException("Expected ForwardWorkPlanCollaborationOpportunityValidationHint to be provided")
            );

    FileUploadUtil.validateMaxFileLimit(
        form,
        errors,
        collaborationOpportunityValidationHint.getFileUploadLimit(),
        CollaborationOpportunityValidationHintCommon.TOO_MANY_FILES_ERROR_MESSAGE
    );
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ForwardWorkPlanCollaborationOpportunityForm.class);
  }
}
