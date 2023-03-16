package uk.co.ogauthority.pathfinder.model.form.project.decommissionedpipeline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import uk.co.ogauthority.pathfinder.service.pipeline.PipelineService;
import uk.co.ogauthority.pathfinder.testutil.DecommissionedPipelineTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class DecommissionedPipelineFormValidatorTest {

  @Mock
  private MinMaxDateInputValidator minMaxDateInputValidator;

  @Mock
  private PipelineService pipelineService;

  private DecommissionedPipelineFormValidator decommissionedPipelineFormValidator;

  private DecommissionedPipelineValidationHint decommissionedPipelineValidationHint;

  @Before
  public void setup() {
    decommissionedPipelineFormValidator = new DecommissionedPipelineFormValidator(
        minMaxDateInputValidator,
        pipelineService
    );
    doCallRealMethod().when(minMaxDateInputValidator).validate(any(), any(), any());
    when(minMaxDateInputValidator.supports(any())).thenReturn(true);
    when(pipelineService.isPipelineSelectable(anyInt())).thenReturn(true);
  }

  @Test
  public void validate_whenFullValidationAndValidForm_thenNoErrors() {
    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissionedPipelineFormValidator, form, errors, decommissionedPipelineValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndValidForm_thenNoErrors() {
    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissionedPipelineFormValidator, form, errors, decommissionedPipelineValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndDecommissioningDateMissing_thenNoErrors() {
    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setDecommissioningDate(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(decommissionedPipelineFormValidator, form, errors, decommissionedPipelineValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFullSaveAndDecommissioningDateMissing_thenError() {
    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setDecommissioningDate(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissionedPipelineFormValidator, form, errors, decommissionedPipelineValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningDate.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningDate.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                DecommissionedPipelineValidationHint.DECOMMISSIONING_DATE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMinYearLabel()),
                DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMinYearLabel(),
                DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMaxYearLabel()
            )))
    );
  }

  @Test
  public void validate_whenFullSaveAndDecommissioningDateMaxMissing_thenError() {
    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setDecommissioningDate(new MinMaxDateInput("2020", null));

    var errors = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissionedPipelineFormValidator, form, errors, decommissionedPipelineValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningDate.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningDate.maxYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                DecommissionedPipelineValidationHint.DECOMMISSIONING_DATE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMinYearLabel()),
                DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMinYearLabel(),
                DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMaxYearLabel()
            )))
    );
  }

  @Test
  public void validate_whenFullSaveAndDecommissioningDateMaxInPast_thenError() {
    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setDecommissioningDate(new MinMaxDateInput("2020", "2019"));

    var errors = new BeanPropertyBindingResult(form, "form");

    decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(decommissionedPipelineFormValidator, form, errors, decommissionedPipelineValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningDate.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode())),
        entry("decommissioningDate.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningDate.minYear", Set.of(String.format(
            MinMaxDateInputValidator.MIN_BEFORE_MAX_YEAR_ERROR,
            DecommissionedPipelineValidationHint.DECOMMISSIONING_DATE_LABEL.getInitCappedLabel(),
            DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMinYearLabel(),
            DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMaxYearLabel())
        )),
        entry("decommissioningDate.maxYear", Set.of(String.format(
            MaxYearMustBeInFutureHint.MAX_YEAR_IN_FUTURE_ERROR,
            DecommissionedPipelineValidationHint.DECOMMISSIONING_DATE_LABEL.getInitCappedLabel(),
            DecommissionedPipelineValidationHint.DECOMMISSIONING_YEAR_LABELS.getMaxYearLabel())
        ))
    );
  }

  @Test
  public void validate_whenInvalidPipeline_assertError() {

    var invalidPipelineId = 100;

    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setPipeline(String.valueOf(invalidPipelineId));

    when(pipelineService.isPipelineSelectable(invalidPipelineId)).thenReturn(false);

    var errors = new BeanPropertyBindingResult(form, "form");

    Set.of(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(validationType);

      ValidationUtils.invokeValidator(
          decommissionedPipelineFormValidator,
          form,
          errors,
          decommissionedPipelineValidationHint
      );

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).containsExactly(
          entry(
              DecommissionedPipelineFormValidator.PIPELINE_FIELD_ID,
              Set.of(
                  String.format(
                      "%s.%s",
                      DecommissionedPipelineFormValidator.PIPELINE_FIELD_ID,
                      DecommissionedPipelineFormValidator.INVALID_PIPELINE_ERROR_CODE
                  )
              )
          )
      );

      assertThat(fieldErrorMessages).containsExactly(
          entry(
              DecommissionedPipelineFormValidator.PIPELINE_FIELD_ID,
              Set.of(DecommissionedPipelineFormValidator.INVALID_PIPELINE_ERROR_MESSAGE)
          )
      );
    });
  }

  @Test
  public void validate_whenValidPipeline_assertNoErrors() {

    var validPipelineId = 100;

    var form = DecommissionedPipelineTestUtil.createDecommissionedPipelineForm();
    form.setPipeline(String.valueOf(validPipelineId));

    when(pipelineService.isPipelineSelectable(validPipelineId)).thenReturn(true);

    var errors = new BeanPropertyBindingResult(form, "form");

    Set.of(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      decommissionedPipelineValidationHint = new DecommissionedPipelineValidationHint(validationType);

      ValidationUtils.invokeValidator(
          decommissionedPipelineFormValidator,
          form,
          errors,
          decommissionedPipelineValidationHint
      );

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
      var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

      assertThat(fieldErrors).isEmpty();
      assertThat(fieldErrorMessages).isEmpty();
    });

  }
}
