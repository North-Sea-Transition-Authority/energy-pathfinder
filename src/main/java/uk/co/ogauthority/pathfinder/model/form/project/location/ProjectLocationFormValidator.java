package uk.co.ogauthority.pathfinder.model.form.project.location;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.service.project.location.LicenceBlockValidatorService;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ProjectLocationFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;
  private final LicenceBlockValidatorService licenceBlockValidatorService;

  @Autowired
  public ProjectLocationFormValidator(DateInputValidator dateInputValidator,
                                      LicenceBlockValidatorService licenceBlockValidatorService) {
    this.dateInputValidator = dateInputValidator;
    this.licenceBlockValidatorService = licenceBlockValidatorService;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (ProjectLocationForm) target;

    ProjectLocationValidationHint projectLocationValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(ProjectLocationValidationHint.class))
        .map(ProjectLocationValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected ProjectLocationValidationHint validation hint to be provided")
        );

    if (BooleanUtils.isTrue(form.getApprovedFieldDevelopmentPlan())) {
      ValidationUtil.invokeNestedValidator(
          errors,
          dateInputValidator,
          "approvedFdpDate",
          form.getApprovedFdpDate(),
          projectLocationValidationHint.getFdpApprovalDateValidationHints()
      );
    }

    if (BooleanUtils.isTrue(form.getApprovedDecomProgram())) {
      ValidationUtil.invokeNestedValidator(
          errors,
          dateInputValidator,
          "approvedDecomProgramDate",
          form.getApprovedDecomProgramDate(),
          projectLocationValidationHint.getDecomProgramApprovalDateValidationHints()
      );
    }

    //validate selected blocks exist in portal data
    licenceBlockValidatorService.addErrorsForInvalidBlocks(form.getLicenceBlocks(), errors, "licenceBlocksSelect");

  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(ProjectLocationForm.class);
  }
}
