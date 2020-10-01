package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.contactdetail.ContactDetailView;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class CollaborationOpportunityViewUtil {

  private CollaborationOpportunityViewUtil() {
    throw new IllegalStateException("CollaborationOpportunityViewUtil is a utility class and should not be instantiated");
  }


  public static CollaborationOpportunityView createView(
      CollaborationOpportunity opportunity,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews
  ) {
    var projectId = opportunity.getProjectDetail().getProject().getId();
    var view = new CollaborationOpportunityView(
        displayOrder,
        opportunity.getId(),
        projectId
    );

    view.setFunction(
        opportunity.getFunction() != null
            ? opportunity.getFunction().getDisplayName()
            : opportunity.getManualFunction()
    );
    view.setDescriptionOfWork(opportunity.getDescriptionOfWork());
    view.setEstimatedServiceDate(DateUtil.formatDate(opportunity.getEstimatedServiceDate()));
    ContactDetailView contactDetailView = new ContactDetailView();
    contactDetailView.setName(opportunity.getName());
    contactDetailView.setPhoneNumber(opportunity.getPhoneNumber());
    contactDetailView.setEmailAddress(opportunity.getEmailAddress());
    contactDetailView.setJobTitle(opportunity.getJobTitle());
    view.setContactDetailView(contactDetailView);

    view.setUploadedFileViews(uploadedFileViews);

    view.setEditLink(
        new SummaryLink(
            SummaryLinkText.EDIT.getDisplayName(),
            ReverseRouter.route(on(CollaborationOpportunitiesController.class).editCollaborationOpportunity(
                projectId,
                opportunity.getId(),
                null
            ))
        )
    );

    view.setDeleteLink(
        new SummaryLink(
            SummaryLinkText.DELETE.getDisplayName(),
            ReverseRouter.route(on(CollaborationOpportunitiesController.class).deleteCollaborationOpportunityConfirm(
                projectId,
                opportunity.getId(),
                displayOrder,
                null
            ))
        )
    );

    return view;
  }

  public static CollaborationOpportunityView createView(
      CollaborationOpportunity opportunity,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews,
      Boolean isValid
  ) {
    var view = createView(opportunity, displayOrder, uploadedFileViews);
    view.setIsValid(isValid);
    return view;
  }
}
