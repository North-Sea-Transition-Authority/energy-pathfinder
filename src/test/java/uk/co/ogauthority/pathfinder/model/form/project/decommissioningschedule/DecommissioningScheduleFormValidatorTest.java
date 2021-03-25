package uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.CessationOfProductionDateType;
import uk.co.ogauthority.pathfinder.model.enums.project.decommissioningschedule.DecommissioningStartDateType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissioningScheduleFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  @Mock
  private QuarterYearInputValidator quarterYearInputValidator;

  private DecommissioningScheduleFormValidator decommissioningScheduleFormValidator;

  @Before
  public void setUp() {
    decommissioningScheduleFormValidator = new DecommissioningScheduleFormValidator(
        dateInputValidator,
        quarterYearInputValidator
    );

    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);

    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any());
    when(quarterYearInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_whenFullSaveAndExactStartDateTypeAndExactStartDateNull_thenError() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);
    form.setExactDecommissioningStartDate(new ThreeFieldDateInput(null, null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("exactDecommissioningStartDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("exactDecommissioningStartDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("exactDecommissioningStartDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    final String decommissioningStartDateLabel = DecommissioningScheduleValidationHint.DECOMMISSIONING_START_DATE_LABEL.getLabel();

    assertThat(fieldErrorMessages).containsExactly(
        entry("exactDecommissioningStartDate.day", Set.of(
            String.format(
                DateInputValidator.EMPTY_DATE_ERROR,
                StringDisplayUtil.getPrefixForVowelOrConsonant(decommissioningStartDateLabel) + decommissioningStartDateLabel
            )
        )),
        entry("exactDecommissioningStartDate.month", Set.of("")),
        entry("exactDecommissioningStartDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenFullSaveAndEstimatedStartDateTypeAndEstimatedStartDateNull_thenError() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);
    form.setEstimatedDecommissioningStartDate(new QuarterYearInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("estimatedDecommissioningStartDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("estimatedDecommissioningStartDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("estimatedDecommissioningStartDate.quarter", Set.of(
            String.format(
                QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR,
                "a " + DecommissioningScheduleValidationHint.DECOMMISSIONING_START_DATE_LABEL.getLabel()
            ))
        ),
        entry("estimatedDecommissioningStartDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenFullSaveAndUnknownStartDateTypeAndStartDateNotProvidedReasonNull_thenError() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.UNKNOWN);
    form.setDecommissioningStartDateNotProvidedReason(null);

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningStartDateNotProvidedReason", Set.of("decommissioningStartDateNotProvidedReason.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningStartDateNotProvidedReason", Set.of(
            DecommissioningScheduleFormValidator.MISSING_DECOMMISSIONING_START_DATE_NOT_PROVIDED_REASON_ERROR
        ))
    );
  }

  @Test
  public void validate_whenPartialSaveAndExactStartDateTypeAndExactStartDateNull_thenNoErrors() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.EXACT);
    form.setExactDecommissioningStartDate(new ThreeFieldDateInput(null, null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialSaveAndEstimatedStartDateTypeAndEstimatedStartDateNull_thenNoErrors() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.ESTIMATED);
    form.setEstimatedDecommissioningStartDate(new QuarterYearInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialSaveAndUnknownStartDateTypeAndStartDateNotProvidedReasonNull_thenNoErrors() {
    var form = new DecommissioningScheduleForm();
    form.setDecommissioningStartDateType(DecommissioningStartDateType.UNKNOWN);
    form.setDecommissioningStartDateNotProvidedReason(null);

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFullSaveAndExactCopDateTypeAndExactCopDateNull_thenError() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.EXACT);
    form.setExactCessationOfProductionDate(new ThreeFieldDateInput(null, null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("exactCessationOfProductionDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("exactCessationOfProductionDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("exactCessationOfProductionDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    final String cessationOfProductionDateLabel = DecommissioningScheduleValidationHint.CESSATION_OF_PRODUCTION_DATE_LABEL.getLabel();

    assertThat(fieldErrorMessages).containsExactly(
        entry("exactCessationOfProductionDate.day", Set.of(
            String.format(
                DateInputValidator.EMPTY_DATE_ERROR,
                StringDisplayUtil.getPrefixForVowelOrConsonant(cessationOfProductionDateLabel) + cessationOfProductionDateLabel
            )
        )),
        entry("exactCessationOfProductionDate.month", Set.of("")),
        entry("exactCessationOfProductionDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenFullSaveAndEstimatedCopDateTypeAndEstimatedCopDateNull_thenError() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.ESTIMATED);
    form.setEstimatedCessationOfProductionDate(new QuarterYearInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("estimatedCessationOfProductionDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("estimatedCessationOfProductionDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("estimatedCessationOfProductionDate.quarter", Set.of(
            String.format(
                QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR,
                "a " + DecommissioningScheduleValidationHint.CESSATION_OF_PRODUCTION_DATE_LABEL.getLabel()
            ))
        ),
        entry("estimatedCessationOfProductionDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenFullSaveAndUnknownCopDateTypeAndCopDateNotProvidedReasonNull_thenError() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);
    form.setCessationOfProductionDateNotProvidedReason(null);

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("cessationOfProductionDateNotProvidedReason", Set.of("cessationOfProductionDateNotProvidedReason.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("cessationOfProductionDateNotProvidedReason", Set.of(
            DecommissioningScheduleFormValidator.MISSING_CESSATION_OF_PRODUCTION_DATE_NOT_PROVIDED_REASON_ERROR
        ))
    );
  }

  @Test
  public void validate_whenPartialSaveAndExactCopDateTypeAndExactCopDateNull_thenNoErrors() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.EXACT);
    form.setExactCessationOfProductionDate(new ThreeFieldDateInput(null, null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialSaveAndEstimatedCopDateTypeAndEstimatedCopDateNull_thenNoErrors() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.ESTIMATED);
    form.setEstimatedCessationOfProductionDate(new QuarterYearInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialSaveAndUnknownCopDateTypeAndCopDateNotProvidedReasonNull_thenNoErrors() {
    var form = new DecommissioningScheduleForm();
    form.setCessationOfProductionDateType(CessationOfProductionDateType.UNKNOWN);
    form.setCessationOfProductionDateNotProvidedReason(null);

    var errors = new BeanPropertyBindingResult(form, "form");

    var decommissioningScheduleValidationHint = new DecommissioningScheduleValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissioningScheduleFormValidator, form, errors, decommissioningScheduleValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }
}
