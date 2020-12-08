package uk.co.ogauthority.pathfinder.model.form.useraction;

import java.util.Objects;

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

    DashboardLink that = (DashboardLink) o;

    return Objects.equals(screenReaderText, that.screenReaderText)
        && Objects.equals(applyNotVisitedClass, that.applyNotVisitedClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), screenReaderText, applyNotVisitedClass);
  }
}
