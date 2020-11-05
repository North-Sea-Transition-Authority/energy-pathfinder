package uk.co.ogauthority.pathfinder.model.form.useraction;

public class DashboardLink extends UserAction {

  private final String screenReaderText;

  private final boolean applyNotVisitedClass;

  public DashboardLink(String prompt,
                       String url,
                       boolean isEnabled,
                       String screenReaderText) {
    super(prompt, url, UserActionType.DASHBOARD_LINK, isEnabled);
    this.screenReaderText = screenReaderText;
    this.applyNotVisitedClass = true;
  }

  public String getScreenReaderText() {
    return screenReaderText;
  }

  public boolean isApplyNotVisitedClass() {
    return applyNotVisitedClass;
  }
}
