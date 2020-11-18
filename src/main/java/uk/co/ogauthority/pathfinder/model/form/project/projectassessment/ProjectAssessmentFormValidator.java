package uk.co.ogauthority.pathfinder.model.form.project.projectassessment;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

@Component
public class ProjectAssessmentFormValidator implements SmartValidator {

  public static final String MISSING_UPDATE_REQUIRED_ERROR = "Select if an update to the project is required";

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (ProjectAssessmentForm) target;

    if (BooleanUtils.isTrue(form.getReadyToBePublished())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "updateRequired",
          "updateRequired.invalid",
          MISSING_UPDATE_REQUIRED_ERROR
      );
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
