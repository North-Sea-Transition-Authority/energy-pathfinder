package uk.co.ogauthority.pathfinder.model.form.useraction;

import java.util.Objects;

public class Link extends UserAction {

  private final String screenReaderText;

  public Link(String prompt, String url, boolean isEnabled, String screenReaderText) {
    super(prompt, url, UserActionType.LINK, isEnabled);
    this.screenReaderText = screenReaderText;
  }

  public String getScreenReaderText() {
    return screenReaderText;
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

    Link that = (Link) o;

    return Objects.equals(screenReaderText, that.screenReaderText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), screenReaderText);
  }
}
