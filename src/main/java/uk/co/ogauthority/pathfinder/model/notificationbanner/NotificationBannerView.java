package uk.co.ogauthority.pathfinder.model.notificationbanner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.enums.notificationbanner.NotificationBannerType;

public class NotificationBannerView implements Serializable {

  private final NotificationBannerTitle title;
  private final NotificationBannerHeading heading;
  private final List<NotificationBannerBodyLine> bodyLines;
  private final NotificationBannerType bannerType;
  private final NotificationBannerLink bannerLink;

  public static class BannerBuilder {
    private final NotificationBannerTitle title;
    private final NotificationBannerHeading heading;
    private final List<NotificationBannerBodyLine> bodyLines;
    private final NotificationBannerType bannerType;
    private  NotificationBannerLink bannerLink;

    public BannerBuilder(NotificationBannerTitle title,
                         NotificationBannerHeading heading,
                         NotificationBannerType bannerType) {
      this.title = title;
      this.heading = heading;
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
    this.heading = bannerBuilder.heading;
    this.bodyLines = bannerBuilder.bodyLines;
    this.bannerType = bannerBuilder.bannerType;
    this.bannerLink = bannerBuilder.bannerLink;
  }

  public String getTitleAsString() {
    return title.getTitle();
  }

  public String getHeadingAsString() {
    return heading.getHeading();
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
