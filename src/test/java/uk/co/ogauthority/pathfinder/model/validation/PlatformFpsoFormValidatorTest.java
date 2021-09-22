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
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved();
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
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved();
    form.setInfrastructureType(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_platformMissingPlatformStructure_isInvalid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved();
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
  public void validate_emptyDateAcceptable_withEmptyDate_isValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved();
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_emptyDateNotAcceptable_withEmptyDate_isInValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved();
    form.setTopsideRemovalYears(new MinMaxDateInput(null, null));
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

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
  public void validate_noSubstructuresRemoved_isValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndNoSubstructuresToBeRemoved();

    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_noSubstructuresRemoved_missingSubstructureQuestions_isInValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withPlatformAndSubstructuresToBeRemoved();
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));
    form.setSubstructureRemovalMass(null);
    form.setSubstructureRemovalPremise(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("substructureRemovalYears.minYear", Set.of("minYear.invalid")),
        entry("substructureRemovalMass", Set.of("substructureRemovalMass.invalid")),
        entry("substructureRemovalPremise", Set.of("substructureRemovalPremise.invalid"))
    );

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

}
