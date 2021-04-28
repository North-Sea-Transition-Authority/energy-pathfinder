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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.exception.ActionNotAllowedException;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  private static final String INVALID_CONTRACT_DURATION_PREFIX = WorkPlanUpcomingTenderFormValidator.INVALID_CONTRACT_DURATION_PREFIX;
  private static final String INVALID_CONTRACT_DURATION_ERROR_CODE = WorkPlanUpcomingTenderFormValidator.INVALID_CONTRACT_DURATION_ERROR_CODE;

  private WorkPlanUpcomingTenderFormValidator validator;

  @Before
  public void setUp() {
    validator = new WorkPlanUpcomingTenderFormValidator(dateInputValidator);
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_whenFullValidationAndCompleteForm_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.FULL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test
  public void validate_whenPartialValidationAndCompleteForm_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.PARTIAL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test
  public void validate_whenFullValidationAndNullTenderDate_thenErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(null));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.FULL);
    assertEstimatedTenderDateFieldErrorCodesAndMessages(
        fieldErrors,
        DateInputValidator.DAY_INVALID_CODE,
        DateInputValidator.MONTH_INVALID_CODE,
        DateInputValidator.YEAR_INVALID_CODE,
        "Enter an " + WorkPlanUpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getLabel() + " "
    );
  }

  @Test
  public void validate_whenPartialValidationAndNullTenderDate_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(null));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.PARTIAL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test
  public void validate_whenFullValidationAndTenderDateInPast_thenErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().minusDays(1L)));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.FULL);
    assertEstimatedTenderDateFieldErrorCodesAndMessages(
        fieldErrors,
        DateInputValidator.DAY_AFTER_DATE_CODE,
        DateInputValidator.MONTH_AFTER_DATE_CODE,
        DateInputValidator.YEAR_AFTER_DATE_CODE,
        WorkPlanUpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getInitCappedLabel()
            + " must be after " + WorkPlanUpcomingTenderValidationHint.DATE_ERROR_LABEL
    );
  }

  @Test
  public void validate_whenPartialValidationAndTenderDateInPast_thenErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().minusDays(1L)));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.PARTIAL);
    assertEstimatedTenderDateFieldErrorCodesAndMessages(
        fieldErrors,
        DateInputValidator.DAY_AFTER_DATE_CODE,
        DateInputValidator.MONTH_AFTER_DATE_CODE,
        DateInputValidator.YEAR_AFTER_DATE_CODE,
        WorkPlanUpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getInitCappedLabel()
            + " must be after " + WorkPlanUpcomingTenderValidationHint.DATE_ERROR_LABEL
    );
  }

  @Test
  public void validate_whenFullValidationAndTenderDateInFuture_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().plusDays(1L)));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.FULL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test
  public void validate_whenPartialValidationAndTenderDateInFuture_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().plusDays(1L)));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.PARTIAL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoUpcomingTenderValidationHint_thenException() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    ValidationUtils.invokeValidator(validator, form, errors);
  }

  @Test
  public void validate_whenContractTermDaysAndDurationNotProvidedAndFullValidation_thenErrors() {

    var form = getInvalidDayDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.FULL);

    assertContractTermFieldErrorCodesAndMessages(
        errors,
        form,
        "contractTermDayDuration"
    );
  }

  @Test
  public void validate_whenContractTermDaysAndDurationNotProvidedAndPartialValidation_thenNoErrors() {

    var form = getInvalidDayDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.PARTIAL);

    assertBindingResultHasNoErrors(errors);
  }

  private WorkPlanUpcomingTenderForm getInvalidDayDurationForm() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(DurationPeriod.DAYS);
    form.setContractTermDayDuration(null);
    return form;
  }

  @Test
  public void validate_whenContractTermWeeksAndDurationNotProvidedAndFullValidation_thenErrors() {

    var form = getInvalidWeekDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.FULL);

    assertContractTermFieldErrorCodesAndMessages(
        errors,
        form,
        "contractTermWeekDuration"
    );
  }

  @Test
  public void validate_whenContractTermWeeksAndDurationNotProvidedAndPartialValidation_thenNoErrors() {

    var form = getInvalidWeekDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.PARTIAL);

    assertBindingResultHasNoErrors(errors);
  }

  private WorkPlanUpcomingTenderForm getInvalidWeekDurationForm() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(DurationPeriod.WEEKS);
    form.setContractTermWeekDuration(null);
    return form;
  }

  @Test
  public void validate_whenContractTermMonthsAndDurationNotProvidedAndFullValidation_thenErrors() {

    var form = getInvalidMonthDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.FULL);

    assertContractTermFieldErrorCodesAndMessages(
        errors,
        form,
        "contractTermMonthDuration"
    );
  }

  @Test
  public void validate_whenContractTermMonthsAndDurationNotProvidedAndPartialValidation_thenNoErrors() {

    var form = getInvalidMonthDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.PARTIAL);

    assertBindingResultHasNoErrors(errors);
  }

  private WorkPlanUpcomingTenderForm getInvalidMonthDurationForm() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(DurationPeriod.MONTHS);
    form.setContractTermMonthDuration(null);
    return form;
  }

  @Test
  public void validate_whenContractTermYearsAndDurationNotProvidedAndFullValidation_thenErrors() {

    var form = getInvalidYearDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.FULL);

    assertContractTermFieldErrorCodesAndMessages(
        errors,
        form,
        "contractTermYearDuration"
    );
  }

  @Test
  public void validate_whenContractTermYearsAndDurationNotProvidedAndPartialValidation_thenNoErrors() {

    var form = getInvalidYearDurationForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.PARTIAL);

    assertBindingResultHasNoErrors(errors);
  }

  private WorkPlanUpcomingTenderForm getInvalidYearDurationForm() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setContractTermDurationPeriod(DurationPeriod.YEARS);
    form.setContractTermYearDuration(null);
    return form;
  }

  private void assertContractTermFieldErrorCodesAndMessages(BindingResult errors,
                                                            WorkPlanUpcomingTenderForm form,
                                                            String formFieldExpectingError) {
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(formFieldExpectingError, Set.of(formFieldExpectingError + INVALID_CONTRACT_DURATION_ERROR_CODE))
    );

    final var suffix = form.getContractTermDurationPeriod().getDisplayNamePlural().toLowerCase();

    assertThat(fieldErrorMessages).containsExactly(
        entry(formFieldExpectingError, Set.of(String.format("%s %s", INVALID_CONTRACT_DURATION_PREFIX, suffix)))
    );
  }

  private void assertEstimatedTenderDateFieldErrorCodesAndMessages(BeanPropertyBindingResult errors,
                                                                   String expectedDayErrorCode,
                                                                   String expectedMonthErrorCode,
                                                                   String expectedYearErrorCode,
                                                                   String expectedDayErrorMessage) {
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("estimatedTenderDate.day", Set.of(expectedDayErrorCode)),
        entry("estimatedTenderDate.month", Set.of(expectedMonthErrorCode)),
        entry("estimatedTenderDate.year", Set.of(expectedYearErrorCode))
    );
    assertThat(fieldErrorMessages).contains(
        entry("estimatedTenderDate.day", Set.of(expectedDayErrorMessage)),
        entry("estimatedTenderDate.month", Set.of("")),
        entry("estimatedTenderDate.year", Set.of(""))
    );
  }

  private void assertBindingResultHasNoErrors(BeanPropertyBindingResult fieldErrors) {
    assertThat(fieldErrors.hasErrors()).isFalse();
  }

  private BeanPropertyBindingResult validateFormAndGetErrors(WorkPlanUpcomingTenderForm form,
                                                             ValidationType validationType) {
    var errors = new BeanPropertyBindingResult(form, "form");
    var upcomingTenderValidationHint = new WorkPlanUpcomingTenderValidationHint(validationType);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);
    return errors;
  }
}