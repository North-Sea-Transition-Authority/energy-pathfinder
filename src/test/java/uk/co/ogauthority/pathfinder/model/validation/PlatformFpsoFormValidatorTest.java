package uk.co.ogauthority.pathfinder.model.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.platformsfpsos.PlatformFpsoInfrastructureType;
import uk.co.ogauthority.pathfinder.model.form.forminput.minmaxdateinput.MinMaxDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.minmaxdate.MinMaxDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.PlatformFpsoTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class PlatformFpsoFormValidatorTest {

  private PlatformFpsoFormValidator validator;

  @Before
  public void setUp() throws Exception {
    validator = new PlatformFpsoFormValidator(new MinMaxDateInputValidator());
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform();
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  // The infrastructureType will normally never be null due to the @NotNull
  // constraint on the field on the form, however it will be null in the case
  // when the user clicks "Save and complete later" and partial validation runs
  @Test
  public void validate_missingInfrastructureType_isValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform();
    form.setInfrastructureType(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_platformMissingPlatformStructure_isInvalid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform();
    form.setInfrastructureType(PlatformFpsoInfrastructureType.PLATFORM);
    form.setPlatformStructure(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("platformStructure", Set.of("platformStructure.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "platformStructure",
            Set.of(PlatformFpsoFormValidator.MISSING_PLATFORM_ERROR)
        )
    );
  }

  @Test
  public void validate_fpsoMissingFpsoStructure_isInvalid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpsoAndSubstructuresToBeRemoved();
    form.setFpsoStructure(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("fpsoStructure", Set.of("fpsoStructure.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "fpsoStructure",
            Set.of(PlatformFpsoFormValidator.MISSING_FPSO_ERROR)
        )
    );
  }

  @Test
  public void validate_fpsoMissingFpsoType_isInvalid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpsoAndSubstructuresToBeRemoved();
    form.setFpsoType(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("fpsoType", Set.of("fpsoType.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "fpsoType",
            Set.of(PlatformFpsoFormValidator.MISSING_FPSO_TYPE_ERROR)
        )
    );
  }

  @Test
  public void validate_fpsoMissingFpsoDimensions_isInvalid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpsoAndSubstructuresToBeRemoved();
    form.setFpsoDimensions(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("fpsoDimensions", Set.of("fpsoDimensions.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "fpsoDimensions",
            Set.of(PlatformFpsoFormValidator.MISSING_FPSO_DIMENSIONS_ERROR)
        )
    );
  }

  @Test
  public void validate_emptyDateNotAcceptable_withEmptyDate_isInValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatform();
    form.setTopsideRemovalYears(new MinMaxDateInput(null, null));
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
    assertThat(fieldErrors).containsExactly(
        entry("topsideRemovalYears.minYear", Set.of("minYear.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "topsideRemovalYears.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                PlatformFpsoValidationHint.TOPSIDES_REMOVAL_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(PlatformFpsoValidationHint.TOPSIDES_YEAR_LABELS.getMinYearLabel()),
                PlatformFpsoValidationHint.TOPSIDES_YEAR_LABELS.getMinYearLabel(),
                PlatformFpsoValidationHint.TOPSIDES_YEAR_LABELS.getMaxYearLabel()
            ))
        )
    );
  }

  @Test
  public void validate_whenFpsoAndFullValidationAndSubstructuresExpectedToBeRemovedNull_thenError() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(null);

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("substructureExpectedToBeRemoved", Set.of("substructureExpectedToBeRemoved.invalid"))
    );

    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("substructureExpectedToBeRemoved", Set.of(PlatformFpsoFormValidator.MISSING_SUBSTRUCTURE_REMOVAL_ERROR))
    );

  }

  @Test
  public void validate_whenFpsoAndPartialValidationAndSubstructuresExpectedToBeRemovedNull_thenNoError() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(null);

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFpsoAndPartialValidationAndSubstructuresExpectedIsFalse_thenNoErrorsInConditionalQuestions() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(false);

    // explicitly set condition fields to null, so we can be sure we don't get errors in this scenario
    form.setSubstructureRemovalMass(null);
    form.setSubstructureRemovalPremise(null);
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFpsoAndFullValidationAndSubstructuresExpectedIsFalse_thenNoErrorsInConditionalQuestions() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(false);

    // explicitly set condition fields to null, so we can be sure we don't get errors in this scenario
    form.setSubstructureRemovalMass(null);
    form.setSubstructureRemovalPremise(null);
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFpsoAndSubstructuresExpectedAndPartialValidationAndNoConditionalQuestionsAnswered_thenNoErrors() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);

    // explicitly set condition fields to null, so we can be sure we don't get errors in this scenario
    form.setSubstructureRemovalMass(null);
    form.setSubstructureRemovalPremise(null);
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenFpsoAndSubstructuresExpectedAndFullValidationAndNoConditionalQuestionsAnswered_thenErrors() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);
    form.setSubstructureRemovalMass(null);
    form.setSubstructureRemovalPremise(null);
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("substructureRemovalYears.minYear", Set.of("minYear.invalid")),
        entry("substructureRemovalMass", Set.of("substructureRemovalMass.invalid")),
        entry("substructureRemovalPremise", Set.of("substructureRemovalPremise.invalid"))
    );

    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "substructureRemovalYears.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                PlatformFpsoValidationHint.SUBSTRUCTURE_REMOVAL_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(PlatformFpsoValidationHint.SUBSTRUCTURE_YEAR_LABELS.getMinYearLabel()),
                PlatformFpsoValidationHint.SUBSTRUCTURE_YEAR_LABELS.getMinYearLabel(),
                PlatformFpsoValidationHint.SUBSTRUCTURE_YEAR_LABELS.getMaxYearLabel()
            ))
        ),
        entry("substructureRemovalMass", Set.of(PlatformFpsoFormValidator.MISSING_SUBSTRUCTURE_REMOVAL_MASS_ERROR)),
        entry("substructureRemovalPremise", Set.of(PlatformFpsoFormValidator.MISSING_SUBSTRUCTURE_REMOVAL_PREMISE_ERROR))
    );
  }

  @Test
  public void validate_whenSubstructureRemovalMassIsNegativeAndPartialValidation_thenError() {
    validateAndAssertSubstructureRemovalMassErrorsWhenNotPositiveInteger(-1, ValidationType.PARTIAL);
  }

  @Test
  public void validate_whenSubstructureRemovalMassIsNegativeAndFullValidation_thenError() {
    validateAndAssertSubstructureRemovalMassErrorsWhenNotPositiveInteger(-1, ValidationType.FULL);
  }

  @Test
  public void validate_whenSubstructureRemovalMassIsZeroAndPartialValidation_thenError() {
    validateAndAssertSubstructureRemovalMassErrorsWhenNotPositiveInteger(0, ValidationType.FULL);
  }

  @Test
  public void validate_whenSubstructureRemovalMassIsZeroAndFullValidation_thenError() {
    validateAndAssertSubstructureRemovalMassErrorsWhenNotPositiveInteger(0, ValidationType.FULL);
  }

  @Test
  public void validate_whenSubstructureRemovalExpectedAndFullValidationAndRemovalYearsNull_thenError() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("substructureRemovalYears.minYear", Set.of("minYear.invalid"))
    );

    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry(
            "substructureRemovalYears.minYear",
            Set.of(String.format(
                MinMaxDateInputValidator.ENTER_BOTH_YEARS_ERROR,
                PlatformFpsoValidationHint.SUBSTRUCTURE_REMOVAL_LABEL.getInitCappedLabel(),
                StringDisplayUtil.getPrefixForVowelOrConsonant(PlatformFpsoValidationHint.SUBSTRUCTURE_YEAR_LABELS.getMinYearLabel()),
                PlatformFpsoValidationHint.SUBSTRUCTURE_YEAR_LABELS.getMinYearLabel(),
                PlatformFpsoValidationHint.SUBSTRUCTURE_YEAR_LABELS.getMaxYearLabel()
            ))
        )
    );
  }

  @Test
  public void validate_whenSubstructureRemovalExpectedAndPartialValidationAndRemovalYearsNull_thenNoError() {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  private void validateAndAssertSubstructureRemovalMassErrorsWhenNotPositiveInteger(int substructureMassTestValue,
                                                                                    ValidationType validationType) {

    final var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withFpso_manualStructure();
    form.setSubstructureExpectedToBeRemoved(true);
    form.setSubstructureRemovalMass(substructureMassTestValue);

    final var errors = new BeanPropertyBindingResult(form, "form");
    final var validationHint = new PlatformFpsoValidationHint(validationType);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    final var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("substructureRemovalMass", Set.of("substructureRemovalMass.invalid"))
    );

    final var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
        entry("substructureRemovalMass", Set.of(PlatformFpsoFormValidator.NEGATIVE_SUBSTRUCTURE_REMOVAL_MASS_ERROR))
    );
  }

}
