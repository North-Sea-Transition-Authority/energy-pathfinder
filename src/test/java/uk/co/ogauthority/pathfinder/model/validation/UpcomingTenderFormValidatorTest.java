package uk.co.ogauthority.pathfinder.model.validation;

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
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.TwoFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.twofielddate.TwoFieldDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderFormValidatorTest {

  @Mock
  private TwoFieldDateInputValidator twoFieldDateInputValidator;

  private UpcomingTenderFormValidator validator;


  @Before
  public void setUp() throws Exception {
    validator = new UpcomingTenderFormValidator(twoFieldDateInputValidator);
    doCallRealMethod().when(twoFieldDateInputValidator).validate(any(), any(), any());
    when(twoFieldDateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_isValid_withEmptyDate() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new TwoFieldDateInput(null));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors, new EmptyDateAcceptableHint());

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_isInValid_withPastDate() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new TwoFieldDateInput(LocalDate.now().minusDays(1L)));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors, new EmptyDateAcceptableHint());

    checkCommonFieldErrorsAndMessages(errors);
  }

  @Test
  public void validate_completeForm_dateIsNotInFuture_isInvalid() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setEstimatedTenderDate(new TwoFieldDateInput(LocalDate.now().minusDays(1L)));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);
    checkCommonFieldErrorsAndMessages(errors);
  }

  private void checkCommonFieldErrorsAndMessages(BeanPropertyBindingResult errors) {
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isGreaterThan(0);
    assertThat(fieldErrors).contains(
        entry("estimatedTenderDate.month", Set.of(TwoFieldDateInputValidator.MONTH_AFTER_DATE_CODE)),
        entry("estimatedTenderDate.year", Set.of(TwoFieldDateInputValidator.YEAR_AFTER_DATE_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("estimatedTenderDate.month", Set.of("")),
        entry("estimatedTenderDate.year", Set.of(
            UpcomingTenderFormValidator.ESTIMATED_TENDER_LABEL.getLabel() + " must be after " + UpcomingTenderFormValidator.DATE_ERROR_LABEL)
        )
    );
  }
}
