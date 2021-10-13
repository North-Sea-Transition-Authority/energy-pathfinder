package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ProjectInformationFormValidator implements SmartValidator {

  public static final String MISSING_ENERGY_TRANSITION_CATEGORY_ERROR = "Select an energy transition category";

  private final QuarterYearInputValidator quarterYearInputValidator;

  @Autowired
  public ProjectInformationFormValidator(QuarterYearInputValidator quarterYearInputValidator) {
    this.quarterYearInputValidator = quarterYearInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectInformationForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var form = (ProjectInformationForm) target;

    ProjectInformationValidationHint projectInformationValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectInformationValidationHint.class))
        .map(ProjectInformationValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectInformationValidationHint validation hint to be provided")
        );

    var fieldStage = form.getFieldStage();

    if (fieldStage != null) {
      if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.DEVELOPMENT))) {
        validateFirstProductionDate(
            "developmentFirstProductionDate",
            form.getDevelopmentFirstProductionDate(),
            projectInformationValidationHint,
            errors
        );
      } else if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.ENERGY_TRANSITION))) {
        if (ValidationType.FULL.equals(projectInformationValidationHint.getValidationType())) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              "energyTransitionCategory",
              "energyTransitionCategory.invalid",
              MISSING_ENERGY_TRANSITION_CATEGORY_ERROR
          );
        }
      }
    }
  }

  private void validateFirstProductionDate(String formTarget,
                                           QuarterYearInput firstProductionDate,
                                           ProjectInformationValidationHint projectInformationValidationHint,
                                           Errors errors) {
    ValidationUtil.invokeNestedValidator(
        errors,
        quarterYearInputValidator,
        formTarget,
        firstProductionDate,
        projectInformationValidationHint.getFirstProductionDateValidationHints()
    );
  }
}
