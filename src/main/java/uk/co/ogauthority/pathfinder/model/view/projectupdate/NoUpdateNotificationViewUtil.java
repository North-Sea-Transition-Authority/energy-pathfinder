package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.NoUpdateNotification;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class NoUpdateNotificationViewUtil {

  private NoUpdateNotificationViewUtil() {
    throw new IllegalStateException("NoUpdateNotificationViewUtil is a utility class and should not be instantiated.");
  }

  public static NoUpdateNotificationView from(NoUpdateNotification noUpdateNotification,
                                              WebUserAccount submittedByUser) {
    var noUpdateNotificationView = new NoUpdateNotificationView();
    noUpdateNotificationView.setSupplyChainReason(noUpdateNotification.getSupplyChainReason());
    noUpdateNotificationView.setRegulatorReason(noUpdateNotification.getRegulatorReason());
    noUpdateNotificationView.setSubmittedDate(
        DateUtil.formatInstant(noUpdateNotification.getProjectUpdate().getToDetail().getCreatedDatetime()));
    noUpdateNotificationView.setSubmittedByUserName(submittedByUser.getFullName());
    noUpdateNotificationView.setSubmittedByUserEmailAddress(submittedByUser.getEmailAddress());
    return noUpdateNotificationView;
  }
}
