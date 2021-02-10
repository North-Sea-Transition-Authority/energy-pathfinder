package uk.co.ogauthority.pathfinder.model.view.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.testutil.ProjectUpdateTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateRequestViewUtilTest {

  @Test
  public void from_withDeadlineDate() {
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    var requestedByUser = UserTestingUtil.getWebUserAccount();

    var regulatorUpdateRequestView = RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, requestedByUser);

    checkCommonFields(regulatorUpdateRequest, regulatorUpdateRequestView, requestedByUser);
    assertThat(regulatorUpdateRequestView.getDeadlineDate()).isEqualTo(DateUtil.formatDate(regulatorUpdateRequest.getDeadlineDate()));
  }

  @Test
  public void from_withNullDeadlineDate() {
    var regulatorUpdateRequest = ProjectUpdateTestUtil.createRegulatorUpdateRequest();
    regulatorUpdateRequest.setDeadlineDate(null);
    var requestedByUser = UserTestingUtil.getWebUserAccount();

    var regulatorUpdateRequestView = RegulatorUpdateRequestViewUtil.from(regulatorUpdateRequest, requestedByUser);

    checkCommonFields(regulatorUpdateRequest, regulatorUpdateRequestView, requestedByUser);
    assertThat(regulatorUpdateRequestView.getDeadlineDate()).isEmpty();
  }

  private static void checkCommonFields(RegulatorUpdateRequest regulatorUpdateRequest,
                                        RegulatorUpdateRequestView regulatorUpdateRequestView,
                                        WebUserAccount requestedByUser) {
    assertThat(regulatorUpdateRequestView.getUpdateReason()).isEqualTo(regulatorUpdateRequest.getUpdateReason());
    assertThat(regulatorUpdateRequestView.getDeadlineDate()).isEqualTo(DateUtil.formatDate(regulatorUpdateRequest.getDeadlineDate()));
    assertThat(regulatorUpdateRequestView.getRequestedByUserName()).isEqualTo(requestedByUser.getFullName());
    assertThat(regulatorUpdateRequestView.getRequestedByUserEmailAddress()).isEqualTo(requestedByUser.getEmailAddress());
  }
}
