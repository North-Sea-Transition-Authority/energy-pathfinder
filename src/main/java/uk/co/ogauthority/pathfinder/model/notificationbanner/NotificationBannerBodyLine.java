package uk.co.ogauthority.pathfinder.model.notificationbanner;

import java.io.Serializable;

public class NotificationBannerBodyLine implements Serializable {

  public static final String DEFAULT_CLASS = "govuk-body";

  private final String lineText;
  private final String lineClass;

  public NotificationBannerBodyLine(String lineText, String lineClass) {
    this.lineText = lineText;
    this.lineClass = lineClass;
  }

  public static NotificationBannerBodyLine withDefaultClass(String lineText) {
    return new NotificationBannerBodyLine(lineText, DEFAULT_CLASS);
  }

  public String getLineText() {
    return lineText;
  }

  public String getLineClass() {
    return lineClass;
  }
}
