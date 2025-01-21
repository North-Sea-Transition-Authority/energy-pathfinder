package uk.co.ogauthority.pathfinder.testutil;

import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.campaigninformation.CampaignInformation;
import uk.co.ogauthority.pathfinder.model.form.project.campaigninformation.CampaignInformationForm;

public class CampaignInformationTestUtil {

  public static final Boolean PUBLISHED_CAMPAIGN = true;
  public static final String SCOPE_DESCRIPTION = "My scope description";

  private CampaignInformationTestUtil() {
    throw new IllegalStateException("CampaignInformationTestUtil is a utility class and should not be instantiated");
  }

  public static CampaignInformation createCampaignInformation() {
    return createCampaignInformation(
        null,
        ProjectUtil.getProjectDetails(),
        PUBLISHED_CAMPAIGN,
        SCOPE_DESCRIPTION
    );
  }

  public static CampaignInformation createCampaignInformation(Integer id, ProjectDetail projectDetail) {
    return createCampaignInformation(
        id,
        projectDetail,
        PUBLISHED_CAMPAIGN,
        SCOPE_DESCRIPTION
    );
  }

  public static CampaignInformation createCampaignInformation(
      Integer id,
      ProjectDetail projectDetail,
      Boolean publishedCampaign,
      String scopeDescription
  ) {
    var campaignInformation = new CampaignInformation(id);
    campaignInformation.setProjectDetail(projectDetail);
    campaignInformation.setIsPartOfCampaign(publishedCampaign);
    campaignInformation.setScopeDescription(scopeDescription);
    return campaignInformation;
  }

  public static CampaignInformationForm createCampaignInformationForm(
      Boolean isPartOfCampaign,
      String scopeDescription
  ) {
    var form = new CampaignInformationForm();
    form.setIsPartOfCampaign(isPartOfCampaign);
    form.setScopeDescription(scopeDescription);
    return form;
  }

  public static CampaignInformationForm createCampaignInformationForm() {
    return createCampaignInformationForm(
        PUBLISHED_CAMPAIGN,
        SCOPE_DESCRIPTION
    );
  }
}
