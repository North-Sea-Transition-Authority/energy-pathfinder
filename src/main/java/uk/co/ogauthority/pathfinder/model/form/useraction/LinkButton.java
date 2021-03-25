package uk.co.ogauthority.pathfinder.model.form.useraction;

import java.util.Objects;

public class LinkButton extends UserAction {

  private final ButtonType buttonType;

  public LinkButton(String prompt, String url, boolean isEnabled, ButtonType buttonType) {
    super(prompt, url, UserActionType.LINK_BUTTON, isEnabled);
    this.buttonType = buttonType;
  }

  public ButtonType getButtonType() {
    return buttonType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    LinkButton that = (LinkButton) o;
    return buttonType == that.buttonType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), buttonType);
  }
}
