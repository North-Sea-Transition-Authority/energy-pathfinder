package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class WorkPlanUpcomingTenderFormValidator implements SmartValidator {
  private final DateInputValidator dateInputValidator;

  @Autowired
  public  WorkPlanUpcomingTenderFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (WorkPlanUpcomingTenderForm) target;

    WorkPlanUpcomingTenderValidationHint workPlanUpcomingTenderValidationHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(WorkPlanUpcomingTenderValidationHint.class))
        .map(WorkPlanUpcomingTenderValidationHint.class::cast)
        .findFirst()
        .orElseThrow(
            () -> new ActionNotAllowedException("Expected WorkPlanUpcomingTenderValidationHint validation hint to be provided")
        );

    ValidationUtil.invokeNestedValidator(
        errors,
        dateInputValidator,
        "estimatedTenderDate",
        form.getEstimatedTenderDate(),
        workPlanUpcomingTenderValidationHint.getEstimatedTenderDateHint()
    );
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(WorkPlanUpcomingTenderForm.class);
  }
}