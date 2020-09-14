package uk.co.ogauthority.pathfinder.model.form.project.projectinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.Quarter;
import uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput.QuarterYearInput;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.quarteryear.QuarterYearInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectInformationFormValidatorTest {

  @Mock
  private DateInputValidator dateInputValidator;

  @Mock
  private QuarterYearInputValidator quarterYearInputValidator;

  private ProjectInformationFormValidator projectInformationFormValidator;

  @Before
  public void setup() {
    projectInformationFormValidator = new ProjectInformationFormValidator(
        dateInputValidator,
        quarterYearInputValidator
    );

    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);

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
  public void validate_whenDiscoveryFieldStageAndEmptyHiddenQuestionsWithPartialValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DISCOVERY);
    form.setDiscoveryFirstProductionDate(new QuarterYearInput(null, null));

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDiscoveryFieldStageAndValidHiddenQuestionsWithFullValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DISCOVERY);
    form.setDiscoveryFirstProductionDate(new QuarterYearInput(Quarter.Q1, "2020"));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDiscoveryFieldStageAndEmptyHiddenQuestionsWithFullValidation_thenInvalid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DISCOVERY);
    form.setDiscoveryFirstProductionDate(new QuarterYearInput(null, null));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("discoveryFirstProductionDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("discoveryFirstProductionDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("discoveryFirstProductionDate.quarter", Set.of(
            String.format(
                QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR,
                "a " + ProjectInformationValidationHint.FIRST_PRODUCTION_DATE_LABEL.getLabel()
            ))
        ),
        entry("discoveryFirstProductionDate.year", Set.of(""))
    );
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
  public void validate_whenDecommissioningFieldStageAndEmptyHiddenQuestionsWithPartialValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DECOMMISSIONING);
    form.setDecomWorkStartDate(new QuarterYearInput(null, null));
    form.setProductionCessationDate(new ThreeFieldDateInput(null, null, null));

    var errors = getErrors(form, ValidationType.PARTIAL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDecommissioningFieldStageAndValidHiddenQuestionsWithFullValidation_thenValid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DECOMMISSIONING);
    form.setDecomWorkStartDate(new QuarterYearInput(Quarter.Q1, "2020"));
    form.setProductionCessationDate(new ThreeFieldDateInput(LocalDate.now()));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenDecommissioningFieldStageAndEmptyHiddenQuestionsWithFullValidation_thenDecomWorkInvalid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DECOMMISSIONING);
    form.setDecomWorkStartDate(new QuarterYearInput(null, null));
    form.setProductionCessationDate(new ThreeFieldDateInput(null, null, null));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("decomWorkStartDate.quarter", Set.of(QuarterYearInputValidator.QUARTER_INVALID_CODE)),
        entry("decomWorkStartDate.year", Set.of(QuarterYearInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("decomWorkStartDate.quarter", Set.of(
            String.format(
                QuarterYearInputValidator.EMPTY_QUARTER_YEAR_ERROR,
                "a " + ProjectInformationValidationHint.DECOM_WORK_START_DATE_LABEL.getLabel()
            ))
        ),
        entry("decomWorkStartDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_whenDecommissioningFieldStageAndPartiallyEnteredProductionCessationDate_thenInvalid() {
    var form = ProjectInformationUtil.getCompleteForm();
    form.setFieldStage(FieldStage.DECOMMISSIONING);
    form.setDecomWorkStartDate(new QuarterYearInput(Quarter.Q1, "2020"));
    form.setProductionCessationDate(new ThreeFieldDateInput(2020, null, null));

    var errors = getErrors(form, ValidationType.FULL);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).containsExactly(
        entry("productionCessationDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("productionCessationDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("productionCessationDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("productionCessationDate.day", Set.of(
              ProjectInformationValidationHint.PRODUCTION_CESSATION_DATE_LABEL.getInitCappedLabel() +
              DateInputValidator.VALID_DATE_ERROR
            )
        ),
        entry("productionCessationDate.month", Set.of("")),
        entry("productionCessationDate.year", Set.of(""))
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