package uk.co.ogauthority.pathfinder.model.form.project.subseainfrastructure;

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
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.SubseaInfrastructureTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class SubseaInfrastructureFormValidatorTest {

  @Mock
  private MinMaxDateInputValidator minMaxDateInputValidator;

  private SubseaInfrastructureFormValidator subseaInfrastructureFormValidator;

  private SubseaInfrastructureValidationHint subseaInfrastructureValidationHint;

  @Before
  public void setup() {
    subseaInfrastructureFormValidator = new SubseaInfrastructureFormValidator(minMaxDateInputValidator);
    doCallRealMethod().when(minMaxDateInputValidator).validate(any(), any(), any());
    when(minMaxDateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_whenFullValidationAndValidForm_thenNoErrors() {
    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(subseaInfrastructureFormValidator, form, errors, subseaInfrastructureValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndValidForm_thenNoErrors() {
    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(subseaInfrastructureFormValidator, form, errors, subseaInfrastructureValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenPartialValidationAndDecommissioningDateMissing_thenNoErrors() {
    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setDecommissioningDate(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(subseaInfrastructureFormValidator, form, errors, subseaInfrastructureValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFullSaveAndDecommissioningDateMissing_thenError() {
    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setDecommissioningDate(new MinMaxDateInput(null, null));

    var errors = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(subseaInfrastructureFormValidator, form, errors, subseaInfrastructureValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningDate.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningDate.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                SubseaInfrastructureValidationHint.DECOM_DATE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMinYearLabel()),
                SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMinYearLabel(),
                SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMaxYearLabel()
        )))
    );
  }

  @Test
  public void validate_whenFullSaveAndDecommissioningDateMaxMissing_thenError() {
    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setDecommissioningDate(new MinMaxDateInput("2020", null));

    var errors = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(subseaInfrastructureFormValidator, form, errors, subseaInfrastructureValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningDate.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningDate.maxYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                SubseaInfrastructureValidationHint.DECOM_DATE_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMinYearLabel()),
                SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMinYearLabel(),
                SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMaxYearLabel()
            )))
    );
  }

  @Test
  public void validate_whenFullSaveAndDecommissioningDateMaxInPast_thenError() {
    var form = SubseaInfrastructureTestUtil.createSubseaInfrastructureForm();
    form.setDecommissioningDate(new MinMaxDateInput("2020", "2019"));

    var errors = new BeanPropertyBindingResult(form, "form");

    subseaInfrastructureValidationHint = new SubseaInfrastructureValidationHint(ValidationType.FULL);
    ValidationUtils.invokeValidator(subseaInfrastructureFormValidator, form, errors, subseaInfrastructureValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decommissioningDate.minYear", Set.of("minYear" + FieldValidationErrorCodes.INVALID.getCode())),
        entry("decommissioningDate.maxYear", Set.of("maxYear" + FieldValidationErrorCodes.INVALID.getCode()))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decommissioningDate.minYear", Set.of(String.format(
            MinMaxDateInputValidator.MIN_BEFORE_MAX_YEAR_ERROR,
            SubseaInfrastructureValidationHint.DECOM_DATE_LABEL.getInitCappedLabel(),
            SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMinYearLabel(),
            SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMaxYearLabel())
        )),
        entry("decommissioningDate.maxYear", Set.of(String.format(
            MinMaxDateInputValidator.MAX_YEAR_IN_FUTURE_ERROR,
            SubseaInfrastructureValidationHint.DECOM_DATE_LABEL.getInitCappedLabel(),
            SubseaInfrastructureValidationHint.DECOM_YEAR_LABELS.getMaxYearLabel())
        ))
    );
  }

}