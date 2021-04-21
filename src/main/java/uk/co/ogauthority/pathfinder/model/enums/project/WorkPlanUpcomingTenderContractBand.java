package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum WorkPlanUpcomingTenderContractBand {
  LESS_THAN_5M("Less than £5 million"),
  GREATER_THAN_OR_EQUAL_TO_5M("£5 million or more");

  private final String displayName;

  WorkPlanUpcomingTenderContractBand(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, WorkPlanUpcomingTenderContractBand::getDisplayName));
  }
}
