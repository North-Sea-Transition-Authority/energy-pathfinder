package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity;

import java.util.ArrayList;
import java.util.List;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class CollaborationOpportunityViewUtilCommon {

  private CollaborationOpportunityViewUtilCommon() {
    throw new IllegalStateException("CollaborationOpportunityViewUtilCommon is a utility class and should not be instantiated");
  }

  public static CollaborationOpportunityViewCommon populateView(
      CollaborationOpportunityViewCommon view,
      CollaborationOpportunityCommon opportunity,
      boolean includeSummaryLinks,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews,
      String editUrl,
      String deleteUrl
  ) {

    var projectId = opportunity.getProjectDetail().getProject().getId();

    view.setDisplayOrder(displayOrder);
    view.setId(opportunity.getId());
    view.setProjectId(projectId);

    view.setFunction(
        opportunity.getFunction() != null
            ? new StringWithTag(opportunity.getFunction().getDisplayName(), Tag.NONE)
            : new StringWithTag(opportunity.getManualFunction(), Tag.NOT_FROM_LIST)
    );
    view.setDescriptionOfWork(opportunity.getDescriptionOfWork());
    view.setUrgentResponseNeeded(StringDisplayUtil.yesNoFromBoolean(opportunity.getUrgentResponseNeeded()));

    view.setContactName(opportunity.getName());
    view.setContactPhoneNumber(opportunity.getPhoneNumber());
    view.setContactEmailAddress(opportunity.getEmailAddress());
    view.setContactJobTitle(opportunity.getJobTitle());

    view.setUploadedFileViews(uploadedFileViews);

    ArrayList<SummaryLink> summaryLinks = new ArrayList<>();
    if (includeSummaryLinks) {
      final var editLink = new SummaryLink(
          SummaryLinkText.EDIT.getDisplayName(),
          editUrl
      );
      summaryLinks.add(editLink);

      final var removeLink = new SummaryLink(
          SummaryLinkText.DELETE.getDisplayName(),
          deleteUrl
      );
      summaryLinks.add(removeLink);
    }

    view.setSummaryLinks(summaryLinks);

    return view;
  }

  public static CollaborationOpportunityViewCommon populateView(
      CollaborationOpportunityViewCommon opportunityView,
      CollaborationOpportunityCommon opportunity,
      boolean includeSummaryLinks,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews,
      String editUrl,
      String deleteUrl,
      Boolean isValid
  ) {
    final var populatedOpportunityView = populateView(
        opportunityView,
        opportunity,
        includeSummaryLinks,
        displayOrder,
        uploadedFileViews,
        editUrl,
        deleteUrl
    );
    populatedOpportunityView.setIsValid(isValid);
    return populatedOpportunityView;
  }
}
