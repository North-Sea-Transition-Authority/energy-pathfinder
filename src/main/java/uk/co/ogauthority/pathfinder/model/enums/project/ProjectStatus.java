package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum ProjectStatus {
  DRAFT("Draft"),
  QA("QA"),
  PUBLISHED("Published"),
  ARCHIVED("Archived");

  private static final Set<ProjectStatus> POST_SUBMISSION_PROJECT_STATUSES = EnumSet.of(QA, PUBLISHED, ARCHIVED);

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

  public static Set<ProjectStatus> getPostSubmissionProjectStatuses() {
    return POST_SUBMISSION_PROJECT_STATUSES;
  }
}
