package uk.co.ogauthority.pathfinder.publicdata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.ogauthority.pathfinder.testutil.CampaignInformationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.CampaignProjectTestUtil;
import uk.co.ogauthority.pathfinder.testutil.SelectableProjectTestUtil;

class InfrastructureProjectCampaignJsonTest {

  @Test
  void from_isPartOfCampaignIsTrueAndCampaignProjectsIsNotNull() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    campaignInformation.setIsPartOfCampaign(true);

    var campaignProject1 = CampaignProjectTestUtil.newBuilder()
        .withProject(SelectableProjectTestUtil.newBuilder()
            .withProjectId(1)
            .withPublished(true)
            .build())
        .build();

    var campaignProject2 = CampaignProjectTestUtil.newBuilder()
        .withProject(SelectableProjectTestUtil.newBuilder()
            .withProjectId(2)
            .withPublished(true)
            .build())
        .build();

    var campaignProject3 = CampaignProjectTestUtil.newBuilder()
        .withProject(SelectableProjectTestUtil.newBuilder()
            .withProjectId(3)
            .withPublished(false)
            .build())
        .build();

    var infrastructureProjectCampaignJson =
        InfrastructureProjectCampaignJson.from(campaignInformation, List.of(campaignProject1, campaignProject2, campaignProject3));

    var expectedInfrastructureProjectCampaignJson = new InfrastructureProjectCampaignJson(
        campaignInformation.getScopeDescription(),
        true,
        Set.of(1, 2)
    );

    assertThat(infrastructureProjectCampaignJson).isEqualTo(expectedInfrastructureProjectCampaignJson);
  }

  @Test
  void from_isPartOfCampaignIsTrueAndCampaignProjectsIsNotNullAndAllCampaignProjectProjectsAreNotPublished() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    campaignInformation.setIsPartOfCampaign(true);

    var campaignProject = CampaignProjectTestUtil.newBuilder()
        .withProject(SelectableProjectTestUtil.newBuilder()
            .withProjectId(1)
            .withPublished(false)
            .build())
        .build();

    var infrastructureProjectCampaignJson =
        InfrastructureProjectCampaignJson.from(campaignInformation, List.of(campaignProject));

    var expectedInfrastructureProjectCampaignJson = new InfrastructureProjectCampaignJson(
        campaignInformation.getScopeDescription(),
        true,
        null
    );

    assertThat(infrastructureProjectCampaignJson).isEqualTo(expectedInfrastructureProjectCampaignJson);
  }

  @Test
  void from_isPartOfCampaignIsTrueAndCampaignProjectsIsNull() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    campaignInformation.setIsPartOfCampaign(true);

    var infrastructureProjectCampaignJson = InfrastructureProjectCampaignJson.from(campaignInformation, null);

    var expectedInfrastructureProjectCampaignJson = new InfrastructureProjectCampaignJson(
        campaignInformation.getScopeDescription(),
        true,
       null
    );

    assertThat(infrastructureProjectCampaignJson).isEqualTo(expectedInfrastructureProjectCampaignJson);
  }

  @Test
  void from_isPartOfCampaignIsFalseAndCampaignProjectsIsNotNull() {
    var campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    campaignInformation.setIsPartOfCampaign(false);

    var campaignProject1 = CampaignProjectTestUtil.newBuilder()
        .withProject(SelectableProjectTestUtil.newBuilder()
            .withProjectId(1)
            .withPublished(true)
            .build())
        .build();

    var infrastructureProjectCampaignJson =
        InfrastructureProjectCampaignJson.from(campaignInformation, List.of(campaignProject1));

    var expectedInfrastructureProjectCampaignJson = new InfrastructureProjectCampaignJson(
        campaignInformation.getScopeDescription(),
        false,
        null
    );

    assertThat(infrastructureProjectCampaignJson).isEqualTo(expectedInfrastructureProjectCampaignJson);
  }
}
