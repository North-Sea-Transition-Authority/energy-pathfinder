package uk.co.ogauthority.pathfinder.model.enums;

public enum MeasurementUnits {
  METRES("metre", "metres", "(metres)");

  private final String singular;
  private final String plural;
  private final String suffix;

  MeasurementUnits(String singular, String plural, String suffix) {
    this.singular = singular;
    this.plural = plural;
    this.suffix = suffix;
  }

  public String getSingular() {
    return singular;
  }

  public String getPlural() {
    return plural;
  }

  public String getSuffix() {
    return suffix;
  }
}
