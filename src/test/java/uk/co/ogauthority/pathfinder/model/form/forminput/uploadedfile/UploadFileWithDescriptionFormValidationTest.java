package uk.co.ogauthority.pathfinder.model.form.forminput.uploadedfile;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring.LengthRestrictedStringValidator;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LengthRestrictedStringValidator.class)
class UploadFileWithDescriptionFormValidationTest extends AbstractControllerTest {

  private static final Set<ValidationType> PERMITTED_VALIDATION_GROUPS = Set.of(
      ValidationType.FULL,
      ValidationType.PARTIAL,
      ValidationType.MANDATORY_UPLOAD
  );

  private static final Integer MAX_DESCRIPTION_SIZE = 2000;

  private static Validator validator;
  private TestUploadFileWithDescriptionForm form;

  @BeforeAll
  static void setup() {
    var factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @BeforeEach
  void setUp() {
    form = getValidUploadFileWithDescriptionForm();
  }

  @Test
  void validation_whenDescriptionMoreThanAllowedLimit_thenError() {
    form.setUploadedFileDescription(ValidatorTestingUtil.getStringOfLength(MAX_DESCRIPTION_SIZE + 1));
    validateForm_smokeValidationGroups_constraintViolationNotEmpty(form);
  }

  @Test
  void validation_whenDescriptionLessThanAllowedLimit_thenNoError() {
    form.setUploadedFileDescription(ValidatorTestingUtil.getStringOfLength(MAX_DESCRIPTION_SIZE - 1));
    validateForm_smokeValidationGroups_constraintViolationEmpty(form);
  }

  @Test
  void validation_whenDescriptionEqualToAllowedLimit_thenNoError() {
    form.setUploadedFileDescription(ValidatorTestingUtil.getStringOfLength(MAX_DESCRIPTION_SIZE));
    validateForm_smokeValidationGroups_constraintViolationEmpty(form);
  }

  private TestUploadFileWithDescriptionForm getValidUploadFileWithDescriptionForm() {
    TestUploadFileWithDescriptionForm form = new TestUploadFileWithDescriptionForm();
    form.setUploadedFileId("1234");
    form.setUploadedFileDescription("test");
    form.setUploadedFileInstant(Instant.now());
    return form;
  }

  private void validateForm_smokeValidationGroups_constraintViolationNotEmpty(TestUploadFileWithDescriptionForm form) {
    Arrays.stream(ValidationType.values())
        .filter(validationType -> validationType.getValidationClass() != null)
        .forEach(validationType -> { var constraintViolations = validator.validate(form, validationType.getValidationClass());
          if(PERMITTED_VALIDATION_GROUPS.contains(validationType)) {
            assertThat(constraintViolations).extracting(ConstraintViolation::getMessage)
                .containsExactly(String.format("The file description must be %s characters or fewer", MAX_DESCRIPTION_SIZE));
          } else {
            assertThat(constraintViolations).isEmpty();
          }
        });
  }

  private void validateForm_smokeValidationGroups_constraintViolationEmpty(TestUploadFileWithDescriptionForm form) {
    Arrays.stream(ValidationType.values())
        .filter(validationType -> validationType.getValidationClass() != null)
        .forEach(validationType -> { var constraintViolations = validator.validate(form, validationType.getValidationClass());
            assertThat(constraintViolations).isEmpty();
        });
  }

}