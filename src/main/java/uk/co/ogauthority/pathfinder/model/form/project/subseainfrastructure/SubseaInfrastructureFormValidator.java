package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class SubseaInfrastructureFormValidator implements SmartValidator {

  private final MinMaxDateInputValidator minMaxDateInputValidator;

  @Autowired
  public SubseaInfrastructureFormValidator(MinMaxDateInputValidator minMaxDateInputValidator) {
    this.minMaxDateInputValidator = minMaxDateInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(SubseaInfrastructureForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var form = (SubseaInfrastructureForm) target;

    var subseaInfrastructureValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(SubseaInfrastructureValidationHint.class))
        .map(SubseaInfrastructureValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected SubseaInfrastructureValidationHint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        minMaxDateInputValidator,
        "decommissioningDate",
        form.getDecommissioningDate(),
        subseaInfrastructureValidationHint.getDecommissioningDateHints()
    );
  }
}
