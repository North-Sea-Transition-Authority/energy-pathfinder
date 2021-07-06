package uk.co.ogauthority.pathfinder.model.view.campaigninformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CampaignInformationView {

  private String scopeDescription;

  private String isIncludedInCampaign;

  private List<String> campaignProjects = new ArrayList<>();

  public String getScopeDescription() {
    return scopeDescription;
  }

  public void setScopeDescription(String scopeDescription) {
    this.scopeDescription = scopeDescription;
  }

  public String getIsIncludedInCampaign() {
    return isIncludedInCampaign;
  }

  public void setIsIncludedInCampaign(String isIncludedInCampaign) {
    this.isIncludedInCampaign = isIncludedInCampaign;
  }

  public List<String> getCampaignProjects() {
    return campaignProjects;
  }

  public void setCampaignProjects(List<String> campaignProjects) {
    this.campaignProjects = campaignProjects;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof CampaignInformationView)) {
      return false;
    }

    CampaignInformationView that = (CampaignInformationView) o;
    return Objects.equals(scopeDescription, that.scopeDescription)
        && Objects.equals(isIncludedInCampaign, that.isIncludedInCampaign)
        && Objects.equals(campaignProjects, that.campaignProjects);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        scopeDescription,
        isIncludedInCampaign,
        campaignProjects
    );
  }
}
