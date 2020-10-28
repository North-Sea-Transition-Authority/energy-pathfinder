package uk.co.ogauthority.pathfinder.model.form.useraction;

public class LinkButton extends UserAction {

  private final ButtonType buttonType;

  public LinkButton(String prompt, String url, boolean isEnabled, ButtonType buttonType) {
    super(prompt, url, UserActionType.LINK_BUTTON, isEnabled);
    this.buttonType = buttonType;
  }

  public ButtonType getButtonType() {
    return buttonType;
  }
}
