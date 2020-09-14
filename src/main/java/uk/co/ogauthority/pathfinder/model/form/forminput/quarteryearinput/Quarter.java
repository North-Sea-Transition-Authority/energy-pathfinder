package uk.co.ogauthority.pathfinder.model.form.forminput.quarteryearinput;

import java.util.Arrays;
import java.util.Map;
import uk.co.ogauthority.pathfinder.util.StreamUtil;

public enum Quarter {
  Q1(1),
  Q2(2),
  Q3(3),
  Q4(4);

  private final Integer displayValue;

  Quarter(Integer displayValue) {
    this.displayValue = displayValue;
  }

  public Integer getDisplayValue() {
    return displayValue;
  }

  public static Map<String, Integer> getAllAsMap() {
    return Arrays.stream(values())
        .collect(StreamUtil.toLinkedHashMap(Enum::name, Quarter::getDisplayValue));
  }
}
