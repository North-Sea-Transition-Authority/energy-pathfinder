package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ProjectInformationFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;
  private final QuarterYearInputValidator quarterYearInputValidator;

  @Autowired
  public ProjectInformationFormValidator(DateInputValidator dateInputValidator,
                                         QuarterYearInputValidator quarterYearInputValidator) {
    this.dateInputValidator = dateInputValidator;
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
        .map(hint -> ((ProjectInformationValidationHint) hint))
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectInformationValidationHint validation hint to be provided")
        );

    var fieldStage = form.getFieldStage();

    if (fieldStage != null) {
      if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.DISCOVERY))) {
        validateFirstProductionDate(
            "discoveryFirstProductionDate",
            form.getDiscoveryFirstProductionDate(),
            projectInformationValidationHint,
            errors
        );
      } else if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.DEVELOPMENT))) {
        validateFirstProductionDate(
            "developmentFirstProductionDate",
            form.getDevelopmentFirstProductionDate(),
            projectInformationValidationHint,
            errors
        );
      } else if (BooleanUtils.isTrue(fieldStage.equals(FieldStage.DECOMMISSIONING))) {
        ValidationUtil.invokeNestedValidator(
            errors,
            quarterYearInputValidator,
            "decomWorkStartDate",
            form.getDecomWorkStartDate(),
            projectInformationValidationHint.getDecomWorkStartDateValidationHints()
        );

        if (hasProductionCessationDateBeenEntered(form.getProductionCessationDate())) {
          ValidationUtil.invokeNestedValidator(
              errors,
              dateInputValidator,
              "productionCessationDate",
              form.getProductionCessationDate(),
              projectInformationValidationHint.getProductionCessationDateValidationHints()
          );
        }
      }
    }
  }

  private boolean hasProductionCessationDateBeenEntered(ThreeFieldDateInput productionCessationDate) {
    return (
        productionCessationDate != null
            && (
              productionCessationDate.getDay() != null
              || productionCessationDate.getMonth() != null
              || productionCessationDate.getYear() != null
            ));
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
