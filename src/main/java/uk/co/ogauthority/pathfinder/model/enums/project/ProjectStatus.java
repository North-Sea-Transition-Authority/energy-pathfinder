package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum ProjectStatus {
  DRAFT("Draft"),
  QA("QA"),
  PUBLISHED("Published");

  private final String displayName;

  ProjectStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, ProjectStatus::getDisplayName));
  }
}
