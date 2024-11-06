package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.model.enums.project.UkcsArea;
import uk.co.ogauthority.pathfinder.testutil.DevUkTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

class InfrastructureProjectFieldJsonTest {

  @Test
  void from() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();
    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var infrastructureProjectFieldJson = InfrastructureProjectFieldJson.from(projectLocation);

    var expectedInfrastructureProjectFieldJson = new InfrastructureProjectFieldJson(
        projectLocation.getField().getFieldName(),
        projectLocation.getFieldType().name(),
        projectLocation.getField().getUkcsArea().name()
    );

    assertThat(infrastructureProjectFieldJson).isEqualTo(expectedInfrastructureProjectFieldJson);
  }

  @Test
  void from_ukcsAreaIsNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var field = DevUkTestUtil.getDevUkField(1, "Test field", 2, null);
    projectLocation.setField(field);

    var infrastructureProjectFieldJson = InfrastructureProjectFieldJson.from(projectLocation);

    assertThat(infrastructureProjectFieldJson.ukcsArea()).isNull();
  }

  @Test
  void from_ukcsAreaIsNotNull() {
    var projectDetail = ProjectUtil.getPublishedProjectDetails();

    var projectLocation = ProjectLocationTestUtil.getProjectLocation(projectDetail);

    var field = DevUkTestUtil.getDevUkField(1, "Test field", 2, UkcsArea.CNS);
    projectLocation.setField(field);

    var infrastructureProjectFieldJson = InfrastructureProjectFieldJson.from(projectLocation);

    assertThat(infrastructureProjectFieldJson.ukcsArea()).isEqualTo(UkcsArea.CNS.name());
  }
}
