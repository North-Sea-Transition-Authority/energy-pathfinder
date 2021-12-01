package uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetailEntity;
import uk.co.ogauthority.pathfinder.service.entityduplication.ParentEntity;

@Entity
@Table(name = "campaign_information")
public class CampaignInformation extends ProjectDetailEntity implements ParentEntity {

  private String scopeDescription;

  private Boolean isPartOfCampaign;

  public String getScopeDescription() {
    return scopeDescription;
  }

  public void setScopeDescription(String scopeDescription) {
    this.scopeDescription = scopeDescription;
  }

  public Boolean isPartOfCampaign() {
    return isPartOfCampaign;
  }

  public void setIsPartOfCampaign(Boolean publishedCampaign) {
    this.isPartOfCampaign = publishedCampaign;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    if (!super.equals(o)) {
      return false;
    }

    CampaignInformation that = (CampaignInformation) o;
    return Objects.equals(scopeDescription, that.scopeDescription)
        && Objects.equals(isPartOfCampaign, that.isPartOfCampaign);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        scopeDescription,
        isPartOfCampaign
    );
  }
}