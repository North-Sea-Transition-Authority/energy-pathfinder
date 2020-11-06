package uk.co.ogauthority.pathfinder.model.view.projectoperator;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorViewUtilTest {

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Test
  public void from_whenOrganisationGroupPopulated_thenViewPropertySet() {

    final var organisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "name", "short name");

    final var projectOperator = new ProjectOperator(
        projectDetail,
        organisationGroup
    );

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getOrganisationGroupName()).isEqualTo(organisationGroup.getName());
  }

  @Test
  public void from_whenOrganisationGroupNotPopulated_thenViewProperyIsBlankString() {

    final var projectOperator = new ProjectOperator(
        projectDetail,
        null
    );

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getOrganisationGroupName()).isEqualTo(StringUtils.EMPTY);
  }

}