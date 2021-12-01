package uk.co.ogauthority.pathfinder.model.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
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
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  private UpcomingTenderFormValidator validator;

  @Before
  public void setUp() {
    validator = new UpcomingTenderFormValidator(dateInputValidator);
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new UpcomingTenderValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_isValid_withEmptyDate() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(null));
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new UpcomingTenderValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_isInValid_withPastDate() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().minusDays(1L)));
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new UpcomingTenderValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    checkCommonFieldErrorsAndMessages(errors);
  }

  @Test
  public void validate_completeForm_dateIsNotInFuture_isInvalid() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new ThreeFieldDateInput(LocalDate.now().minusDays(1L)));
    var errors = new BeanPropertyBindingResult(form, "form");

    var upcomingTenderValidationHint = new UpcomingTenderValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);
    checkCommonFieldErrorsAndMessages(errors);
  }

  @Test
  public void validate_completeFormWithTooManyDocumentsUploaded_isInvalid() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var uploadForm = UploadedFileUtil.createUploadFileWithDescriptionForm();
    form.setUploadedFileWithDescriptionForms(List.of(uploadForm, uploadForm));

    var upcomingTenderValidationHint = new UpcomingTenderValidationHint(ValidationType.FULL);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors, upcomingTenderValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("uploadedFileWithDescriptionForms", Set.of(
            "uploadedFileWithDescriptionForms" + FieldValidationErrorCodes.EXCEEDED_MAXIMUM_FILE_UPLOAD_LIMIT.getCode()
        ))
    );

    assertThat(fieldErrorMessages).contains(
        entry("uploadedFileWithDescriptionForms", Set.of(UpcomingTenderValidationHint.TOO_MANY_FILES_ERROR_MESSAGE))
    );

  }

  @Test(expected = ActionNotAllowedException.class)
  public void validate_whenNoUpcomingTenderValidationHint_thenException() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    ValidationUtils.invokeValidator(validator, form, errors);
  }

  private void checkCommonFieldErrorsAndMessages(BeanPropertyBindingResult errors) {
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("estimatedTenderDate.day", Set.of(DateInputValidator.DAY_AFTER_DATE_CODE)),
        entry("estimatedTenderDate.month", Set.of(DateInputValidator.MONTH_AFTER_DATE_CODE)),
        entry("estimatedTenderDate.year", Set.of(DateInputValidator.YEAR_AFTER_DATE_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("estimatedTenderDate.day", Set.of(
            UpcomingTenderValidationHint.ESTIMATED_TENDER_LABEL.getInitCappedLabel() + " must be after " + UpcomingTenderValidationHint.DATE_ERROR_LABEL)
        ),
        entry("estimatedTenderDate.month", Set.of("")),
        entry("estimatedTenderDate.year", Set.of(""))
    );
  }
}
