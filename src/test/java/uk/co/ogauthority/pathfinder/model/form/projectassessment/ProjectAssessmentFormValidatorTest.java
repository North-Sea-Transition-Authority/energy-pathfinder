package uk.co.ogauthority.pathfinder.model.form.projectassessment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
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
  public void validate_whenNotReadyToBePublished_thenNoErrors() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    form.setReadyToBePublished(false);

    final var canRequestUpdate = true;

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectAssessmentValidationHint = new ProjectAssessmentValidationHint(
        canRequestUpdate,
        ProjectType.INFRASTRUCTURE
    );

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors, projectAssessmentValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenReadyToBePublishedAndCanRequestUpdateAndUpdateRequiredNull_thenError() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    form.setReadyToBePublished(true);
    form.setUpdateRequired(null);

    final var canRequestUpdate = true;

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectAssessmentValidationHint = new ProjectAssessmentValidationHint(
        canRequestUpdate,
        ProjectType.INFRASTRUCTURE
    );

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors, projectAssessmentValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).containsExactly(
        entry("updateRequired", Set.of("updateRequired" + FieldValidationErrorCodes.INVALID.getCode()))
    );
  }

  @Test
  public void validate_whenReadyToBePublishedAndCanRequestUpdateAndUpdateRequiredNotNull_thenNoErrors() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    form.setReadyToBePublished(true);
    form.setUpdateRequired(false);

    final var canRequestUpdate = true;

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectAssessmentValidationHint = new ProjectAssessmentValidationHint(
        canRequestUpdate,
        ProjectType.INFRASTRUCTURE
    );

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors, projectAssessmentValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }

  @Test
  public void validate_whenReadyToBePublishedAndCannotRequestUpdateAndUpdateRequiredNotNull_thenError() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    form.setReadyToBePublished(true);
    form.setUpdateRequired(true);

    final var canRequestUpdate = false;

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectAssessmentValidationHint = new ProjectAssessmentValidationHint(
        canRequestUpdate,
        ProjectType.INFRASTRUCTURE
    );

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors, projectAssessmentValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).containsExactly(
        entry("updateRequired", Set.of("updateRequired" + FieldValidationErrorCodes.INVALID.getCode()))
    );
  }

  @Test
  public void validate_whenReadyToBePublishedAndCannotRequestUpdateAndUpdateRequiredNull_thenNoErrors() {
    var form = ProjectAssessmentTestUtil.createProjectAssessmentForm();
    form.setReadyToBePublished(true);
    form.setUpdateRequired(null);

    final var canRequestUpdate = false;

    var errors = new BeanPropertyBindingResult(form, "form");
    var projectAssessmentValidationHint = new ProjectAssessmentValidationHint(
        canRequestUpdate,
        ProjectType.INFRASTRUCTURE
    );

    ValidationUtils.invokeValidator(projectAssessmentFormValidator, form, errors, projectAssessmentValidationHint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(fieldErrors).isEmpty();
  }
}
