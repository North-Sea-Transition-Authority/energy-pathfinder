package uk.co.ogauthority.pathfinder.model.form.project.location;

import java.util.Arrays;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.fivium.formlibrary.input.CoordinateInputLatitudeHemisphere;
import uk.co.fivium.formlibrary.validator.coordinate.CoordinateInputValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.service.devuk.DevUkFieldService;
import uk.co.ogauthority.pathfinder.service.project.location.LicenceBlockValidatorService;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ProjectLocationFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;
  private final LicenceBlockValidatorService licenceBlockValidatorService;
  private final DevUkFieldService devUkFieldService;

  public static final String INVALID_FIELD_ERROR_CODE = "field" + FieldValidationErrorCodes.INVALID;
  public static final String INVALID_FIELD_ERROR_MSG = "Select a seaward field";

  @Autowired
  public ProjectLocationFormValidator(DateInputValidator dateInputValidator,
                                      LicenceBlockValidatorService licenceBlockValidatorService,
                                      DevUkFieldService devUkFieldService) {
    this.dateInputValidator = dateInputValidator;
    this.licenceBlockValidatorService = licenceBlockValidatorService;
    this.devUkFieldService = devUkFieldService;
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

    var validationType = projectLocationValidationHint.getValidationType();

    var centreOfInterestLatitudeValidator = CoordinateInputValidator.builder()
        .mustBeBetween(45, 0, 0, 64, 59, 59.999);
    if (validationType == ValidationType.PARTIAL) {
      centreOfInterestLatitudeValidator.isOptional();
    }
    var centreOfInterestLatitude = form.getCentreOfInterestLatitude();
    // The only option for latitude hemisphere is north, and it is not part of the form so we need to set it manually to pass the
    // validation. We only set it if no inputs are blank to allow partial saving.
    if (StringUtils.isNoneBlank(
        centreOfInterestLatitude.getDegreesInput().getInputValue(),
        centreOfInterestLatitude.getMinutesInput().getInputValue(),
        centreOfInterestLatitude.getSecondsInput().getInputValue()
    )) {
      centreOfInterestLatitude.getHemisphereInput().setInputValue(CoordinateInputLatitudeHemisphere.NORTH.name());
    }
    centreOfInterestLatitudeValidator.validate(centreOfInterestLatitude, errors);

    var centreOfInterestLongitudeValidator = CoordinateInputValidator.builder()
        .mustBeBetween(0, 0, 0, 30, 59, 59.999);
    if (validationType == ValidationType.PARTIAL) {
      centreOfInterestLongitudeValidator.isOptional();
    }
    centreOfInterestLongitudeValidator.validate(form.getCentreOfInterestLongitude(), errors);

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

    if (StringUtils.isNotBlank(form.getField())) {
      var field = devUkFieldService.findById(Integer.parseInt(form.getField()));
      if (field.isEmpty() || !field.get().isActive() || field.get().isLandward()) {
        errors.rejectValue("field", INVALID_FIELD_ERROR_CODE, INVALID_FIELD_ERROR_MSG);
      }
    }

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
