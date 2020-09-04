package uk.co.ogauthority.pathfinder.util.validation;

import uk.co.ogauthority.pathfinder.service.controller.ControllerHelperService;

/**
 * Use to indicate the result of custom validation that doesn't use a BindingResult.
 * Currently used when doing validation without the {@link ControllerHelperService}.
 */
public enum ValidationResult {
  VALID,
  INVALID,
  NOT_VALIDATED;
}
