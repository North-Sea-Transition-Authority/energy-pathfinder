package uk.co.ogauthority.pathfinder.model.form.project.campaigninformation;

import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import uk.co.ogauthority.pathfinder.model.form.validation.FullValidation;

public class CampaignInformationForm {

  @NotEmpty(message = "Provide the scope of the campaign", groups = FullValidation.class)
  private String scopeDescription;

  @NotNull(
      message = "Select if this project is already part of a campaign with any published Energy Pathfinder projects",
      groups = FullValidation.class
  )
  private Boolean isPartOfCampaign;

  private List<Integer> projectsIncludedInCampaign = Collections.emptyList();

  private String projectSelect;

  public String getScopeDescription() {
    return scopeDescription;
  }

  public void setScopeDescription(String scopeDescription) {
    this.scopeDescription = scopeDescription;
  }

  public Boolean isPartOfCampaign() {
    return isPartOfCampaign;
  }

  public Boolean getIsPartOfCampaign() {
    return isPartOfCampaign();
  }

  public void setIsPartOfCampaign(Boolean isPartOfCampaign) {
    this.isPartOfCampaign = isPartOfCampaign;
  }

  public List<Integer> getProjectsIncludedInCampaign() {
    return projectsIncludedInCampaign;
  }

  public void setProjectsIncludedInCampaign(List<Integer> projectsIncludedInCampaign) {
    this.projectsIncludedInCampaign = projectsIncludedInCampaign;
  }

  public String getProjectSelect() {
    return projectSelect;
  }

  public void setProjectSelect(String projectSelect) {
    this.projectSelect = projectSelect;
  }
}