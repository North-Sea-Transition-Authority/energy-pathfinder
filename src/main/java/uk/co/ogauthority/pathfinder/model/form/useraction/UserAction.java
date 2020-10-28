package uk.co.ogauthority.pathfinder.model.form.useraction;

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
}
