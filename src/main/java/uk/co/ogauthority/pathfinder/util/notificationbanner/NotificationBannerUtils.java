package uk.co.ogauthority.pathfinder.util.notificationbanner;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.ogauthority.pathfinder.model.enums.notificationbanner.NotificationBannerType;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerHeading;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerLink;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerTitle;
import uk.co.ogauthority.pathfinder.model.notificationbanner.NotificationBannerView;

public class NotificationBannerUtils {

  public static final String NOTIFICATION_BANNER_OBJECT_NAME = "notificationBannerView";

  private NotificationBannerUtils() {
    throw new IllegalStateException("This is a helper class, it should not be instantiated");
  }

  public static void successBannerWithLink(NotificationBannerTitle title,
                                           NotificationBannerHeading heading,
                                           NotificationBannerLink bannerLink,
                                           RedirectAttributes redirectAttributes) {
    var notificationBannerView = new NotificationBannerView
        .BannerBuilder(title, heading, NotificationBannerType.SUCCESS)
        .addLink(bannerLink)
        .build();
    redirectAttributes.addFlashAttribute(NOTIFICATION_BANNER_OBJECT_NAME, notificationBannerView);
  }
}
