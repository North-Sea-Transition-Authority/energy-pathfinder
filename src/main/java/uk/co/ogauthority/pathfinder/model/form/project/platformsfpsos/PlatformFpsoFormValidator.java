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
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class PlatformFpsoFormValidator implements SmartValidator {

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
        .map(hint -> ((PlatformFpsoValidationHint) hint))
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected PlatformFpsoValidationHint to be provided")
        );

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

      if (platformFpsoValidationHint.getValidationType().equals(ValidationType.FULL)) {
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
