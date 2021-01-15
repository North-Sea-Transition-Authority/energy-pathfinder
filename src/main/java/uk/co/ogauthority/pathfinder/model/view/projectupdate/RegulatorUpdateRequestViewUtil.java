package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class RegulatorUpdateRequestViewUtil {

  private RegulatorUpdateRequestViewUtil() {
    throw new IllegalStateException("RegulatorUpdateRequestViewUtil is a utility class and should not be instantiated.");
  }

  public static RegulatorUpdateRequestView from(RegulatorUpdateRequest regulatorUpdateRequest,
                                                WebUserAccount requestedByUser) {
    var regulatorUpdateRequestView = new RegulatorUpdateRequestView();
    regulatorUpdateRequestView.setUpdateReason(regulatorUpdateRequest.getUpdateReason());
    regulatorUpdateRequestView.setDeadlineDate(DateUtil.formatDate(regulatorUpdateRequest.getDeadlineDate()));
    regulatorUpdateRequestView.setRequestedDate(DateUtil.formatInstant(regulatorUpdateRequest.getRequestedInstant()));
    regulatorUpdateRequestView.setRequestedByUserName(requestedByUser.getFullName());
    regulatorUpdateRequestView.setRequestedByUserEmailAddress(requestedByUser.getEmailAddress());
    return regulatorUpdateRequestView;
  }
}
