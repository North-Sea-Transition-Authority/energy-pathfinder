package uk.co.ogauthority.pathfinder.model.view.campaigninformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignProject;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class CampaignInformationViewUtil {

  private CampaignInformationViewUtil() {
    throw new IllegalStateException("CampaignInformationViewUtil is a util class and should not be instantiated");
  }

  public static CampaignInformationView from(CampaignInformation campaignInformation,
                                             List<CampaignProject> campaignProjects) {

    final var campaignInformationView = new CampaignInformationView();
    campaignInformationView.setScopeDescription(campaignInformation.getScopeDescription());

    final var isIncludedInCampaignAsString = StringDisplayUtil.yesNoFromBoolean(campaignInformation.isPartOfCampaign());
    campaignInformationView.setIsIncludedInCampaign(isIncludedInCampaignAsString);

    List<String> campaignProjectDisplayNames = new ArrayList<>();

    if (BooleanUtils.isTrue(campaignInformation.isPartOfCampaign()) && !campaignProjects.isEmpty()) {
      campaignProjectDisplayNames = campaignProjects
          .stream()
          .map(campaignProject -> campaignProject.getProject().getProjectDisplayName())
          .sorted(Comparator.comparing(String::toLowerCase))
          .collect(Collectors.toList());
    }

    campaignInformationView.setCampaignProjects(campaignProjectDisplayNames);

    return campaignInformationView;
  }
}
