package uk.co.ogauthority.pathfinder.model.enums;

import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.MandatoryUploadValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;

/**
 * Use to indicate how a form or entity should be validated.
 * FULL and PARTIAL used in {@link uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver} to
 * indicate how to save a form.
 * NO_VALIDATION used for Summary pages to skip validating when not completing the section.
 */
public enum ValidationType {

  FULL("Save and complete", FullValidation.class),
  PARTIAL("Save and complete later", PartialValidation.class),
  NO_VALIDATION("", null),
  MANDATORY_UPLOAD("", MandatoryUploadValidation.class);

  private final String buttonText;

  private final Class<?> validationClass;

  ValidationType(String buttonText, Class<?> validationClass) {
    this.buttonText = buttonText;
    this.validationClass = validationClass;
  }

  public String getButtonText() {
    return buttonText;
  }

  public Class<?> getValidationClass() {
    return validationClass;
  }
}
