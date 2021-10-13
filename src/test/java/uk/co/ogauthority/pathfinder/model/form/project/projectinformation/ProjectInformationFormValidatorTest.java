package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.EnergyTransitionCategory;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationFormValidatorTest {

  @Mock
  private QuarterYearInputValidator quarterYearInputValidator;

  private ProjectInformationFormValidator projectInformationFormValidator;

  @Before
  public void setup() {
    projectInformationFormValidator = new ProjectInformationFormValidator(
        quarterYearInputValidator
    );

    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any());
    when(quarterYearInputValidator.supports(any())).thenReturn(true);
  }

  private BindingResult getErrors(ProjectInformationForm form, ValidationType validationType) {
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectInformationValidationHint = new ProjectInformationValidationHint(validationType);

    ValidationUtils.invokeValidator(projectInformationFormValidator, form, errors, projectInformationValidationHint);

    return errors;
  }

  @Test
  public void validate_whenCompleteForm_thenValid() {

    var form = ProjectInformationUtil.getCompleteForm();
    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDevelopmentFieldStageAndEmptyHiddenQuestionsWithPartialValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DEVELOPMENT);
    form.setDevelopmentFirstProductionDate(new QuarterYearInput(null, null));

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDevelopmentFieldStageAndValidHiddenQuestionsWithFullValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DEVELOPMENT);
    form.setDevelopmentFirstProductionDate(new QuarterYearInput(Quarter.Q1, "2020"));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDevelopmentFieldStageAndEmptyHiddenQuestionsWithFullValidation_thenInvalid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DEVELOPMENT);
    form.setDevelopmentFirstProductionDate(new QuarterYearInput(null, null));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("developmentFirstProductionDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("developmentFirstProductionDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("developmentFirstProductionDate.quarter", Set.of(
            String.format(
                QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR,
                "a " + ProjectInformationValidationHint.FIRST_PRODUCTION_DATE_LABEL.getLabel()
            ))
        ),
        entry("developmentFirstProductionDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenEnergyTransitionFieldStageAndEmptyHiddenQuestionsWithPartialValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.ENERGY_TRANSITION);
    form.setEnergyTransitionCategory(null);

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenEnergyTransitionFieldStageAndValidHiddenQuestionsWithFullValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.ENERGY_TRANSITION);
    form.setEnergyTransitionCategory(EnergyTransitionCategory.HYDROGEN);

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenEnergyTransitionFieldStageAndEmptyHiddenQuestionsWithFullValidation_thenInvalid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.ENERGY_TRANSITION);
    form.setEnergyTransitionCategory(null);

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("energyTransitionCategory", Set.of("energyTransitionCategory.invalid"))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("energyTransitionCategory", Set.of(ProjectInformationFormValidator.MISSING_ENERGY_TRANSITION_CATEGORY_ERROR))
    );
  }

  @Test
  public void validate_whenNullFieldStageAndPartialValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(null);

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

}