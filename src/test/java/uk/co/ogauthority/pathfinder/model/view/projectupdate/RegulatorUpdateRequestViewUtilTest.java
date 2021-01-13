package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateRequestViewUtilTest {

  @Test
  public void from() {
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    var requestedByUser = UserTestingUtil.getWebUserAccount();

    var regulatorUpdateRequestView = RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, requestedByUser);

    assertThat(regulatorUpdateRequestView.getUpdateReason()).isEqualTo(regulatorUpdateRequest.getUpdateReason());
    assertThat(regulatorUpdateRequestView.getDeadlineDate()).isEqualTo(DateUtil.formatInstant(regulatorUpdateRequest.getRequestedInstant()));
    assertThat(regulatorUpdateRequestView.getRequestedByUserName()).isEqualTo(requestedByUser.getFullName());
    assertThat(regulatorUpdateRequestView.getRequestedByUserEmailAddress()).isEqualTo(requestedByUser.getEmailAddress());
  }
}
