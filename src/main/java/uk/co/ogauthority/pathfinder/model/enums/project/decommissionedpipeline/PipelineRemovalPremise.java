package uk.co.ogauthority.pathfinder.model.enums.project.decommissionedpipeline;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum PipelineRemovalPremise {

  LEAVE_IN_PLACE("Leave in place"),
  LEAVE_IN_SITU_BURIED_TO_1M("Leave in situ buried to 1m"),
  PARTIAL_REMOVAL_AND_BURY("Partial removal and bury"),
  FULL_REMOVAL("Full removal");

  private final String displayName;

  PipelineRemovalPremise(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, PipelineRemovalPremise::getDisplayName));
  }
}
