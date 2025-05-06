package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;

public class CampaignProjectTestUtil {

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private Integer id = 1;
    private CampaignInformation campaignInformation = CampaignInformationTestUtil.createCampaignInformation();
    private SelectableProject project = SelectableProjectTestUtil.newBuilder().build();

    private Builder() {
    }

    public Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder withCampaignInformation(CampaignInformation campaignInformation) {
      this.campaignInformation = campaignInformation;
      return this;
    }

    public Builder withProject(SelectableProject project) {
      this.project = project;
      return this;
    }

    public CampaignProject build() {
      var campaignProject = new CampaignProject(id);

      campaignProject.setCampaignInformation(campaignInformation);
      campaignProject.setProject(project);

      return campaignProject;
    }
  }
}
