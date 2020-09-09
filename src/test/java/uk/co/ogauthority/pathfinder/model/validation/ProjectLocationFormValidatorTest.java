package uk.co.ogauthority.pathfinder.model.validation;

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
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationValidationHint;
import uk.co.ogauthority.pathfinder.model.form.validation.date.DateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationFormValidatorTest {

  public static final ThreeFieldDateInput BAD_THREE_FIELD_DATE = new ThreeFieldDateInput(-1, 22, -1);

  @Mock
  private DateInputValidator dateInputValidator;

  private ProjectLocationFormValidator validator;

  @Before
  public void setUp() {
    validator = new ProjectLocationFormValidator(dateInputValidator);
    doCallRealMethod().when(dateInputValidator).validate(any(), any(), any());
    when(dateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_answeredTrueButMissingDate_withEmptyDateAcceptableHint() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.PARTIAL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_answeredTrueButMissingDate() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(new ThreeFieldDateInput(null, null, null));
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
    assertThat(fieldErrors).containsExactly(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.day", Set.of(
            String.format(DateInputValidator.EMPTY_DATE_ERROR, "an "+ ProjectLocationValidationHint.APPROVED_FDP_LABEL.getLabel()))
        ),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_answeredTrueButMissingDate_isInvalid() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(BAD_THREE_FIELD_DATE);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
    assertThat(fieldErrors).containsExactly(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_FDP_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)
        ),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_answeredTrueToBoth_bothDatesMissing() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(new ThreeFieldDateInput(null, null, null));
    form.setApprovedDecomProgram(true);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE)),
        entry("approvedDecomProgramDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("approvedFdpDate.day", Set.of(
            String.format(DateInputValidator.EMPTY_DATE_ERROR, "an "+ ProjectLocationValidationHint.APPROVED_FDP_LABEL.getLabel()))
        ),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of("")),
        entry("approvedDecomProgramDate.day", Set.of(
            String.format(DateInputValidator.EMPTY_DATE_ERROR, "an " + ProjectLocationValidationHint.APPROVED_DECOM_LABEL.getLabel()))
        ),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_answeredTrueToBoth_bothDatesMissing_areInvalid() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(BAD_THREE_FIELD_DATE);
    form.setApprovedDecomProgram(true);
    form.setApprovedDecomProgramDate(BAD_THREE_FIELD_DATE);
    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isPositive();
    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE)),
        entry("approvedDecomProgramDate.day", Set.of(DateInputValidator.DAY_INVALID_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(DateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(DateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("approvedFdpDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_FDP_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of("")),
        entry("approvedDecomProgramDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_DECOM_LABEL.getInitCappedLabel() + DateInputValidator.VALID_DATE_ERROR)
        ),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of("")
        )
    );
  }

  @Test
  public void validate_fdpApprovalDateCannotBeInFuture_whenInFuture_thenFail() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    form.setApprovedFdpDate(new ThreeFieldDateInput(LocalDate.now().plusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.day", Set.of(DateInputValidator.DAY_BEFORE_DATE_CODE)),
        entry("approvedFdpDate.month", Set.of(DateInputValidator.MONTH_BEFORE_DATE_CODE)),
        entry("approvedFdpDate.year", Set.of(DateInputValidator.YEAR_BEFORE_DATE_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_FDP_LABEL.getInitCappedLabel() + " must be the same as or before today's date")),
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_fdpApprovalDateCannotBeInFuture_whenTheSameAs_thenPass() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    form.setApprovedFdpDate(new ThreeFieldDateInput(LocalDate.now()));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_fdpApprovalDateCannotBeInFuture_whenBefore_thenPass() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    form.setApprovedFieldDevelopmentPlan(true);
    form.setApprovedFdpDate(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_decomProgramApprovalCannotBeInFuture_whenInFuture_thenFail() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    form.setApprovedDecomProgram(true);
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(LocalDate.now().plusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("approvedDecomProgramDate.day", Set.of(DateInputValidator.DAY_BEFORE_DATE_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(DateInputValidator.MONTH_BEFORE_DATE_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(DateInputValidator.YEAR_BEFORE_DATE_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedDecomProgramDate.day", Set.of(
            ProjectLocationValidationHint.APPROVED_DECOM_LABEL.getInitCappedLabel() + " must be the same as or before today's date")),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of(""))
    );
  }

  @Test
  public void validate_decomProgramApprovalCannotBeInFuture_whenTheSameAs_thenPass() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(LocalDate.now()));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_decomProgramApprovalDateCannotBeInFuture_whenBefore_thenPass() {
    var form = ProjectLocationUtil.getCompletedForm_withField();
    form.setApprovedDecomProgramDate(new ThreeFieldDateInput(LocalDate.now().minusYears(1)));

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectLocationValidationHint = new ProjectLocationValidationHint(ValidationType.FULL);

    ValidationUtils.invokeValidator(validator, form, errors, projectLocationValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }
}
