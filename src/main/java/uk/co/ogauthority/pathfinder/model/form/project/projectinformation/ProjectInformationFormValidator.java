package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ProjectInformationFormValidator implements SmartValidator {

  public static final String MISSING_FIELD_STAGE_CATEGORY_ERROR = "Select a category";
  public static final String INVALID_FIELD_STAGE_CATEGORY_ERROR = "The category selected is not valid for the field stage";

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
    var fieldStagesWithStageCategories = FieldStageSubCategory.getAllFieldStagesWithSubCategories();

    if (fieldStage != null) {
      if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.DEVELOPMENT))) {
        validateFirstProductionDate(
            form.getDevelopmentFirstProductionDate(),
            projectInformationValidationHint,
            errors
        );
      } else if (
          fieldStagesWithStageCategories.contains(fieldStage)
              && ValidationType.FULL.equals(projectInformationValidationHint.getValidationType())
      ) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "fieldStageSubCategory",
            "fieldStageSubCategory.required",
            MISSING_FIELD_STAGE_CATEGORY_ERROR
        );

        var fieldStageSubCategory = form.getFieldStageSubCategory();
        if (Objects.nonNull(fieldStageSubCategory) && !fieldStageSubCategory.getFieldStage().equals(fieldStage)) {
          errors.rejectValue(
              "fieldStageSubCategory",
              "fieldStageSubCategory.invalid",
              INVALID_FIELD_STAGE_CATEGORY_ERROR
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
