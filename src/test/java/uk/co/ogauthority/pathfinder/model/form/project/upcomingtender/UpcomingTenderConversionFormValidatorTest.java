package uk.co.ogauthority.pathfinder.model.form.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderConversionUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class UpcomingTenderConversionFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  @InjectMocks
  private UpcomingTenderConversionFormValidator upcomingTenderConversionFormValidator;

  private AwardedContractValidationHint awardedContractValidationHint;
  private UpcomingTenderConversionForm form;

  @BeforeEach
  void setup() {
    awardedContractValidationHint = new AwardedContractValidationHint(ValidationType.PARTIAL);
    form = UpcomingTenderConversionUtil.createUpcomingTenderConversionForm();
  }

  @Test
  void supports() {
    assertThat(upcomingTenderConversionFormValidator.supports(UpcomingTenderConversionForm.class)).isTrue();
  }

  @Test
  void validate_completeForm_thenValid() {
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(upcomingTenderConversionFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_emptyForm_thenInValid() {
    form = new UpcomingTenderConversionForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(upcomingTenderConversionFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("dateAwarded", Set.of("dateAwarded.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("dateAwarded", Set.of("Enter a date awarded"))
    );
  }

  @Test
  void validate_whenPartialValidationAndNoDateAwarded_thenValid() {
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
    form.setDateAwarded(new ThreeFieldDateInput(null, null, null));
    var errors = new BeanPropertyBindingResult(form, "form");

    awardedContractValidationHint = new AwardedContractValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(upcomingTenderConversionFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_partialDateAwarded_thenInvalid() {
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
    form.setDateAwarded(new ThreeFieldDateInput(2020, null, null));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(upcomingTenderConversionFormValidator, form, errors, awardedContractValidationHint);

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
  void validate_pastDateAwarded_thenValid() {
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
    form.setDateAwarded(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(upcomingTenderConversionFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_todayDateAwarded_thenValid() {
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
    form.setDateAwarded(new ThreeFieldDateInput(LocalDate.now()));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(upcomingTenderConversionFormValidator, form, errors, awardedContractValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }
}
