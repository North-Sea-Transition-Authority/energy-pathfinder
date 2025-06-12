package uk.co.ogauthority.pathfinder.model.form.project.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStage;
import uk.co.ogauthority.pathfinder.model.enums.project.FieldStageSubCategory;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.testutil.ProjectTaskListSetupTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ProjectSetupFormValidatorTest {

  private ProjectSetupFormValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ProjectSetupFormValidator();
  }

  @Test
  void validate_whenDecommissioningFieldStageSubCategoryAndFullValidationAndNoSectionsCompleted_thenErrors() {
    var oilAndGasFieldStage = FieldStage.OIL_AND_GAS;
    var decommissioningFieldStageSubCategory = FieldStageSubCategory.DECOMMISSIONING;

    var fullValidationType = ValidationType.FULL;

    var emptySetupForm = new ProjectSetupForm();

    var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

    var hint = new ProjectSetupFormValidationHint(oilAndGasFieldStage, decommissioningFieldStageSubCategory, fullValidationType);

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
  void validate_whenDiscoveryFieldStageAndFulLValidationAndNoSectionsCompleted_thenErrors() {
    var oilAndGasFieldStage = FieldStage.OIL_AND_GAS;
    var discoveryFieldStageSubCategory = FieldStageSubCategory.DISCOVERY;

    var fullValidationType = ValidationType.FULL;

    var emptySetupForm = new ProjectSetupForm();

    var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

    var hint = new ProjectSetupFormValidationHint(oilAndGasFieldStage, discoveryFieldStageSubCategory, fullValidationType);

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
  void validate_whenDevelopmentFieldStageSubCategoryAndFulLValidationAndNoSectionsCompleted_thenErrors() {
    var oilAndGasFieldStage = FieldStage.OIL_AND_GAS;
    var discoveryFieldStageSubCategory = FieldStageSubCategory.DEVELOPMENT;

    var fullValidationType = ValidationType.FULL;

    var emptySetupForm = new ProjectSetupForm();

    var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

    var hint = new ProjectSetupFormValidationHint(oilAndGasFieldStage, discoveryFieldStageSubCategory, fullValidationType);

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

  @ParameterizedTest
  @EnumSource(FieldStage.class)
  void validate_whenPartialValidationAndNoSectionsCompleted_fieldStageSubCategorySmokeTest_thenNoErrors(FieldStage fieldStage) {
    Arrays.stream(FieldStageSubCategory.values()).forEach(fieldStageSubCategory -> {
      var partialValidationType = ValidationType.PARTIAL;

      var emptySetupForm = new ProjectSetupForm();

      var errors =  new BeanPropertyBindingResult(emptySetupForm, "form");

      var hint = new ProjectSetupFormValidationHint(fieldStage, fieldStageSubCategory, partialValidationType);

      ValidationUtils.invokeValidator(validator, emptySetupForm, errors, hint);

      var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

      assertThat(fieldErrors).isEmpty();
    });
  }

  @ParameterizedTest
  @EnumSource(FieldStage.class)
  void validate_whenAllSectionsCompleted_validationTypeAndFieldStageSubCategorySmokeTest_thenNoErrors(FieldStage fieldStage) {

    var setupForm = ProjectTaskListSetupTestUtil.getProjectSetupFormWithAllSectionsAnswered();

    var errors =  new BeanPropertyBindingResult(setupForm, "form");

    Arrays.stream(FieldStageSubCategory.values())
        .forEach(fieldStageSubCategory -> List.of(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

          var hint = new ProjectSetupFormValidationHint(fieldStage, fieldStageSubCategory, validationType);

          ValidationUtils.invokeValidator(validator, setupForm, errors, hint);

          var fieldErrors = ValidatorTestingUtil.extractErrors(errors);

          assertThat(fieldErrors).isEmpty();
        }));
  }
}
