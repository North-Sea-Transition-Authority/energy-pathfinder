package uk.co.ogauthority.pathfinder.service.project.commissionedwell;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.commissionedwell.CommissionedWellFormValidator;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CommissionedWellTestUtil;

@ExtendWith(MockitoExtension.class)
class CommissionedWellScheduleValidationServiceTest {

  @Mock
  private CommissionedWellFormValidator commissionedWellFormValidator;

  @Mock
  private ValidationService validationService;

  private CommissionedWellScheduleValidationService commissionedWellScheduleValidationService;

  @BeforeEach
  void setup() {
    commissionedWellScheduleValidationService = new CommissionedWellScheduleValidationService(
        commissionedWellFormValidator,
        validationService
    );
  }

  @Test
  void isFormValid_validationTypeSmokeTest_whenValidForm_thenTrue() {

    var commissionedWellForm = CommissionedWellTestUtil.getCompleteCommissionedWellForm();

    var emptyBindingResult = new BeanPropertyBindingResult(commissionedWellForm, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      when(validationService.validate(eq(commissionedWellForm), any(), eq(validationType)))
          .thenReturn(emptyBindingResult);

      var isFormValid = commissionedWellScheduleValidationService.isFormValid(
          commissionedWellForm,
          validationType
      );

      assertTrue(isFormValid);
    });
  }

  @Test
  void isFormValid_validationTypeSmokeTest_whenInvalidForm_thenFalse() {

    var commissionedWellForm = CommissionedWellTestUtil.getCompleteCommissionedWellForm();

    var bindingResultWithError = new BeanPropertyBindingResult(commissionedWellForm, "form");
    bindingResultWithError.addError(new FieldError("Error", "ErrorMessage", "default message"));

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      when(validationService.validate(eq(commissionedWellForm), any(), eq(validationType)))
          .thenReturn(bindingResultWithError);

      var isFormValid = commissionedWellScheduleValidationService.isFormValid(
          commissionedWellForm,
          validationType
      );

      assertFalse(isFormValid);
    });
  }

  @Test
  void areAllFormsValid_validationTypeSmokeTest_whenValidForms_thenTrue() {

    var commissionedWellForm = CommissionedWellTestUtil.getCompleteCommissionedWellForm();

    var emptyBindingResult = new BeanPropertyBindingResult(commissionedWellForm, "form");

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      when(validationService.validate(eq(commissionedWellForm), any(), eq(validationType)))
          .thenReturn(emptyBindingResult);

      var areFormsValid = commissionedWellScheduleValidationService.areAllFormsValid(List.of(commissionedWellForm), validationType);

      assertTrue(areFormsValid);
    });

  }

  @Test
  void areAllFormsValid_validationTypeSmokeTest_whenInvalidForms_thenFalse() {

    var commissionedWellForm = CommissionedWellTestUtil.getCompleteCommissionedWellForm();

    var bindingResultWithError = new BeanPropertyBindingResult(commissionedWellForm, "form");
    bindingResultWithError.addError(new FieldError("Error", "ErrorMessage", "default message"));

    Arrays.asList(ValidationType.FULL, ValidationType.PARTIAL).forEach(validationType -> {

      when(validationService.validate(eq(commissionedWellForm), any(), eq(validationType)))
          .thenReturn(bindingResultWithError);

      var areFormsValid = commissionedWellScheduleValidationService.areAllFormsValid(List.of(commissionedWellForm), validationType);

      assertFalse(areFormsValid);
    });
  }
}
