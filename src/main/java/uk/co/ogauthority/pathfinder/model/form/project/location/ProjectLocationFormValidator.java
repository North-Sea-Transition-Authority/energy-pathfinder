package uk.co.ogauthority.pathfinder.model.form.project.location;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.validation.twofielddate.TwoFieldDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class ProjectLocationFormValidator implements SmartValidator {

  public static final FormInputLabel APPROVED_FDP_LABEL = new FormInputLabel("Approved Field Development Plan date");
  public static final FormInputLabel APPROVED_DECOM_LABEL = new FormInputLabel("Approved Decommissioning Program date");
  private final TwoFieldDateInputValidator twoFieldDateInputValidator;

  @Autowired
  public ProjectLocationFormValidator(TwoFieldDateInputValidator twoFieldDateInputValidator) {
    this.twoFieldDateInputValidator = twoFieldDateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (ProjectLocationForm) target;
    Optional<EmptyDateAcceptableHint> emptyDateAcceptableHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(EmptyDateAcceptableHint.class))
        .map(hint -> ((EmptyDateAcceptableHint) hint))
        .findFirst();

    if (BooleanUtils.isTrue(form.getApprovedFieldDevelopmentPlan())) {
      ValidationUtil.invokeNestedValidator(
          errors,
          twoFieldDateInputValidator,
          "approvedFdpDate",
          form.getApprovedFdpDate(),
          APPROVED_FDP_LABEL,
          emptyDateAcceptableHint.isPresent() ? emptyDateAcceptableHint.get() : new Object[0]
      );
    }

    if (BooleanUtils.isTrue(form.getApprovedDecomProgram())) {
      ValidationUtil.invokeNestedValidator(
          errors,
          twoFieldDateInputValidator,
          "approvedDecomProgramDate",
          form.getApprovedDecomProgramDate(),
          APPROVED_DECOM_LABEL,
          emptyDateAcceptableHint.isPresent() ? emptyDateAcceptableHint.get() : new Object[0]
      );
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
