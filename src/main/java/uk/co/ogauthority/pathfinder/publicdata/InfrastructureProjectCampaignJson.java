package uk.co.ogauthority.pathfinder.publicdata;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import uk.co.ogauthority.pathfinder.model.entity.project.SelectableProject;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;

record InfrastructureProjectCampaignJson(
    String scope,
    Boolean partOfExistingInfrastructureProjectCampaign,
    Set<Integer> existingCampaignInfrastructureProjectIds
) {

  static InfrastructureProjectCampaignJson from(
      CampaignInformation campaignInformation,
      Collection<CampaignProject> campaignProjects
  ) {
    var scope = campaignInformation.getScopeDescription();
    var partOfExistingInfrastructureProjectCampaign = campaignInformation.isPartOfCampaign();
    Set<Integer> existingCampaignInfrastructureProjectIds = null;
    if (partOfExistingInfrastructureProjectCampaign && campaignProjects != null) {
      var publishedSelectableProjectIds = campaignProjects
          .stream()
          .map(CampaignProject::getProject)
          .filter(SelectableProject::isPublished)
          .map(SelectableProject::getProjectId)
          .collect(Collectors.toSet());

      if (!publishedSelectableProjectIds.isEmpty()) {
        existingCampaignInfrastructureProjectIds = publishedSelectableProjectIds;
      }
    }

    return new InfrastructureProjectCampaignJson(
        scope,
        partOfExistingInfrastructureProjectCampaign,
        existingCampaignInfrastructureProjectIds
    );
  }
}
