package uk.co.ogauthority.pathfinder.model.enums.subscription;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.Displayable;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum SubscriptionManagementOption implements Displayable {

  UNSUBSCRIBE(0, "I want to unsubscribe"),
  UPDATE_SUBSCRIPTION(10, "I want to change my preferences");

  private final String displayText;
  private final int displayOrder;

  SubscriptionManagementOption(int displayOrder, String displayText) {
    this.displayOrder = displayOrder;
    this.displayText = displayText;
  }

  public String getDisplayText() {
    return displayText;
  }

  @Override
  public String getDisplayName() {
    return displayText;
  }

  @Override
  public int getDisplayOrder() {
    return displayOrder;
  }

  public static Map<String, String> getAllAsMap() {
    return Arrays.stream(values())
        .sorted(Comparator.comparing(SubscriptionManagementOption::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(Enum::name, SubscriptionManagementOption::getDisplayText));
  }
}
