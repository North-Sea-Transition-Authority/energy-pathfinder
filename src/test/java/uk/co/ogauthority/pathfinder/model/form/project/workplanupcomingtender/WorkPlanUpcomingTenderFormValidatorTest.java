package uk.co.ogauthority.pathfinder.model.form.project.workplanupcomingtender;


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
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  private WorkPlanUpcomingTenderFormValidator validator;

  @Before
  public void setUp() {
    validator = new WorkPlanUpcomingTenderFormValidator(dateInputValidator);
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new WorkPlanUpcomingTenderValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_isValid_withEmptyDate() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(null));
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new WorkPlanUpcomingTenderValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_isInValid_withPastDate() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().minusDays(1L)));
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new WorkPlanUpcomingTenderValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    checkCommonFieldErrorsAndMessages(errors);
  }

  @Test
  public void validate_completeForm_dateIsNotInFuture_isInvalid() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().minusDays(1L)));
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new WorkPlanUpcomingTenderValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);
    checkCommonFieldErrorsAndMessages(errors);
  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoUpcomingTenderValidationHint_thenException() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    ValidationUtils.invokeValidator(validator, form, errors);
  }

  private void checkCommonFieldErrorsAndMessages(BeanPropertyBindingResult errors) {
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
    assertThat(fieldErrors).contains(
        entry("estimatedTenderDate.day", Set.of(DateInputValidator.DAY_AFTER_DATE_CODE)),
        entry("estimatedTenderDate.month", Set.of(DateInputValidator.MONTH_AFTER_DATE_CODE)),
        entry("estimatedTenderDate.year", Set.of(DateInputValidator.YEAR_AFTER_DATE_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("estimatedTenderDate.day", Set.of(
            WorkPlanUpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getInitCappedLabel() + " must be after " + WorkPlanUpcomingTenderValidationHint.DATE_ERROR_LABEL)
        ),
        entry("estimatedTenderDate.month", Set.of("")),
        entry("estimatedTenderDate.year", Set.of(""))
    );
  }
}