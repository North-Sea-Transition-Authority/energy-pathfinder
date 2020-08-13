package uk.co.ogauthority.pathfinder.model.form.validation.lengthrestrictedstring;

public class LengthRestrictedStringTestForm {

  @LengthRestrictedString(messagePrefix = "The string value")
  private String defaultRestrictedStringValue;

  @LengthRestrictedString(messagePrefix = "The string value", max = 10)
  private String restrictedTo10StringValue;

  @LengthRestrictedString(messagePrefix = "The string value", max = 1)
  private String restrictedTo1StringValue;

  public LengthRestrictedStringTestForm() {}

  public String getDefaultRestrictedStringValue() {
    return defaultRestrictedStringValue;
  }

  public void setDefaultRestrictedStringValue(String defaultRestrictedStringValue) {
    this.defaultRestrictedStringValue = defaultRestrictedStringValue;
  }

  public String getRestrictedTo10StringValue() {
    return restrictedTo10StringValue;
  }

  public void setRestrictedTo10StringValue(String restrictedTo10StringValue) {
    this.restrictedTo10StringValue = restrictedTo10StringValue;
  }

  public String getRestrictedTo1StringValue() {
    return restrictedTo1StringValue;
  }

  public void setRestrictedTo1StringValue(String restrictedTo1StringValue) {
    this.restrictedTo1StringValue = restrictedTo1StringValue;
  }
}
