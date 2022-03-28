package uk.co.ogauthority.pathfinder.model.enums;

import java.util.Optional;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
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

  FULL("Save and complete", FullValidation.class, AnalyticsEventCategory.SAVE_PROJECT_FORM),
  PARTIAL("Save and complete later", PartialValidation.class, AnalyticsEventCategory.SAVE_PROJECT_FORM_COMPLETE_LATER),
  NO_VALIDATION("", null, null),
  MANDATORY_UPLOAD("", MandatoryUploadValidation.class, null);

  private final String buttonText;

  private final Class<?> validationClass;

  private final AnalyticsEventCategory analyticsEventCategory;

  ValidationType(String buttonText, Class<?> validationClass, AnalyticsEventCategory analyticsEventCategory) {
    this.buttonText = buttonText;
    this.validationClass = validationClass;
    this.analyticsEventCategory = analyticsEventCategory;
  }

  public String getButtonText() {
    return buttonText;
  }

  public Class<?> getValidationClass() {
    return validationClass;
  }

  public Optional<AnalyticsEventCategory> getAnalyticsEventCategory() {
    return Optional.ofNullable(analyticsEventCategory);
  }

}
