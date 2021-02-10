package uk.co.ogauthority.pathfinder.model.form.project.decommissionedwell;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class DecommissionedWellFormValidator implements SmartValidator {

  private final QuarterYearInputValidator quarterYearInputValidator;

  @Autowired
  public DecommissionedWellFormValidator(QuarterYearInputValidator quarterYearInputValidator) {
    this.quarterYearInputValidator = quarterYearInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(DecommissionedWellForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var form = (DecommissionedWellForm) target;

    var decommissionedWellValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(DecommissionedWellValidationHint.class))
        .map(DecommissionedWellValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException(
                "Expected DecommissionedWellValidationHint to be provided"
            )
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        quarterYearInputValidator,
        "plugAbandonmentDate",
        form.getPlugAbandonmentDate(),
        decommissionedWellValidationHint.getPlugAbandonmentDateHints()
    );
  }
}
