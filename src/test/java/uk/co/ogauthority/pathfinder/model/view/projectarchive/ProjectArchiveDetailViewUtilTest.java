package uk.co.ogauthority.pathfinder.model.view.projectarchive;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.projectarchive.ProjectArchiveDetail;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectArchiveDetailViewUtilTest {

  @Test
  public void from() {
    var projectArchiveDetail = new ProjectArchiveDetail();
    projectArchiveDetail.setArchiveReason("Test archive reason");

    var archivedInstant = Instant.now();
    var archivedByUser = UserTestingUtil.getAuthenticatedUserAccount();

    var projectArchiveDetailView = ProjectArchiveDetailViewUtil.from(
        projectArchiveDetail,
        archivedInstant,
        archivedByUser
    );

    assertThat(projectArchiveDetailView.getArchiveReason()).isEqualTo(projectArchiveDetail.getArchiveReason());
    assertThat(projectArchiveDetailView.getArchivedDate()).isEqualTo(DateUtil.formatInstant(archivedInstant));
    assertThat(projectArchiveDetailView.getArchivedByUser()).isEqualTo(archivedByUser.getFullName());
  }
}
