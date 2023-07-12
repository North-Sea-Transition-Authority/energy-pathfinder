package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.infrastructure;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class InfrastructureAwardedContractFormValidator implements SmartValidator {

  private final DateInputValidator dateInputValidator;

  @Autowired
  public InfrastructureAwardedContractFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(AwardedContractForm.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {

    var form = (AwardedContractForm) target;

    var awardedContractValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(AwardedContractValidationHint.class))
        .map(AwardedContractValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected AwardedContractValidationHint validation hint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        dateInputValidator,
        "dateAwarded",
        form.getDateAwarded(),
        awardedContractValidationHint.getDateAwardedValidationHints()
    );
  }
}
