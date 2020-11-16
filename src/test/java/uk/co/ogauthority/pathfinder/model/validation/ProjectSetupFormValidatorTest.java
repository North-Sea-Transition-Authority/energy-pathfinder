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
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupFormValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupFormValidator;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ProjectTaskListSetupTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSetupFormValidatorTest {

  private ProjectSetupFormValidator validator;

  @Before
  public void setUp() throws Exception {
    validator = new ProjectSetupFormValidator();
  }


  @Test
  public void validate_completeForm_isValid() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupForm();
    var errors =  new BeanPropertyBindingResult(form, "form");

    var hint = new ProjectSetupFormValidationHint(false, ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_inCompleteForm_imValid() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupForm_withDecomSections();
    form.setWellsIncluded(null);
    var errors =  new BeanPropertyBindingResult(form, "form");

    var hint = new ProjectSetupFormValidationHint(true, ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrors).contains(
        entry("wellsIncluded", Set.of(
            "wellsIncluded" + FieldValidationErrorCodes.INVALID.getCode()
        ))
    );

    assertThat(fieldErrorMessages).contains(
      entry("wellsIncluded", Set.of(ProjectSetupFormValidationHint.WELLS_REQUIRED_TEXT))
    );
  }

  @Test
  public void validate_completeForm_decomRelated_isValid() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupForm_withDecomSections();
    var errors =  new BeanPropertyBindingResult(form, "form");

    var hint = new ProjectSetupFormValidationHint(true, ValidationType.FULL);
    ValidationUtils.invokeValidator(validator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_partialForm_partialValidation_isValid() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupForm();
    form.setAwardedContractsIncluded(null);
    var errors =  new BeanPropertyBindingResult(form, "form");

    var hint = new ProjectSetupFormValidationHint(false, ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(validator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_completeForm_partialValidator_decomRelated_isValid() {
    var form = ProjectTaskListSetupTestUtil.getProjectSetupForm_withDecomSections();
    form.setWellsIncluded(null);
    var errors =  new BeanPropertyBindingResult(form, "form");

    var hint = new ProjectSetupFormValidationHint(true, ValidationType.PARTIAL);
    ValidationUtils.invokeValidator(validator, form, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).isEmpty();
  }
}
