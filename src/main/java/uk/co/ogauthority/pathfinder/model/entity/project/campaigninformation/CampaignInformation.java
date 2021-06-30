package uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation;

import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;

@Entity
@Table(name = "campaign_information")
public class CampaignInformation extends ProjectDetailEntity {

  private String scopeDescription;

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

  public void setPublishedCampaign(boolean publishedCampaign) {
    this.publishedCampaign = publishedCampaign;
  }
}