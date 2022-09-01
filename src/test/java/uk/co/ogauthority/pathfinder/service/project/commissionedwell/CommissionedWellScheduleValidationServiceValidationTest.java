package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import javax.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellValidatorHint;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellScheduleValidationServiceValidationTest {

  @Mock
  private CommissionedWellFormValidator commissionedWellFormValidator;

  private CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;

  @BeforeEach
  void setup() {
    var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
    var validationService = new ValidationService(validator);

    commissionedWellScheduleValidationService = new CommissionedWellScheduleValidationService(
        commissionedWellFormValidator,
        validationService
    );
  }

  /**
   * This test is to assert that no spring annotations are used on the form and the validate action interacts with the
   * custom validator. If validation annotations are used in the future then this test will fail due to a behaviour change.
   */
  @Test
  void validate_whenEmptyForm_assertNoErrorsFromSpringAnnotationsAndInteractionWithCustomValidator() {

    var commissionedWellForm = CommissionedWellTestUtil.getEmptyCommissionedWellForm();
    var bindingResult = new BeanPropertyBindingResult(commissionedWellForm, "form");

    Arrays.asList(ValidationType.PARTIAL, ValidationType.FULL).forEach(validationType -> {

      commissionedWellScheduleValidationService.validate(
          commissionedWellForm,
          bindingResult,
          validationType
      );

      verify(commissionedWellFormValidator, times(1)).validate(
          commissionedWellForm,
          bindingResult,
          new CommissionedWellValidatorHint(validationType)
      );

      var fieldErrors = ValidatorTestingUtil.extractErrors(bindingResult);

      assertThat(fieldErrors).isEmpty();
    });
  }
}