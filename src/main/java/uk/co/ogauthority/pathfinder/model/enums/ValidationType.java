package uk.co.ogauthority.pathfinder.model.enums;

import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;
import uk.co.ogauthority.pathfinder.model.form.validation.PartialValidation;

public enum ValidationType {

  FULL("Save and complete", FullValidation.class),
  PARTIAL("Save and complete later", PartialValidation.class);

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
