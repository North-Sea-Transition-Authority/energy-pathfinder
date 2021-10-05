package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOperatorDisplayNameUtilTest {

  @Test
  public void getProjectOperatorDisplayName_whenNoPublishableOrganisation_thenOnlyProjectOperatorName() {

    final var projectOperator = TeamTestingUtil.generateOrganisationGroup(100, "name", "short name");

    final var projectOperatorDisplayName = ProjectOperatorDisplayNameUtil.getProjectOperatorDisplayName(projectOperator, null);

    assertThat(projectOperatorDisplayName).isEqualTo(projectOperator.getName());
  }

  @Test
  public void getProjectOperatorDisplayName_whenPublishableOrganisation_thenBothOperatorNamesShown() {

    final var projectOperator = TeamTestingUtil.generateOrganisationGroup(100, "group name", "short name");
    final var publishableOperator = TeamTestingUtil.generateOrganisationUnit(200, "unit name", projectOperator);

    final var projectOperatorDisplayName = ProjectOperatorDisplayNameUtil.getProjectOperatorDisplayName(
        projectOperator,
        publishableOperator
    );

    assertThat(projectOperatorDisplayName).isEqualTo(
        String.format("%s (%s)", projectOperator.getName(), publishableOperator.getName())
    );

  }

}