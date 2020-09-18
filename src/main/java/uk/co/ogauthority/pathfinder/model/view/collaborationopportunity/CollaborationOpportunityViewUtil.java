package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.CollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.controller.project.upcomingtender.UpcomingTendersController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.DateUtil;

public class CollaborationOpportunityViewUtil {
  public CollaborationOpportunityViewUtil() {
    throw new IllegalStateException("CollaborationOpportunityViewUtil is a utility class and should not be instantiated");
  }


  public static CollaborationOpportunityView createView(
      CollaborationOpportunity opportunity,
      Integer displayOrder
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
    view.setContactName(opportunity.getContactName());
    view.setPhoneNumber(opportunity.getPhoneNumber());
    view.setJobTitle(opportunity.getJobTitle());
    view.setEmailAddress(opportunity.getEmailAddress());
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
            ReverseRouter.route(on(UpcomingTendersController.class).deleteUpcomingTenderConfirm(
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
      Boolean isValid
  ) {
    var view = createView(opportunity, displayOrder);
    view.setIsValid(isValid);
    return view;
  }
}
