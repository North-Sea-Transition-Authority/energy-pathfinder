package uk.co.ogauthority.pathfinder.model.notificationbanner;

import java.io.Serializable;

public class NotificationBannerLink implements Serializable {

  private final String linkUrl;
  private final String linkText;

  public NotificationBannerLink(String linkUrl, String linkText) {
    this.linkUrl = linkUrl;
    this.linkText = linkText;
  }

  public String getLinkUrl() {
    return linkUrl;
  }

  public String getLinkText() {
    return linkText;
  }

}
