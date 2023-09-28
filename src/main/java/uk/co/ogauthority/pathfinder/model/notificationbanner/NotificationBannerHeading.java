package uk.co.ogauthority.pathfinder.model.notificationbanner;

import java.io.Serializable;

public class NotificationBannerHeading implements Serializable {

  private final String heading;

  public NotificationBannerHeading(String heading) {
    this.heading = heading;
  }

  public String getHeading() {
    return heading;
  }
}
