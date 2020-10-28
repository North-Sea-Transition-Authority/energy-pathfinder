package uk.co.ogauthority.pathfinder.model.form.useraction;

public class Link extends UserAction {

  private final String screenReaderText;

  public Link(String prompt, String url, boolean isEnabled, String screenReaderText) {
    super(prompt, url, UserActionType.LINK, isEnabled);
    this.screenReaderText = screenReaderText;
  }

  public String getScreenReaderText() {
    return screenReaderText;
  }
}
