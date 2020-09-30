package uk.co.ogauthority.pathfinder.util.summary;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.form.fds.ErrorItem;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

public class SummaryUtil {

  public static final Integer DEFAULT_DISPLAY_ORDER = 1;

  private SummaryUtil() {
    throw new IllegalStateException("SummaryUtil is a utility class and should not be instantiated");
  }

  public static List<ErrorItem> getErrors(
                                          List<SummaryItem> views,
                                          String emptyListError,
                                          String fieldNameError,
                                          String errorMessage) {
    if (views.isEmpty()) {
      return Collections.singletonList(
          new ErrorItem(
              DEFAULT_DISPLAY_ORDER,
              emptyListError,
              emptyListError
          )
      );
    }

    return views.stream().filter(v -> !v.isValid()).map(v ->
        new ErrorItem(
            v.getDisplayOrder(),
            String.format(fieldNameError, v.getDisplayOrder()),
            String.format(errorMessage, v.getDisplayOrder())
        )
    ).collect(Collectors.toList());
  }

  public static ValidationResult validateViews(List<SummaryItem> views) {
    if (views.isEmpty()) {
      return ValidationResult.INVALID;
    }

    return views.stream().anyMatch(v -> !v.isValid())
        ? ValidationResult.INVALID
        : ValidationResult.VALID;
  }
}
