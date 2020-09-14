package uk.co.ogauthority.pathfinder.model.form.project.awardedcontract;

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
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.AwardedContractUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class AwardedContractFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  private AwardedContractFormValidator awardedContractFormValidator;

  private AwardedContractValidationHint awardedContractValidationHint;

  @Before
  public void setup() {
    awardedContractFormValidator = new AwardedContractFormValidator(dateInputValidator);
    awardedContractValidationHint = new AwardedContractValidationHint();
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_completeForm_thenValid() {
    var form = AwardedContractUtil.createAwardedContractForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(awardedContractFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_noDateAwarded_thenValid() {
    var form = AwardedContractUtil.createAwardedContractForm();
    form.setDateAwarded(new ThreeFieldDateInput(null, null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(awardedContractFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_partialDateAwarded_thenInvalid() {
    var form = AwardedContractUtil.createAwardedContractForm();
    form.setDateAwarded(new ThreeFieldDateInput(2020, null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(awardedContractFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("dateAwarded.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("dateAwarded.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("dateAwarded.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("dateAwarded.day", Set.of(
            AwardedContractValidationHint.DATE_AWARDED_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)
        ),
        entry("dateAwarded.month", Set.of("")),
        entry("dateAwarded.year", Set.of(""))
    );
  }

  @Test
  public void validate_pastDateAwarded_thenValid() {
    var form = AwardedContractUtil.createAwardedContractForm();
    form.setDateAwarded(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(awardedContractFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_todayDateAwarded_thenValid() {
    var form = AwardedContractUtil.createAwardedContractForm();
    form.setDateAwarded(new ThreeFieldDateInput(LocalDate.now()));

    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(awardedContractFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }
}