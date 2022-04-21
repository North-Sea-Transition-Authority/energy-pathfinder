package uk.co.ogauthority.pathfinder.model.form.project.workoplanprojectcontribution;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.projectcontributor.ProjectContributorsFormValidator;

@Component
public class ForwardWorkPlanProjectContributorsFormValidator implements SmartValidator {

  private final ProjectContributorsFormValidator projectContributorsFormValidator;

  @Autowired
  public ForwardWorkPlanProjectContributorsFormValidator(
      ProjectContributorsFormValidator projectContributorsFormValidator) {
    this.projectContributorsFormValidator = projectContributorsFormValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return ForwardWorkPlanProjectContributorsForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    ForwardWorkPlanProjectContributorsForm form = (ForwardWorkPlanProjectContributorsForm) target;
    var projectContributorValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectContributorValidationHint.class))
        .map(ProjectContributorValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException(
                "Expected ProjectContributorValidationHint to be provided"
            )
        );

    if (projectContributorValidationHint.getValidationType().equals(ValidationType.FULL)) {
      if (form.getHasProjectContributors() == null) {
        errors.rejectValue(
            "hasProjectContributors",
            "hasProjectContributors.notNull",
            "Select yes if you want to add any project contributors"
        );
      } else {
        if (form.getHasProjectContributors()) {
          projectContributorsFormValidator.validate(form, errors, validationHints);
        }
      }
    }
  }
}
