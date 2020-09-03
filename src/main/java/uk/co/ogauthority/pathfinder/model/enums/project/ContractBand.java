package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum ContractBand {
  LESS_THAN_25M("Less than £25 million"),
  GREATER_THAN_OR_EQUAL_TO_25M("£25 million or more");

  private final String displayName;

  ContractBand(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, ContractBand::getDisplayName));
  }
}
