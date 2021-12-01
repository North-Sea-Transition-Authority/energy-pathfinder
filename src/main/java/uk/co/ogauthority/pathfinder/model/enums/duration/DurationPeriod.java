package uk.co.ogauthority.pathfinder.model.enums.duration;

import java.util.Collections;
import java.util.Map;

public enum DurationPeriod {
  DAYS("Days", "Day"),
  WEEKS("Weeks", "Week"),
  MONTHS("Months", "Month"),
  YEARS("Years", "Year");

  private final String displayNamePlural;
  private final String displayNameSingular;

  DurationPeriod(String displayNamePlural, String displayNameSingular) {
    this.displayNamePlural = displayNamePlural;
    this.displayNameSingular = displayNameSingular;
  }

  public String getDisplayNamePlural() {
    return displayNamePlural;
  }

  public String getDisplayNameSingular() {
    return displayNameSingular;
  }

  public static Map<String, String> getEntryAsMap(DurationPeriod durationPeriod) {
    return Collections.singletonMap(durationPeriod.name(), durationPeriod.getDisplayNamePlural());
  }
}
