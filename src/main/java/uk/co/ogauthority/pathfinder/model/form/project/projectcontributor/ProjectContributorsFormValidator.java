package uk.co.ogauthority.pathfinder.model.form.project.projectcontributor;

import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;

@Component
public class ProjectContributorsFormValidator implements SmartValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return ProjectContributorsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var projectContributorValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectContributorValidationHint.class))
        .map(ProjectContributorValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException(
                "Expected ProjectContributorValidationHint to be provided"
            )
        );

    ProjectContributorsForm form = (ProjectContributorsForm) target;
    if (projectContributorValidationHint.getValidationType().equals(ValidationType.FULL)
        && form.getContributors().isEmpty()) {
      errors.rejectValue(
          "contributorsSelect",
          "contributorsSelect.notEmpty",
          "You must select at least one operator"
      );
    }
  }
}
