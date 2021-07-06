package uk.co.ogauthority.pathfinder.model.view.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationViewUtilTest {

  private CampaignInformation campaignInformation;

  @Before
  public void setup() {
    campaignInformation = new CampaignInformation();
  }

  @Test
  public void from_whenIsPartOfCampaignTrue_thenAssertYesString() {

    campaignInformation.setIsPartOfCampaign(true);

    final var resultingView = CampaignInformationViewUtil.from(campaignInformation, List.of());

    assertThat(resultingView.getIsIncludedInCampaign()).isEqualTo(StringDisplayUtil.YES);
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  @Test
  public void from_whenIsPartOfCampaignFalse_thenAssertNoString() {

    campaignInformation.setIsPartOfCampaign(false);

    final var resultingView = CampaignInformationViewUtil.from(campaignInformation, List.of());

    assertThat(resultingView.getIsIncludedInCampaign()).isEqualTo(StringDisplayUtil.NO);
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  @Test
  public void from_whenIsPartOfCampaignNull_thenAssertEmptyString() {

    campaignInformation.setIsPartOfCampaign(null);

    final var resultingView = CampaignInformationViewUtil.from(campaignInformation, List.of());

    assertThat(resultingView.getIsIncludedInCampaign()).isEqualTo(StringUtils.EMPTY);
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  @Test
  public void from_whenIsPartOfCampaignFalseAndCampaignProjectsAdded_thenAssertCampaignProjectListEmpty() {

    campaignInformation.setIsPartOfCampaign(false);

    final var resultingView = CampaignInformationViewUtil.from(
        campaignInformation,
        List.of(new CampaignProject())
    );

    assertThat(resultingView.getCampaignProjects()).isEmpty();
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  @Test
  public void from_whenIsPartOfCampaignTrueAndCampaignProjectsAdded_thenAssertCampaignProjectListPopulated() {

    campaignInformation.setIsPartOfCampaign(true);

    final var projectDisplayName = "project display name";

    final var campaignProject = createCampaignProjectWithDisplayName(projectDisplayName);

    final var resultingView = CampaignInformationViewUtil.from(
        campaignInformation,
        List.of(campaignProject)
    );

    assertThat(resultingView.getCampaignProjects()).containsExactly(projectDisplayName);
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  @Test
  public void from_whenIsPartOfCampaignTrueAndNoCampaignProjectsAdded_thenAssertCampaignProjectListEmpty() {

    campaignInformation.setIsPartOfCampaign(true);

    final var resultingView = CampaignInformationViewUtil.from(
        campaignInformation,
        List.of()
    );

    assertThat(resultingView.getCampaignProjects()).isEmpty();
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  @Test
  public void from_whenMultipleCampaignProjectsAdded_confirmSorted() {

    campaignInformation.setIsPartOfCampaign(true);

    final var firstAlphabeticallyDisplayName = "a project display name";
    final var firstAlphabeticallyCampaignProject = createCampaignProjectWithDisplayName(firstAlphabeticallyDisplayName);

    final var lastAlphabeticallyDisplayName = "Z project display name";
    final var lastAlphabeticallyCampaignProject = createCampaignProjectWithDisplayName(lastAlphabeticallyDisplayName);

    final var resultingView = CampaignInformationViewUtil.from(
        campaignInformation,
        List.of(lastAlphabeticallyCampaignProject, firstAlphabeticallyCampaignProject)
    );

    assertThat(resultingView.getCampaignProjects()).containsExactly(
        firstAlphabeticallyDisplayName,
        lastAlphabeticallyDisplayName
    );
    assertCommonViewProperties(resultingView, campaignInformation);
  }

  private void assertCommonViewProperties(CampaignInformationView campaignInformationView,
                                          CampaignInformation sourceCampaignInformation) {
    assertThat(campaignInformationView.getScopeDescription()).isEqualTo(sourceCampaignInformation.getScopeDescription());
  }

  private CampaignProject createCampaignProjectWithDisplayName(String displayName) {

    final var publishedProject = new SelectableProject();
    publishedProject.setProjectDisplayName(displayName);

    final var campaignProject = new CampaignProject();
    campaignProject.setProject(publishedProject);

    return campaignProject;
  }

}