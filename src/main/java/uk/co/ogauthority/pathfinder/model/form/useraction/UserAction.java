package uk.co.ogauthority.pathfinder.model.form.useraction;

import java.util.Objects;

public abstract class UserAction {

  private final String prompt;

  private final String url;

  private final UserActionType type;

  private final boolean isEnabled;

  public UserAction(String prompt, String url, UserActionType type, boolean isEnabled) {
    this.prompt = prompt;
    this.url = url;
    this.type = type;
    this.isEnabled = isEnabled;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getUrl() {
    return url;
  }

  public UserActionType getType() {
    return type;
  }

  public boolean getEnabled() {
    return isEnabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserAction that = (UserAction) o;
    return isEnabled == that.isEnabled
        && Objects.equals(prompt, that.prompt)
        && Objects.equals(url, that.url)
        && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        prompt,
        url,
        type,
        isEnabled
    );
  }
}
