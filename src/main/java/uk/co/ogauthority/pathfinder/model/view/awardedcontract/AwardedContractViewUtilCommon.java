package uk.co.ogauthority.pathfinder.model.view.awardedcontract;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class AwardedContractViewUtilCommon {

  private AwardedContractViewUtilCommon() {
    throw new IllegalStateException("AwardedContractViewUtil is a util class and should not be instantiated");
  }

  public static AwardedContractViewCommon populateView(AwardedContractViewCommon view,
                                                       AwardedContractCommon awardedContract,
                                                       boolean includeSummaryLinks,
                                                       Integer displayOrder,
                                                       SummaryLink editUrl,
                                                       SummaryLink deleteUrl,
                                                       PortalOrganisationGroup addedByPortalOrganisationGroup,
                                                       boolean isValid
  ) {
    view.setDisplayOrder(displayOrder);
    view.setId(awardedContract.getId());

    var projectId = awardedContract.getProjectDetail().getProject().getId();
    view.setProjectId(projectId);
    view.setContractorName(awardedContract.getContractorName());

    var contractFunction = (awardedContract.getContractFunction() != null)
        ? new StringWithTag(awardedContract.getContractFunction().getDisplayName(), Tag.NONE)
        : new StringWithTag(awardedContract.getManualContractFunction(), Tag.NOT_FROM_LIST);
    view.setContractFunction(contractFunction);

    view.setDescriptionOfWork(awardedContract.getDescriptionOfWork());
    view.setDateAwarded(DateUtil.formatDate(awardedContract.getDateAwarded()));

    var contractBand = (awardedContract.getContractBand() != null)
        ? awardedContract.getContractBand().getDisplayName()
        : null;
    view.setContractBand(contractBand);

    view.setContactName(awardedContract.getContactName());
    view.setContactPhoneNumber(awardedContract.getPhoneNumber());
    view.setContactJobTitle(awardedContract.getJobTitle());
    view.setContactEmailAddress(awardedContract.getEmailAddress());

    var summaryLinks = new ArrayList<SummaryLink>();
    if (includeSummaryLinks) {
      summaryLinks.add(editUrl);
      summaryLinks.add(deleteUrl);
    }

    view.setSummaryLinks(summaryLinks);
    view.setAddedByPortalOrganisationGroup(resolvePortalOrganisationName(addedByPortalOrganisationGroup));
    view.setIsValid(isValid);

    return view;
  }

  private static String resolvePortalOrganisationName(PortalOrganisationGroup portalOrganisationGroup) {
    if (StringUtils.isBlank(portalOrganisationGroup.getName())) {
      return "Unknown organisation";
    }
    return portalOrganisationGroup.getName();
  }
}
