package uk.co.ogauthority.pathfinder.model.form.project.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ProjectAssessmentTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectAssessmentFormValidatorTest {

  private ProjectAssessmentFormValidator projectAssessmentFormValidator;

  @Before
  public void setup() {
    projectAssessmentFormValidator = new ProjectAssessmentFormValidator();
  }

  @Test
  public void validate_whenValidForm_thenNoErrors() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenReadyToBePublishedAndUpdateRequiredMissing_thenError() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    form.setReadyToBePublished(true);
    form.setUpdateRequired(null);

    var errors = new BeanPropertyBindingResult(form, "form");

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).containsExactly(
        entry("updateRequired", Set.of("updateRequired" + FieldValidationErrorCodes.INVALID.getCode()))
    );
  }
}
