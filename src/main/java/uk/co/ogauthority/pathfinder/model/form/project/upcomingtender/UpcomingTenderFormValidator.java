package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.AfterDateHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.util.validation.ValidationUtil;

@Component
public class UpcomingTenderFormValidator implements SmartValidator {

  public static final FormInputLabel ESTIMATED_TENDER_LABEL = new FormInputLabel("estimated tender date");
  public static final String DATE_ERROR_LABEL = "today's date";
  private final DateInputValidator dateInputValidator;

  @Autowired
  public UpcomingTenderFormValidator(DateInputValidator dateInputValidator) {
    this.dateInputValidator = dateInputValidator;
  }

  @Override
  public void validate(Object target, Errors errors) {
    validate(target, errors, new Object[0]);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (UpcomingTenderForm) target;
    Optional<EmptyDateAcceptableHint> emptyDateAcceptableHint = Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(EmptyDateAcceptableHint.class))
        .map(hint -> ((EmptyDateAcceptableHint) hint))
        .findFirst();

    //always ensure the date is in the future
    var dateHint = new AfterDateHint(LocalDate.now(), DATE_ERROR_LABEL);

    ValidationUtil.invokeNestedValidator(
        errors,
        dateInputValidator,
        "estimatedTenderDate",
        form.getEstimatedTenderDate(),
        ESTIMATED_TENDER_LABEL,
        emptyDateAcceptableHint.isPresent() ? emptyDateAcceptableHint.get() : new Object[0],
        dateHint
    );
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(UpcomingTenderForm.class);
  }
}
