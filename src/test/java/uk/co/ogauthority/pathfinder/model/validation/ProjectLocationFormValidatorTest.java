package uk.co.ogauthority.pathfinder.model.validation;

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
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.forminput.twofielddateinput.TwoFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.location.ProjectLocationFormValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.twofielddate.TwoFieldDateInputValidator;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectLocationFormValidatorTest {

  public static final TwoFieldDateInput BAD_TWO_FIELD_DATE = new TwoFieldDateInput(-1, 22);

  @Mock
  private TwoFieldDateInputValidator twoFieldDateInputValidator;

  private ProjectLocationFormValidator validator;

  @Before
  public void setUp() throws Exception {
    validator = new ProjectLocationFormValidator(twoFieldDateInputValidator);
    doCallRealMethod().when(twoFieldDateInputValidator).validate(any(), any(), any());
    when(twoFieldDateInputValidator.supports(any())).thenReturn(true);
  }

  @Test
  public void validate_completeForm_isValid() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_answeredTrueButMissingDate_withEmptyDateAcceptableHint() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors, new EmptyDateAcceptableHint());

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_answeredTrueButMissingDate() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(new TwoFieldDateInput(null, null));
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isGreaterThan(0);
    assertThat(fieldErrors).containsExactly(
        entry("approvedFdpDate.month", Set.of(TwoFieldDateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(TwoFieldDateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(
            String.format(TwoFieldDateInputValidator.EMPTY_DATE_ERROR, "an "+ ProjectLocationFormValidator.APPROVED_FDP_LABEL.getLabel()))
        )
    );
  }

  @Test
  public void validate_answeredTrueButMissingDate_isInvalid() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(BAD_TWO_FIELD_DATE);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isGreaterThan(0);
    assertThat(fieldErrors).containsExactly(
        entry("approvedFdpDate.month", Set.of(TwoFieldDateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(TwoFieldDateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).containsExactly(
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(
            ProjectLocationFormValidator.APPROVED_FDP_LABEL.getLabel() + TwoFieldDateInputValidator.VALID_DATE_ERROR)
        )
    );
  }

  @Test
  public void validate_answeredTrueToBoth_bothDatesMissing() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(new TwoFieldDateInput(null, null));
    form.setApprovedDecomProgram(true);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isGreaterThan(0);
    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.month", Set.of(TwoFieldDateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(TwoFieldDateInputValidator.YEAR_INVALID_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(TwoFieldDateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(TwoFieldDateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(
            String.format(TwoFieldDateInputValidator.EMPTY_DATE_ERROR, "an "+ ProjectLocationFormValidator.APPROVED_FDP_LABEL.getLabel()))
        ),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of(
            String.format(TwoFieldDateInputValidator.EMPTY_DATE_ERROR, "an " + ProjectLocationFormValidator.APPROVED_DECOM_LABEL.getLabel()))
        )
    );
  }

  @Test
  public void validate_answeredTrueToBoth_bothDatesMissing_areInvalid() {
    var form = ProjectLocationUtil.getCompletedForm_manualField();
    form.setApprovedFdpDate(BAD_TWO_FIELD_DATE);
    form.setApprovedDecomProgram(true);
    form.setApprovedDecomProgramDate(BAD_TWO_FIELD_DATE);
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(validator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors.size()).isGreaterThan(0);
    assertThat(fieldErrors).contains(
        entry("approvedFdpDate.month", Set.of(TwoFieldDateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedFdpDate.year", Set.of(TwoFieldDateInputValidator.YEAR_INVALID_CODE)),
        entry("approvedDecomProgramDate.month", Set.of(TwoFieldDateInputValidator.MONTH_INVALID_CODE)),
        entry("approvedDecomProgramDate.year", Set.of(TwoFieldDateInputValidator.YEAR_INVALID_CODE))
    );

    assertThat(fieldErrorMessages).contains(
        entry("approvedFdpDate.month", Set.of("")),
        entry("approvedFdpDate.year", Set.of(
            ProjectLocationFormValidator.APPROVED_FDP_LABEL.getLabel() + TwoFieldDateInputValidator.VALID_DATE_ERROR)
        ),
        entry("approvedDecomProgramDate.month", Set.of("")),
        entry("approvedDecomProgramDate.year", Set.of(
            ProjectLocationFormValidator.APPROVED_DECOM_LABEL.getLabel() + TwoFieldDateInputValidator.VALID_DATE_ERROR)
        )
    );
  }
}
