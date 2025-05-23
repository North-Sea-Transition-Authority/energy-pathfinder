package uk.co.ogauthority.pathfinder.testutil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Provides access to common methods for testing Spring validators.
 */
public class ValidatorTestingUtil {

  private ValidatorTestingUtil() {
    throw new AssertionError();
  }

  /**
   * Apply a validator to a form object and return a map of field id -> set of field error codes.
   */
  public static Map<String, Set<String>> getFormValidationErrors(Validator validator, Object form) {

    var errors = new BeanPropertyBindingResult(form, "form");
    ValidationUtils.invokeValidator(validator, form, errors);

    return errors.getFieldErrors().stream()
        .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(FieldError::getCode, Collectors.toSet())));

  }

  /**
   * Return a map of field id -> set of field error codes for a BindingResult
   */
  public static Map<String, Set<String>> extractErrors(BindingResult bindingResult) {

    return bindingResult.getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(FieldError::getCode, Collectors.toSet())
        ));

  }

  /**
   * Return a map of field id -> set of field error messages for a BindingResult
   */
  public static Map<String, Set<String>> extractErrorMessages(BindingResult bindingResult) {

    return bindingResult.getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())
        ));

  }

  public static String over4000Chars() {
    return getStringOfLength(4001);
  }

  public static String exactly4000chars() {
    return getStringOfLength(4000);
  }

  public static String getStringOfLength(Integer length) {
    return StringUtils.repeat("a", length);
  }

}
