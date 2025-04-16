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

  public static final String CARBON_CAPTURE_AND_STORAGE_FIELD = "carbonCaptureSubCategory";
  public static final String CARBON_CAPTURE_AND_STORAGE_MISSING_ERROR =
      String.format("Select a %s category", FieldStage.CARBON_CAPTURE_AND_STORAGE.getDisplayName());

  public static final String HYDROGEN_FIELD = "hydrogenSubCategory";
  public static final String HYDROGEN_MISSING_ERROR =
      String.format("Select a %s category", FieldStage.HYDROGEN.getDisplayName().toLowerCase());

  public static final String ELECTRIFICATION_FIELD = "electrificationSubCategory";
  public static final String ELECTRIFICATION_MISSING_ERROR =
      String.format("Select an %s category", FieldStage.ELECTRIFICATION.getDisplayName().toLowerCase());

  public static final String WIND_ENERGY_FIELD = "windEnergySubCategory";
  public static final String WIND_ENERGY_MISSING_ERROR =
      String.format("Select a %s category", FieldStage.WIND_ENERGY.getDisplayName().toLowerCase());

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

    var projectInformationValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectInformationValidationHint.class))
        .map(ProjectInformationValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectInformationValidationHint validation hint to be provided")
        );

    validateFieldStage(form, errors, projectInformationValidationHint);
  }

  private void validateFieldStage(ProjectInformationForm form,
                                  Errors errors,
                                  ProjectInformationValidationHint projectInformationValidationHint) {
    var fieldStage = form.getFieldStage();

    if (fieldStage != null) {
      if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.DEVELOPMENT))) {
        validateFirstProductionDate(
            form.getDevelopmentFirstProductionDate(),
            projectInformationValidationHint,
            errors
        );
      } else if (ValidationType.FULL.equals(projectInformationValidationHint.getValidationType())) {
        if (FieldStage.CARBON_CAPTURE_AND_STORAGE.equals(fieldStage)) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              CARBON_CAPTURE_AND_STORAGE_FIELD,
              CARBON_CAPTURE_AND_STORAGE_FIELD.concat(".required"),
              CARBON_CAPTURE_AND_STORAGE_MISSING_ERROR
          );
        } else if (FieldStage.HYDROGEN.equals(fieldStage)) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              HYDROGEN_FIELD,
              HYDROGEN_FIELD.concat(".required"),
              HYDROGEN_MISSING_ERROR
          );
        } else if (FieldStage.ELECTRIFICATION.equals(fieldStage)) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              ELECTRIFICATION_FIELD,
              ELECTRIFICATION_FIELD.concat(".required"),
              ELECTRIFICATION_MISSING_ERROR
          );
        } else if (FieldStage.WIND_ENERGY.equals(fieldStage)) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              WIND_ENERGY_FIELD,
              WIND_ENERGY_FIELD.concat(".required"),
              WIND_ENERGY_MISSING_ERROR
          );
        }
      }
    }
  }

  private void validateFirstProductionDate(QuarterYearInput firstProductionDate,
                                           ProjectInformationValidationHint projectInformationValidationHint,
                                           Errors errors) {
    ValidationUtil.invokeNestedValidator(
        errors,
        quarterYearInputValidator,
        "developmentFirstProductionDate",
        firstProductionDate,
        projectInformationValidationHint.getFirstProductionDateValidationHints()
    );
  }
}
