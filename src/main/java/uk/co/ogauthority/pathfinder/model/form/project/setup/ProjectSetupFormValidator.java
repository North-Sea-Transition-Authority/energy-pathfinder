package uk.co.ogauthority.pathfinder.model.form.project.setup;

import java.util.Arrays;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;

@Component
public class ProjectSetupFormValidator implements SmartValidator {

  static final String WELLS_REQUIRED_TEXT = "Select yes if you plan to add any wells to be decommissioned to your project";

  static final String PLATFORMS_FPSOS_REQUIRED_TEXT = "Select yes if you plan to add any platforms or " +
      "FPSOs to be decommissioned to your project";

  static final String SUBSEA_INFRASTRUCTURE_REQUIRED_TEXT = "Select yes if you plan to add any subsea " +
      "infrastructure to be decommissioned to your project";

  static final String INTEGRATED_RIGS_REQUIRED_TEXT = "Select yes if you plan to add any integrated rigs to your project";

  static final String PIPELINES_REQUIRED_TEXT = "Select yes if you plan to add any pipelines to be decommissioned to your project";

  static final String COMMISSIONED_WELLS_REQUIRED_TEXT = "Select yes if you plan to add any wells to be commissioned to your project";

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors, @NonNull Object... validationHints) {

    ProjectSetupFormValidationHint validationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectSetupFormValidationHint.class))
        .map(ProjectSetupFormValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectSetupFormValidationHint to be provided")
        );

    if (ValidationType.FULL.equals(validationHint.validationType()) && FieldStage.OIL_AND_GAS.equals(validationHint.fieldStage())) {

      if (FieldStageSubCategory.DECOMMISSIONING.equals(validationHint.fieldStageSubCategory())) {

        ValidationUtils.rejectIfEmpty(errors, "wellsIncluded", "wellsIncluded.invalid",
            WELLS_REQUIRED_TEXT
        );
        ValidationUtils.rejectIfEmpty(errors, "platformsFpsosIncluded", "platformsFpsosIncluded.invalid",
            PLATFORMS_FPSOS_REQUIRED_TEXT
        );

        ValidationUtils.rejectIfEmpty(errors, "subseaInfrastructureIncluded", "subseaInfrastructureIncluded.invalid",
            SUBSEA_INFRASTRUCTURE_REQUIRED_TEXT
        );

        ValidationUtils.rejectIfEmpty(errors, "integratedRigsIncluded", "integratedRigsIncluded.invalid",
            INTEGRATED_RIGS_REQUIRED_TEXT
        );

        ValidationUtils.rejectIfEmpty(errors, "pipelinesIncluded", "pipelinesIncluded.invalid",
            PIPELINES_REQUIRED_TEXT
        );

      } else if (FieldStageSubCategory.DISCOVERY.equals(validationHint.fieldStageSubCategory())) {
        validateCommissionedWellsInput(errors);
      } else if (FieldStageSubCategory.DEVELOPMENT.equals(validationHint.fieldStageSubCategory())) {
        validateCommissionedWellsInput(errors);
      }
    }
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectSetupForm.class);
  }

  private void validateCommissionedWellsInput(Errors errors) {
    ValidationUtils.rejectIfEmpty(
        errors,
        "commissionedWellsIncluded",
        "commissionedWellsIncluded.invalid",
        COMMISSIONED_WELLS_REQUIRED_TEXT
    );
  }
}
