package uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class DecommissionedPipelineFormValidator implements SmartValidator {

  private final MinMaxDateInputValidator minMaxDateInputValidator;

  @Autowired
  public DecommissionedPipelineFormValidator(MinMaxDateInputValidator minMaxDateInputValidator) {
    this.minMaxDateInputValidator = minMaxDateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (DecommissionedPipelineForm) target;

    var decommissionedPipelineValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(DecommissionedPipelineValidationHint.class))
        .map(hint -> ((DecommissionedPipelineValidationHint) hint))
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected DecommissionedPipelineValidationHint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        minMaxDateInputValidator,
        "decommissioningYears",
        form.getDecommissioningYears(),
        decommissionedPipelineValidationHint.getDecommissioningDateHints()
    );
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(DecommissionedPipelineForm.class);
  }
}
