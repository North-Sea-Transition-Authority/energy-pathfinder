package uk.co.ogauthority.pathfinder.model.form.project.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
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
  public void validate_whenDecommissioningFieldStageAndFullValidationAndNoSectionsCompleted_thenErrors() {

    var decommissioningFieldStage = FieldStage.DECOMMISSIONING;

    var fullValidationType = ValidationType.FULL;

    var emptySetupForm = new ProjectSetupForm();

    var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

    var hint = new ProjectSetupFormValidationHint(decommissioningFieldStage, fullValidationType);

    ValidationUtils.invokeValidator(validator, emptySetupForm, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).containsExactly(
        entry("wellsIncluded", Set.of(String.format("wellsIncluded%s", FieldValidationErrorCodes.INVALID.getCode()))),
        entry("platformsFpsosIncluded", Set.of(String.format("platformsFpsosIncluded%s", FieldValidationErrorCodes.INVALID.getCode()))),
        entry("subseaInfrastructureIncluded", Set.of(String.format("subseaInfrastructureIncluded%s", FieldValidationErrorCodes.INVALID.getCode()))),
        entry("integratedRigsIncluded", Set.of(String.format("integratedRigsIncluded%s", FieldValidationErrorCodes.INVALID.getCode()))),
        entry("pipelinesIncluded", Set.of(String.format("pipelinesIncluded%s", FieldValidationErrorCodes.INVALID.getCode())))
    );

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).containsExactly(
      entry("wellsIncluded", Set.of(ProjectSetupFormValidator.WELLS_REQUIRED_TEXT)),
      entry("platformsFpsosIncluded", Set.of(ProjectSetupFormValidator.PLATFORMS_FPSOS_REQUIRED_TEXT)),
      entry("subseaInfrastructureIncluded", Set.of(ProjectSetupFormValidator.SUBSEA_INFRASTRUCTURE_REQUIRED_TEXT)),
      entry("integratedRigsIncluded", Set.of(ProjectSetupFormValidator.INTEGRATED_RIGS_REQUIRED_TEXT)),
      entry("pipelinesIncluded", Set.of(ProjectSetupFormValidator.PIPELINES_REQUIRED_TEXT))
    );
  }

  @Test
  public void validate_whenDiscoveryFieldStageAndFulLValidationAndNoSectionsCompleted_thenErrors() {

    var discoveryFieldStage = FieldStage.DISCOVERY;

    var fullValidationType = ValidationType.FULL;

    var emptySetupForm = new ProjectSetupForm();

    var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

    var hint = new ProjectSetupFormValidationHint(discoveryFieldStage, fullValidationType);

    ValidationUtils.invokeValidator(validator, emptySetupForm, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).contains(
        entry("commissionedWellsIncluded", Set.of(String.format("commissionedWellsIncluded%s", FieldValidationErrorCodes.INVALID.getCode())))
    );

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).contains(
        entry("commissionedWellsIncluded", Set.of(ProjectSetupFormValidator.COMMISSIONED_WELLS_REQUIRED_TEXT))
    );
  }

  @Test
  public void validate_whenDevelopmentFieldStageAndFulLValidationAndNoSectionsCompleted_thenErrors() {

    var discoveryFieldStage = FieldStage.DEVELOPMENT;

    var fullValidationType = ValidationType.FULL;

    var emptySetupForm = new ProjectSetupForm();

    var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

    var hint = new ProjectSetupFormValidationHint(discoveryFieldStage, fullValidationType);

    ValidationUtils.invokeValidator(validator, emptySetupForm, errors, hint);

    var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(fieldErrors).contains(
        entry("commissionedWellsIncluded", Set.of(String.format("commissionedWellsIncluded%s", FieldValidationErrorCodes.INVALID.getCode())))
    );

    var fieldErrorMessages = ValidatorTestingUtil.extractErrorMessages(errors);

    assertThat(fieldErrorMessages).contains(
        entry("commissionedWellsIncluded", Set.of(ProjectSetupFormValidator.COMMISSIONED_WELLS_REQUIRED_TEXT))
    );
  }

  @Test
  public void validate_whenPartialValidationAndNoSectionsCompleted_fieldStageSmokeTest_thenNoErrors() {
    Arrays.stream(FieldStage.values()).forEach(fieldStage -> {
      var partialValidationType = ValidationType.PARTIAL;

      var emptySetupForm = new ProjectSetupForm();

      var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

      var hint = new ProjectSetupFormValidationHint(fieldStage, partialValidationType);

      ValidationUtils.invokeValidator(validator, emptySetupForm, errors, hint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

      assertThat(fieldErrors).isEmpty();
    });
  }

  @Test
  public void validate_whenAllSectionsCompleted_validationTypeAndFieldStageSmokeTest_thenNoErrors() {

    var setupForm = ProjectTaskListSetupTestUtil.getProjectSetupFormWithAllSectionsAnswered();

    var errors =  new BeanPropertyBindingResult(setupForm, "form");

    Arrays.stream(FieldStage.values()).forEach(fieldStage -> List.of(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      var hint = new ProjectSetupFormValidationHint(fieldStage, validationType);

      ValidationUtils.invokeValidator(validator, setupForm, errors, hint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

      assertThat(fieldErrors).isEmpty();
    }));
  }
}
