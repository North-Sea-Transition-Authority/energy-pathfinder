package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.Quarter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ProjectInformationFormValidatorTest {

  @Mock
  private QuarterYearInputValidator quarterYearInputValidator;

  @InjectMocks
  private ProjectInformationFormValidator projectInformationFormValidator;

  private BindingResult getErrors(ProjectInformationForm form, ValidationType validationType) {
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectInformationValidationHint = new ProjectInformationValidationHint(validationType);

    ValidationUtils.invokeValidator(projectInformationFormValidator, form, errors, projectInformationValidationHint);

    return errors;
  }

  @Test
  void validate_whenCompleteForm_thenValid() {

    var form = ProjectInformationUtil.getCompleteForm();
    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenDevelopmentFieldStageAndEmptyHiddenQuestionsWithPartialValidation_thenValid() {
    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any(Object[].class));
    when(quarterYearInputValidator.supports(any())).thenReturn(true);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DEVELOPMENT);
    form.setDevelopmentFirstProductionDate(new QuarterYearInput(null, null));

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenDevelopmentFieldStageAndValidHiddenQuestionsWithFullValidation_thenValid() {
    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any());
    when(quarterYearInputValidator.supports(any())).thenReturn(true);

    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DEVELOPMENT);
    form.setDevelopmentFirstProductionDate(new QuarterYearInput(Quarter.Q1, "2020"));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenDevelopmentFieldStageAndEmptyHiddenQuestionsWithFullValidation_thenInvalid() {
    doCallRealMethod().when(quarterYearInputValidator).validate(any(), any(), any());
    when(quarterYearInputValidator.supports(any())).thenReturn(true);

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

  @ParameterizedTest
  @EnumSource(
      value = FieldStage.class,
      names = { "CARBON_CAPTURE_AND_STORAGE", "HYDROGEN", "ELECTRIFICATION", "WIND_ENERGY" },
      mode = EnumSource.Mode.INCLUDE
  )
  void validate_whenFieldStageWithSubCategoryAndEmptyHiddenQuestionsWithPartialValidation_thenValid(FieldStage fieldStage) {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(fieldStage);
    form.setCarbonCaptureSubCategory(null);
    form.setHydrogenSubCategory(null);
    form.setElectrificationSubCategory(null);
    form.setWindEnergySubCategory(null);

    var errors = getErrors(form, ValidationType.PARTIAL);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  void validate_whenFieldStageWithSubCategoryAndValidHiddenQuestionsWithFullValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.CARBON_CAPTURE_AND_STORAGE);
    form.setCarbonCaptureSubCategory(FieldStageSubCategory.CAPTURE_AND_ONSHORE);

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("fieldStageWithSubCategoryMissing_fullValidation_arguments")
  void validate_whenFieldStageWithSubCategoryAndEmptyHiddenQuestionsWithFullValidation_thenInvalid(FieldStage fieldStage,
                                                                                                          String field,
                                                                                                          String errorMessage) {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(fieldStage);
    form.setCarbonCaptureSubCategory(null);
    form.setHydrogenSubCategory(null);
    form.setElectrificationSubCategory(null);
    form.setWindEnergySubCategory(null);

    var errors = getErrors(form, ValidationType.FULL);
    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry(field, Set.of(field.concat(".required")))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry(field, Set.of(errorMessage))
    );
  }

  @Test
  void validate_whenNullFieldStageAndPartialValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(null);

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  private static Stream<Arguments> fieldStageWithSubCategoryMissing_fullValidation_arguments() {
    return Stream.of(
        Arguments.of(FieldStage.CARBON_CAPTURE_AND_STORAGE, ProjectInformationFormValidator.CARBON_CAPTURE_AND_STORAGE_FIELD, ProjectInformationFormValidator.CARBON_CAPTURE_AND_STORAGE_MISSING_ERROR),
        Arguments.of(FieldStage.HYDROGEN, ProjectInformationFormValidator.HYDROGEN_FIELD, ProjectInformationFormValidator.HYDROGEN_MISSING_ERROR),
        Arguments.of(FieldStage.ELECTRIFICATION, ProjectInformationFormValidator.ELECTRIFICATION_FIELD, ProjectInformationFormValidator.ELECTRIFICATION_MISSING_ERROR),
        Arguments.of(FieldStage.WIND_ENERGY, ProjectInformationFormValidator.WIND_ENERGY_FIELD, ProjectInformationFormValidator.WIND_ENERGY_MISSING_ERROR)
    );
  }

}
