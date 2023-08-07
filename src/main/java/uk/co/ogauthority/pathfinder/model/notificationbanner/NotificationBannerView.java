package uk.co.ogauthority.pathfinder.model.notificationbanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.notificationbanner.NotificationBannerType;

public class NotificationBannerView implements Serializable {

  private final String title;
  private final List<NotificationBannerBodyLine> bodyLines;
  private final NotificationBannerType bannerType;
  private final NotificationBannerLink bannerLink;

  public static class BannerBuilder {
    private final String title;
    private final List<NotificationBannerBodyLine> bodyLines;
    private final NotificationBannerType bannerType;
    private  NotificationBannerLink bannerLink;

    public BannerBuilder(String title, NotificationBannerType bannerType) {
      this.title = title;
      this.bodyLines = new ArrayList<>();
      this.bannerType = bannerType;
    }

    public BannerBuilder addBodyLine(NotificationBannerBodyLine bodyLine) {
      this.bodyLines.add(bodyLine);
      return this;
    }

    public BannerBuilder addLink(NotificationBannerLink bannerLink) {
      this.bannerLink = bannerLink;
      return this;
    }

    public NotificationBannerView build() {
      return new NotificationBannerView(this);
    }
  }

  private NotificationBannerView(BannerBuilder bannerBuilder) {
    this.title = bannerBuilder.title;
    this.bodyLines = bannerBuilder.bodyLines;
    this.bannerType = bannerBuilder.bannerType;
    this.bannerLink = bannerBuilder.bannerLink;
  }

  public String getTitle() {
    return title;
  }

  public List<NotificationBannerBodyLine> getBodyLines() {
    return bodyLines;
  }

  public NotificationBannerType getBannerType() {
    return bannerType;
  }

  public NotificationBannerLink getBannerLink() {
    return bannerLink;
  }

}
