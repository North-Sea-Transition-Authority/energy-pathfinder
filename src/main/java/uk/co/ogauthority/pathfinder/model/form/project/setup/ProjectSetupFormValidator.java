package uk.co.ogauthority.pathfinder.model.form.project.setup;

import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;

@Component
public class ProjectSetupFormValidator implements SmartValidator {
  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    ProjectSetupFormValidationHint validationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectSetupFormValidationHint.class))
        .map(ProjectSetupFormValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectSetupFormValidationHint to be provided")
        );

    if (validationHint.decomValidationRequired()) {
      //reject missing values for decom sections
      ValidationUtils.rejectIfEmpty(errors, "wellsIncluded", "wellsIncluded.invalid",
          ProjectSetupFormValidationHint.WELLS_REQUIRED_TEXT
      );
      ValidationUtils.rejectIfEmpty(errors, "platformsFpsosIncluded", "platformsFpsosIncluded.invalid",
          ProjectSetupFormValidationHint.PLATFORMS_FPSOS_REQUIRED_TEXT
      );
      ValidationUtils.rejectIfEmpty(errors, "subseaInfrastructureIncluded", "subseaInfrastructureIncluded.invalid",
          ProjectSetupFormValidationHint.SUBSEA_INFRASTRUCTURE_REQUIRED_TEXT
      );
      ValidationUtils.rejectIfEmpty(errors, "integratedRigsIncluded", "integratedRigsIncluded.invalid",
          ProjectSetupFormValidationHint.INTEGRATED_RIGS_REQUIRED_TEXT
      );
      ValidationUtils.rejectIfEmpty(errors, "pipelinesIncluded", "pipelinesIncluded.invalid",
          ProjectSetupFormValidationHint.PIPELINES_REQUIRED_TEXT
      );
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectSetupForm.class);
  }
}
