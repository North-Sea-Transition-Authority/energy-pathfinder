package uk.co.ogauthority.pathfinder.model.view.projectoperator;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

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
    assertThat(projectOperatorView.getOperatorName()).isEqualTo(organisationGroup.getName());
  }

  @Test
  public void from_whenOrganisationGroupNotPopulated_thenViewPropertyIsBlankString() {

    final var projectOperator = new ProjectOperator(
        projectDetail,
        null
    );

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getOperatorName()).isEqualTo(StringUtils.EMPTY);
  }

  @Test
  public void from_whenIsPublishedAsOperatorIsTrue_thenViewPropertyIsYes() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(true);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getIsPublishedAsOperator()).isEqualTo(StringDisplayUtil.YES);
  }

  @Test
  public void from_whenIsPublishedAsOperatorIsFalse_thenViewPropertyIsNo() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(false);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getIsPublishedAsOperator()).isEqualTo(StringDisplayUtil.NO);
  }

  @Test
  public void from_whenIsPublishedAsOperatorIsNull_thenViewPropertyIsEmptyString() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(null);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getIsPublishedAsOperator()).isEmpty();
  }

  @Test
  public void from_whenIsPublishedAsOperatorTrue_thenPublishableOrganisationViewPropertyIsEmptyString() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(true);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getPublishableOrganisationName()).isEmpty();
  }

  @Test
  public void from_whenIsPublishedAsOperatorNull_thenPublishableOrganisationViewPropertyIsEmptyString() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(null);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getPublishableOrganisationName()).isEmpty();

  }

  @Test
  public void from_whenIsPublishedAsOperatorFalseAndPublishableOrganisationProvided_thenPublishableOrganisationViewPropertyIsPopulated() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(false);

    final var publishableOrganisation = TeamTestingUtil.generateOrganisationUnit(100, "name", new PortalOrganisationGroup());
    projectOperator.setPublishableOrganisationUnit(publishableOrganisation);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getPublishableOrganisationName()).isEqualTo(publishableOrganisation.getName());
  }

  @Test
  public void from_whenIsPublishedAsOperatorFalseAndPublishableOrganisationNotProvided_thenPublishableOrganisationViewPropertyIsEmptyString() {

    final var projectOperator = new ProjectOperator();
    projectOperator.setIsPublishedAsOperator(false);
    projectOperator.setPublishableOrganisationUnit(null);

    var projectOperatorView = ProjectOperatorViewUtil.from(projectOperator);
    assertThat(projectOperatorView.getPublishableOrganisationName()).isEmpty();
  }

  @Test
  public void getIsPublishedAsOperatorDisplayString_whenTrue_thenStringYes() {
    final var displayString = ProjectOperatorViewUtil.getIsPublishedAsOperatorDisplayString(true);
    assertThat(displayString).isEqualTo(StringDisplayUtil.YES);
  }

  @Test
  public void getIsPublishedAsOperatorDisplayString_whenFalse_thenStringNo() {
    final var displayString = ProjectOperatorViewUtil.getIsPublishedAsOperatorDisplayString(false);
    assertThat(displayString).isEqualTo(StringDisplayUtil.NO);
  }

  @Test
  public void getIsPublishedAsOperatorDisplayString_whenNull_thenEmptyString() {
    final var displayString = ProjectOperatorViewUtil.getIsPublishedAsOperatorDisplayString(null);
    assertThat(displayString).isEmpty();
  }

  @Test
  public void getPublishableOrganisationName_whenIsPublishedAsOperatorTrue_thenEmptyString() {
   final var publishableOrganisationName = ProjectOperatorViewUtil.getPublishableOrganisationName(true, null);
   assertThat(publishableOrganisationName).isEmpty();
  }

  @Test
  public void getPublishableOrganisationName_whenIsPublishedAsOperatorNull_thenEmptyString() {
    final var publishableOrganisationName = ProjectOperatorViewUtil.getPublishableOrganisationName(null, null);
    assertThat(publishableOrganisationName).isEmpty();
  }

  @Test
  public void getPublishableOrganisationName_whenIsPublishedAsOperatorFalseAndPublishableOrganisationProvided_thenPublishableOrganisationName() {
    final var publishableOrganisation = TeamTestingUtil.generateOrganisationUnit(100, "name", new PortalOrganisationGroup());
    final var publishableOrganisationName = ProjectOperatorViewUtil.getPublishableOrganisationName(
        false,
        publishableOrganisation
    );
    assertThat(publishableOrganisationName).isEqualTo(publishableOrganisation.getName());
  }

  @Test
  public void getPublishableOrganisationName_whenIsPublishedAsOperatorFalseAndPublishableOrganisationNull_thenEmptyString() {
    final var publishableOrganisationName = ProjectOperatorViewUtil.getPublishableOrganisationName(
        false,
        null
    );
    assertThat(publishableOrganisationName).isEmpty();
  }

}