package uk.co.ogauthority.pathfinder.model.form.project.campaigninformation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class CampaignInformationForm {

  @NotEmpty(message = "Provide the scope of the campaign", groups = FullValidation.class)
  private String scopeDescription;

  @NotNull(
      message = "Select if this project is already part of a campaign with a published Energy Pathfinder projects",
      groups = FullValidation.class
  )
  private Boolean publishedCampaign;

  public String getScopeDescription() {
    return scopeDescription;
  }

  public void setScopeDescription(String scopeDescription) {
    this.scopeDescription = scopeDescription;
  }

  public Boolean isPublishedCampaign() {
    return publishedCampaign;
  }

  public Boolean getPublishedCampaign() {
    return isPublishedCampaign();
  }

  public void setPublishedCampaign(Boolean publishedCampaign) {
    this.publishedCampaign = publishedCampaign;
  }
}