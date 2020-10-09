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
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_emptyDateAcceptable_withEmptyDate_isValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_emptyDateNotAcceptable_withEmptyDate_isInValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
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
  public void validate_noSubstructuresRemoved_isValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_noSubstructuresToBeRemoved();

    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_noSubstructuresRemoved_missingSubstructureQuestions_isInValid() {
    var form = PlatformFpsoTestUtil.getPlatformFpsoForm_withSubstructuresToBeRemoved();
    form.setSubstructureRemovalYears(new MinMaxDateInput(null, null));
    form.setSubstructureRemovalMass(null);
    form.setSubstructureRemovalPremise(null);
    var errors = new BeanPropertyBindingResult(form, "form");
    var validationHint = new PlatformFpsoValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, validationHint);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
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
