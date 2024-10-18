package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getProjectDetails();

    var expectedInfrastructureProjectJson = new InfrastructureProjectJson(
        projectDetail.getProject().getId(),
        projectDetail.getStatus(),
        projectDetail.getVersion()
    );

    assertThat(InfrastructureProjectJson.from(projectDetail))
        .isEqualTo(expectedInfrastructureProjectJson);
  }
}
