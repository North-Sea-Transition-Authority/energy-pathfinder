package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.view.StringWithTag;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.model.view.Tag;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.util.StringDisplayUtil;

public class InfrastructureCollaborationOpportunityViewUtil {

  private InfrastructureCollaborationOpportunityViewUtil() {
    throw new IllegalStateException("InfrastructureCollaborationOpportunityViewUtil is a utility class and should not be instantiated");
  }

  public static InfrastructureCollaborationOpportunityView createView(
      InfrastructureCollaborationOpportunity opportunity,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews
  ) {
    var projectId = opportunity.getProjectDetail().getProject().getId();
    var view = new InfrastructureCollaborationOpportunityView(
        displayOrder,
        opportunity.getId(),
        projectId
    );

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

    var editLink = new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class).editCollaborationOpportunity(
            projectId,
            opportunity.getId(),
            null
        ))
    );

    var removeLink = new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class).removeCollaborationOpportunityConfirm(
            projectId,
            opportunity.getId(),
            displayOrder,
            null
        ))
    );

    view.setSummaryLinks(List.of(editLink, removeLink));

    return view;
  }

  public static InfrastructureCollaborationOpportunityView createView(
      InfrastructureCollaborationOpportunity opportunity,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews,
      Boolean isValid
  ) {
    var view = createView(opportunity, displayOrder, uploadedFileViews);
    view.setIsValid(isValid);
    return view;
  }
}
