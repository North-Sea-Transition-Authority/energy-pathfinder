package uk.co.ogauthority.pathfinder.model.enums;

public enum MeasurementUnits {
  METRES("metre", "metres", "in metres"),
  METRIC_TONNE("metric tonne", "metric tonnes", "in metric tonnes");

  private final String singular;
  private final String plural;
  private final String screenReaderSuffix;

  MeasurementUnits(String singular, String plural, String screenReaderSuffix) {
    this.singular = singular;
    this.plural = plural;
    this.screenReaderSuffix = screenReaderSuffix;
  }

  public String getSingular() {
    return singular;
  }

  public String getPlural() {
    return plural;
  }

  public String getScreenReaderSuffix() {
    return screenReaderSuffix;
  }
}
