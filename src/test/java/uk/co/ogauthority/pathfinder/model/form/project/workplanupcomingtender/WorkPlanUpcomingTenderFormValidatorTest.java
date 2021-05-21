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
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.duration.DurationPeriod;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.OnOrAfterQuarterYearHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.validationhint.QuarterYearHint;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.WorkPlanUpcomingTenderUtil;

@RunWith(MockitoJUnitRunner.class)
public class WorkPlanUpcomingTenderFormValidatorTest {

  @Mock
  private QuarterYearInputValidator quarterYearInputValidator;

  private static final String INVALID_CONTRACT_DURATION_PREFIX = WorkPlanUpcomingTenderFormValidator.INVALID_CONTRACT_DURATION_PREFIX;
  private static final String INVALID_CONTRACT_DURATION_ERROR_CODE = WorkPlanUpcomingTenderFormValidator.INVALID_CONTRACT_DURATION_ERROR_CODE;

  private WorkPlanUpcomingTenderFormValidator validator;

  @Before
  public void setUp() {
    validator = new WorkPlanUpcomingTenderFormValidator(quarterYearInputValidator);
    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any());
    when(quarterYearInputValidator.supports(any())).thenReturn(true);
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
    form.setEstimatedTenderStartDate(new QuarterYearInput(null, null));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.FULL);
    assertEstimatedTenderDateFieldErrorCodesAndMessages(
        fieldErrors,
        QuarterYearInputValidator.QUARTER_INVALID_CODE,
        QuarterYearInputValidator.YEAR_INVALID_CODE,
        "Enter an " + WorkPlanUpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getLabel() + " "
    );
  }

  @Test
  public void validate_whenPartialValidationAndNullTenderDate_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderStartDate(new QuarterYearInput(null, null));
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.PARTIAL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test
  public void validate_whenFullValidationAndTenderDateInPast_thenErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderStartDate(new QuarterYearInput(Quarter.Q1, "2020"));

    assertOnOrAfterTenderDateError(form, ValidationType.FULL);
  }

  @Test
  public void validate_whenPartialValidationAndTenderDateInPast_thenErrors() {
    final var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderStartDate(new QuarterYearInput(Quarter.Q1, "2020"));

    assertOnOrAfterTenderDateError(form, ValidationType.PARTIAL);
  }

  private void assertOnOrAfterTenderDateError(WorkPlanUpcomingTenderForm form,
                                              ValidationType validationType) {

    final var fieldErrors = validateFormAndGetErrors(form, validationType);

    final var expectedErrorMessage = String.format(
        OnOrAfterQuarterYearHint.ERROR_MESSAGE_TEXT,
        WorkPlanUpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getInitCappedLabel(),
        QuarterYearHint.CURRENT_QUARTER_YEAR_LABEL
    );

    assertEstimatedTenderDateFieldErrorCodesAndMessages(
        fieldErrors,
        QuarterYearInputValidator.QUARTER_ON_OR_AFTER_DATE_CODE,
        QuarterYearInputValidator.YEAR_ON_OR_AFTER_DATE_CODE,
        expectedErrorMessage
    );
  }

  @Test
  public void validate_whenFullValidationAndTenderDateInFuture_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    final var quarterYearInFuture = new QuarterYearInput(
        Quarter.Q1,
        String.valueOf(LocalDate.now().plusYears(1).getYear())
    );
    form.setEstimatedTenderStartDate(quarterYearInFuture);
    var fieldErrors = validateFormAndGetErrors(form, ValidationType.FULL);
    assertBindingResultHasNoErrors(fieldErrors);
  }

  @Test
  public void validate_whenPartialValidationAndTenderDateInFuture_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getCompleteForm();
    final var quarterYearInFuture = new QuarterYearInput(
        Quarter.Q1,
        String.valueOf(LocalDate.now().plusYears(1).getYear())
    );
    form.setEstimatedTenderStartDate(quarterYearInFuture);    var fieldErrors = validateFormAndGetErrors(form, ValidationType.PARTIAL);
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

  @Test
  public void validate_whenEmptyFormAndPartialValidation_thenNoErrors() {
    var form = WorkPlanUpcomingTenderUtil.getEmptyForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.PARTIAL);

    assertBindingResultHasNoErrors(errors);
  }

  @Test
  public void validate_whenEmptyFormAndFullValidation_thenErrors() {
    var form = WorkPlanUpcomingTenderUtil.getEmptyForm();

    final var errors = validateFormAndGetErrors(form, ValidationType.FULL);

    assertThat(errors.hasErrors()).isTrue();
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
                                                                   String expectedQuarterErrorCode,
                                                                   String expectedYearErrorCode,
                                                                   String expectedQuarterErrorMessage) {
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("estimatedTenderStartDate.quarter", Set.of(expectedQuarterErrorCode)),
        entry("estimatedTenderStartDate.year", Set.of(expectedYearErrorCode))
    );
    assertThat(fieldErrorMessages).contains(
        entry("estimatedTenderStartDate.quarter", Set.of(expectedQuarterErrorMessage)),
        entry("estimatedTenderStartDate.year", Set.of(""))
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