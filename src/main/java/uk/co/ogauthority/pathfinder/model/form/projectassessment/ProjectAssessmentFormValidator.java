package uk.co.ogauthority.pathfinder.model.form.projectassessment;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;

@Component
public class ProjectAssessmentFormValidator implements SmartValidator {

  public static final String CANNOT_PROVIDE_UPDATE_REQUIRED_ERROR
      = "Cannot request an update when an update is already in progress";

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (ProjectAssessmentForm) target;

    ProjectAssessmentValidationHint projectAssessmentValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectAssessmentValidationHint.class))
        .map(ProjectAssessmentValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectAssessmentValidationHint to be provided")
        );

    if (BooleanUtils.isTrue(form.getReadyToBePublished())) {
      if (projectAssessmentValidationHint.isCanRequestUpdate()) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "updateRequired",
            "updateRequired.invalid",
            String.format(
                "Select yes if the %s requires an update",
                projectAssessmentValidationHint.getProjectType().getLowercaseDisplayName()
            )
        );
      } else {
        if (form.getUpdateRequired() != null) {
          errors.rejectValue(
              "updateRequired",
              "updateRequired.invalid",
              CANNOT_PROVIDE_UPDATE_REQUIRED_ERROR
          );
        }
      }
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectAssessmentForm.class);
  }
}
