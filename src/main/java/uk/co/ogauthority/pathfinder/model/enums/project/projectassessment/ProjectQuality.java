package uk.co.ogauthority.pathfinder.model.enums.project.projectassessment;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum ProjectQuality {

  POOR("Poor"),
  ADEQUATE("Adequate"),
  GOOD("Good");

  private final String displayName;

  ProjectQuality(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, ProjectQuality::getDisplayName));
  }
}
