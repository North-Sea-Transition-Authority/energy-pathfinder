package uk.co.ogauthority.pathfinder.model.form.project.plugabandonmentschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.validationhint.MaxYearMustBeInFutureHint;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.PlugAbandonmentScheduleTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlugAbandonmentScheduleFormValidatorTest {

  @Mock
  private MinMaxDateInputValidator minMaxDateInputValidator;

  private PlugAbandonmentScheduleFormValidator plugAbandonmentScheduleFormValidator;

  private PlugAbandonmentScheduleValidationHint plugAbandonmentScheduleValidationHint;
  
  @Before
  public void setup() {
    plugAbandonmentScheduleFormValidator = new PlugAbandonmentScheduleFormValidator(minMaxDateInputValidator);
    doCallRealMethod().when(minMaxDateInputValidator).validate(any(), any(), any());
    when(minMaxDateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_whenPartialValidationAndValidForm_thenNoErrors() {
    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleValidationHint = new PlugAbandonmentScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(plugAbandonmentScheduleFormValidator, form, errors, plugAbandonmentScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndPlugAbandonmentDateMissing_thenNoErrors() {
    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleValidationHint = new PlugAbandonmentScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(plugAbandonmentScheduleFormValidator, form, errors, plugAbandonmentScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFullSaveAndPlugAbandonmentDateMissing_thenError() {
    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleValidationHint = new PlugAbandonmentScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(plugAbandonmentScheduleFormValidator, form, errors, plugAbandonmentScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("plugAbandonmentDate.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("plugAbandonmentDate.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_DATE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMinYearLabel()),
                PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMinYearLabel(),
                PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMaxYearLabel()
        )))
    );
  }

  @Test
  public void validate_whenFullSaveAndPlugAbandonmentDateMaxMissing_thenError() {
    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new MinMaxDateInput("2020", null));

    var errors = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleValidationHint = new PlugAbandonmentScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(plugAbandonmentScheduleFormValidator, form, errors, plugAbandonmentScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("plugAbandonmentDate.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("plugAbandonmentDate.maxYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_DATE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMinYearLabel()),
                PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMinYearLabel(),
                PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMaxYearLabel()
            )))
    );
  }

  @Test
  public void validate_whenFullSaveAndPlugAbandonmentDateMaxInPast_thenError() {
    var form = PlugAbandonmentScheduleTestUtil.getCompletedForm();
    form.setPlugAbandonmentDate(new MinMaxDateInput("2020", "2019"));

    var errors = new BeanPropertyBindingResult(form, "form");

    plugAbandonmentScheduleValidationHint = new PlugAbandonmentScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(plugAbandonmentScheduleFormValidator, form, errors, plugAbandonmentScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("plugAbandonmentDate.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode())),
        entry("plugAbandonmentDate.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("plugAbandonmentDate.minYear", Set.of(String.format(
            MinMaxDateInputValidator.MIN_BEFORE_MAX_YEAR_ERROR,
            PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_DATE_LABEL.getInitCappedLabel(),
            PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMinYearLabel(),
            PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMaxYearLabel())
        )),
        entry("plugAbandonmentDate.maxYear", Set.of(String.format(
            MaxYearMustBeInFutureHint.MAX_YEAR_IN_FUTURE_ERROR,
            PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_DATE_LABEL.getInitCappedLabel(),
            PlugAbandonmentScheduleValidationHint.PLUG_ABANDONMENT_YEAR_LABELS.getMaxYearLabel())
        ))
    );
  }
}
