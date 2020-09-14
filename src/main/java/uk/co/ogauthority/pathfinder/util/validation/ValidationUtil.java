package uk.co.ogauthority.pathfinder.util.validation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.ogauthority.pathfinder.model.form.forminput.FormInputLabel;
import uk.co.ogauthority.pathfinder.model.form.validation.FieldValidationErrorCodes;
import uk.co.ogauthority.pathfinder.model.form.validation.ValidationHint;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class ValidationUtil {

  private ValidationUtil() {
    throw new IllegalStateException("ValidationUtil is a utility class and should not be instantiated");
  }

  /**
   * invoke validator on a nested object while safely pushing and popping the nested object path.
   */
  public static void invokeNestedValidator(Errors errors,
                                           Validator validator,
                                           String targetPath,
                                           Object targetObject,
                                           Object... validationHints) {
    try {
      errors.pushNestedPath(targetPath);
      ValidationUtils.invokeValidator(validator, targetObject, errors, validationHints);
    } finally {
      errors.popNestedPath();
    }
  }

  public static void validateDecimalPlaces(Errors errors, String field, String fieldLabel, int decimalPlaces) {
    if (errors.getFieldValue(field) == null) {
      return;
    }
    var bigDecimal = new BigDecimal(Objects.requireNonNull(errors.getFieldValue(field)).toString());
    if (bigDecimal.stripTrailingZeros().scale() > decimalPlaces) {
      var placePluralised = StringDisplayUtil.pluralise("place", decimalPlaces);
      errors.rejectValue(field, field + FieldValidationErrorCodes.INVALID.getCode(),
          String.format("%s must be %d decimal %s or fewer", fieldLabel, decimalPlaces, placePluralised));
    }
  }

  public static List<Object> extractImplementedValidationHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(hint ->
            Arrays.stream(hint.getClass().getInterfaces())
                .anyMatch(Predicate.isEqual(ValidationHint.class))
        )
        .collect(Collectors.toList());
  }

  public static FormInputLabel extractFormInputLabelFromHints(Object[] validationHints) {
    return Arrays.stream(validationHints)
        .filter(hint -> hint.getClass().equals(FormInputLabel.class))
        .map(hint -> ((FormInputLabel) hint))
        .findFirst()
        .orElse(null);
  }
}
