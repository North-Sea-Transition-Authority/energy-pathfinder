package uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class PlatformFpsoFormValidator implements SmartValidator {

  public static final String MISSING_PLATFORM_ERROR = "Select a platform";
  public static final String MISSING_FPSO_ERROR = "Select an FPSO";
  public static final String MISSING_FPSO_TYPE_ERROR = "Enter an FPSO type";
  public static final String MISSING_FPSO_DIMENSIONS_ERROR = "Enter the FPSO dimensions";
  public static final String MISSING_SUBSTRUCTURE_REMOVAL_MASS_ERROR = "Enter a substructure removal mass";
  public static final String MISSING_SUBSTRUCTURE_REMOVAL_PREMISE_ERROR = "Enter a substructure removal premise";
  private final MinMaxDateInputValidator minMaxDateInputValidator;

  @Autowired
  public PlatformFpsoFormValidator(MinMaxDateInputValidator minMaxDateInputValidator) {
    this.minMaxDateInputValidator = minMaxDateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (PlatformFpsoForm) target;

    var platformFpsoValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(PlatformFpsoValidationHint.class))
        .map(PlatformFpsoValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected PlatformFpsoValidationHint to be provided")
        );

    final var validationType = platformFpsoValidationHint.getValidationType();

    var infrastructureType = form.getInfrastructureType();

    if (PlatformFpsoInfrastructureType.PLATFORM.equals(infrastructureType)) {

      if (ValidationType.FULL.equals(validationType)) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "platformStructure",
            "platformStructure.invalid",
            MISSING_PLATFORM_ERROR
        );
      }
    } else if (PlatformFpsoInfrastructureType.FPSO.equals(infrastructureType)) {

      if (ValidationType.FULL.equals(validationType)) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "fpsoStructure",
            "fpsoStructure.invalid",
            MISSING_FPSO_ERROR
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "fpsoType",
            "fpsoType.invalid",
            MISSING_FPSO_TYPE_ERROR
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "fpsoDimensions",
            "fpsoDimensions.invalid",
            MISSING_FPSO_DIMENSIONS_ERROR
        );
      }
    }

    ValidationUtil.invokeNestedValidator(
        errors,
        minMaxDateInputValidator,
        "topsideRemovalYears",
        form.getTopsideRemovalYears(),
        platformFpsoValidationHint.getTopsidesRemovalHints()
    );


    if (BooleanUtils.isTrue(form.getSubstructureExpectedToBeRemoved())) {
      ValidationUtil.invokeNestedValidator(
          errors,
          minMaxDateInputValidator,
          "substructureRemovalYears",
          form.getSubstructureRemovalYears(),
          platformFpsoValidationHint.getSubstructureRemovalHints()
      );

      if (ValidationType.FULL.equals(validationType)) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "substructureRemovalMass",
            "substructureRemovalMass.invalid",
            MISSING_SUBSTRUCTURE_REMOVAL_MASS_ERROR
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "substructureRemovalPremise",
            "substructureRemovalPremise.invalid",
            MISSING_SUBSTRUCTURE_REMOVAL_PREMISE_ERROR
        );
      }
    }
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(PlatformFpsoForm.class);
  }
}
