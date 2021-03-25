package uk.co.ogauthority.pathfinder.model.form.useraction;

public enum ButtonType {
  PRIMARY(""),
  SECONDARY("secondary"),
  BLUE("blue");

  private final String modifierValue;

  ButtonType(String modifierValue) {
    this.modifierValue = modifierValue;
  }

  public String getModifierValue() {
    return modifierValue;
  }
}
