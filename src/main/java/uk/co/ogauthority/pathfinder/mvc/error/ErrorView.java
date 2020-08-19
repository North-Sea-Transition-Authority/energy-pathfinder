package uk.co.ogauthority.pathfinder.mvc.error;

public enum ErrorView {

  DEFAULT_ERROR("error/error"),
  PAGE_NOT_FOUND("error/404"),
  UNAUTHORISED("error/403");

  private final String viewName;

  ErrorView(String viewName) {
    this.viewName = viewName;
  }

  public String getViewName() {
    return viewName;
  }
}
