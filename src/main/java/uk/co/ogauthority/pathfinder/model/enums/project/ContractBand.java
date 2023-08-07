package uk.co.ogauthority.pathfinder.model.enums.project;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum ContractBand {
  LESS_THAN_25M("Less than £25 million", Set.of(ProjectType.INFRASTRUCTURE)),
  GREATER_THAN_OR_EQUAL_TO_25M("£25 million or more", Set.of(ProjectType.INFRASTRUCTURE)),
  LESS_THAN_5M("Less than £5 million", Set.of(ProjectType.FORWARD_WORK_PLAN)),
  GREATER_THAN_OR_EQUAL_TO_5M("£5 million or more", Set.of(ProjectType.FORWARD_WORK_PLAN));

  private final String displayName;
  private final Set<ProjectType> projectTypes;

  ContractBand(String displayName, Set<ProjectType> projectTypes) {
    this.displayName = displayName;
    this.projectTypes = projectTypes;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Map<String, String> getAllAsMap(ProjectType projectType) {
    return Arrays.stream(values())
        .filter(contractBand -> contractBand.projectTypes.contains(projectType))
        .collect(StreamUtil.toLinkedHashMap(Enum::name, ContractBand::getDisplayName));
  }
}
