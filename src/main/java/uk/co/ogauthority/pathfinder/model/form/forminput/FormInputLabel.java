package uk.co.ogauthority.pathfinder.model.form.forminput;

import org.apache.commons.lang3.StringUtils;

public final class FormInputLabel {
  private final String label;

  public FormInputLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public String getInitCappedLabel() {
    return StringUtils.capitalize(label);
  }
}
