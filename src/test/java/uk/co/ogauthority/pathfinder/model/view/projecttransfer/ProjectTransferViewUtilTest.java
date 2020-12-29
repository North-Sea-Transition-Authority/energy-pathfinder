package uk.co.ogauthority.pathfinder.model.view.projecttransfer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.testutil.ProjectTransferTestUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectTransferViewUtilTest {

  @Test
  public void from() {
    var projectTransfer = ProjectTransferTestUtil.createProjectTransfer();
    var transferredByUser = UserTestingUtil.getAuthenticatedUserAccount();

    var projectTransferView = ProjectTransferViewUtil.from(projectTransfer, transferredByUser);

    assertThat(projectTransferView.getOldOperator()).isEqualTo(projectTransfer.getFromOrganisationGroup().getName());
    assertThat(projectTransferView.getNewOperator()).isEqualTo(projectTransfer.getToOrganisationGroup().getName());
    assertThat(projectTransferView.getTransferReason()).isEqualTo(projectTransfer.getTransferReason());
    assertThat(projectTransferView.getTransferDate()).isEqualTo(DateUtil.formatInstant(projectTransfer.getTransferredInstant()));
    assertThat(projectTransferView.getTransferredByUserName()).isEqualTo(transferredByUser.getFullName());
    assertThat(projectTransferView.getTransferredByUserEmailAddress()).isEqualTo(transferredByUser.getEmailAddress());
  }
}
