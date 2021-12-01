package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.infrastructure;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.infrastructure.InfrastructureCollaborationOpportunitiesController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewUtilCommon;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

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

    final var editUrl = ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
        .editCollaborationOpportunity(
            projectId,
            opportunity.getId(),
            null
        )
    );

    final var deleteUrl = ReverseRouter.route(on(InfrastructureCollaborationOpportunitiesController.class)
        .removeCollaborationOpportunityConfirm(
            projectId,
            opportunity.getId(),
            displayOrder,
            null
        )
    );

    return (InfrastructureCollaborationOpportunityView) CollaborationOpportunityViewUtilCommon.populateView(
        new InfrastructureCollaborationOpportunityView(),
        opportunity,
        displayOrder,
        uploadedFileViews,
        editUrl,
        deleteUrl
    );
  }

  public static InfrastructureCollaborationOpportunityView createView(
      InfrastructureCollaborationOpportunity opportunity,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews,
      Boolean isValid
  ) {
    var view = createView(
        opportunity,
        displayOrder,
        uploadedFileViews
    );
    view.setIsValid(isValid);
    return view;
  }
}
