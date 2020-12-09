package uk.co.ogauthority.pathfinder.model.form.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RequestUpdateFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  private RequestUpdateFormValidator requestUpdateFormValidator;

  @Before
  public void setup() {
    requestUpdateFormValidator = new RequestUpdateFormValidator(dateInputValidator);

    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_whenEmptyDeadlineDate_thenValid() {
    var form = new RequestUpdateForm();
    form.setDeadlineDate(new ThreeFieldDateInput(null, null, null));

    var errors = getErrors(form);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartiallyEnteredDeadlineDate_thenInvalid() {
    var form = new RequestUpdateForm();
    form.setDeadlineDate(new ThreeFieldDateInput(2020, null, null));

    var errors = getErrors(form);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("deadlineDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("deadlineDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("deadlineDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("deadlineDate.day", Set.of(
            RequestUpdateValidationHint.DEADLINE_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)
        ),
        entry("deadlineDate.month", Set.of("")),
        entry("deadlineDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenPastDeadlineDate_thenInvalid() {
    var form = new RequestUpdateForm();
    form.setDeadlineDate(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));

    var errors = getErrors(form);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("deadlineDate.day", Set.of(DateInputValidator.DAY_AFTER_DATE_CODE)),
        entry("deadlineDate.month", Set.of(DateInputValidator.MONTH_AFTER_DATE_CODE)),
        entry("deadlineDate.year", Set.of(DateInputValidator.YEAR_AFTER_DATE_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("deadlineDate.day", Set.of(
            RequestUpdateValidationHint.DEADLINE_LABEL.getInitCappedLabel() + " must be after " + RequestUpdateValidationHint.DATE_ERROR_LABEL)
        ),
        entry("deadlineDate.month", Set.of("")),
        entry("deadlineDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenFutureDeadlineDate_thenValid() {
    var form = new RequestUpdateForm();
    form.setDeadlineDate(new ThreeFieldDateInput(LocalDate.now().plusMonths(1)));

    var errors = getErrors(form);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  private BindingResult getErrors(RequestUpdateForm form) {
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(requestUpdateFormValidator, form, errors, new RequestUpdateValidationHint());

    return errors;
  }
}
