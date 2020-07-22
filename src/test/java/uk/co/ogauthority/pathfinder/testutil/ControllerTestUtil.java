package uk.co.ogauthority.pathfinder.testutil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

public class ControllerTestUtil {

  private ControllerTestUtil() {
    throw new AssertionError();
  }

  /**
   * Use in controller tests to force a validator to return pre-defined errors during a request
   * @param validator that should return errors
   * @param fieldsWithErrors list of field ids that the validator should return errors for
   */
  public static void mockValidatorErrors(Validator validator, List<String> fieldsWithErrors) {

    doAnswer(invocation -> {
      BindingResult result = invocation.getArgument(1);
      fieldsWithErrors.forEach(field ->
          result.rejectValue(field, "fake.code", "fake message"));
      return result;
    }).when(validator).validate(any(), any());

  }

  /**
   * Use in controller tests to force a smart validator to return pre-defined errors during a request
   * @param validator that should return errors
   * @param fieldsWithErrors list of field ids that the validator should return errors for
   */
  public static void mockSmartValidatorErrors(SmartValidator validator, List<String> fieldsWithErrors) {

    doAnswer(invocation -> {
      BindingResult result = invocation.getArgument(1);
      fieldsWithErrors.forEach(field ->
          result.rejectValue(field, "fake.code", "fake message"));
      return result;
    }).when(validator).validate(any(), any(), any());

  }
}
