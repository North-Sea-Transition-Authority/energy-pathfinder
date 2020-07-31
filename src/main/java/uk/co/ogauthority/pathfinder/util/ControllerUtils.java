package uk.co.ogauthority.pathfinder.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.Checkable;

/**
 * Utility class to provide useful methods for controllers.
 */
public class ControllerUtils {

  private ControllerUtils() {
    throw new AssertionError();
  }

  public static Map<String, String> asCheckboxMap(List<? extends Checkable> items) {
    return items.stream()
        .sorted(Comparator.comparing(Checkable::getDisplayOrder))
        .collect(Collectors.toMap(Checkable::getIdentifier, Checkable::getDisplayName, (x,y) -> y, LinkedHashMap::new));
  }
}
