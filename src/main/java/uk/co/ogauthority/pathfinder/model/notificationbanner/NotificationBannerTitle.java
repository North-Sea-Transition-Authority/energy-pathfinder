package uk.co.ogauthority.pathfinder.model.notificationbanner;

import java.io.Serializable;

public class NotificationBannerTitle implements Serializable {

  private final String title;

  public NotificationBannerTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }
}
