package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class NoUpdateNotificationViewUtilTest {

  @Test
  public void from() {
    var noUpdateNotification = ProjectUpdateTestUtil.createNoUpdateNotification();
    var submittedByUser = UserTestingUtil.getWebUserAccount();

    var noUpdateNotificationView = NoUpdateNotificationViewUtil.from(noUpdateNotification, submittedByUser);

    assertThat(noUpdateNotificationView.getSupplyChainReason()).isEqualTo(noUpdateNotification.getSupplyChainReason());
    assertThat(noUpdateNotificationView.getRegulatorReason()).isEqualTo(noUpdateNotification.getRegulatorReason());
    assertThat(noUpdateNotificationView.getSubmittedDate()).isEqualTo(
        DateUtil.formatInstant(noUpdateNotification.getProjectUpdate().getToDetail().getCreatedDatetime())
    );
    assertThat(noUpdateNotificationView.getSubmittedByUserName()).isEqualTo(submittedByUser.getFullName());
    assertThat(noUpdateNotificationView.getSubmittedByUserEmailAddress()).isEqualTo(submittedByUser.getEmailAddress());
  }
}
