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

  public static class InfrastructureCollaborationOpportunityViewBuilder {
    private final InfrastructureCollaborationOpportunity opportunity;
    private final Integer displayOrder;
    private final List<UploadedFileView> uploadedFileViews;
    private Boolean isValid;
    private boolean includeSummaryLinks = false;

    public InfrastructureCollaborationOpportunityViewBuilder(
        InfrastructureCollaborationOpportunity opportunity,
        Integer displayOrder,
        List<UploadedFileView> uploadedFileViews) {
      this.opportunity = opportunity;
      this.displayOrder = displayOrder;
      this.uploadedFileViews = uploadedFileViews;
    }

    public InfrastructureCollaborationOpportunityViewBuilder isValid(Boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public InfrastructureCollaborationOpportunityViewBuilder includeSummaryLinks(boolean includeSummaryLinks) {
      this.includeSummaryLinks = includeSummaryLinks;
      return this;
    }

    public InfrastructureCollaborationOpportunityView build() {
      return createView(
          this.opportunity,
          this.includeSummaryLinks,
          this.displayOrder,
          this.uploadedFileViews,
          this.isValid
      );
    }

    private static InfrastructureCollaborationOpportunityView createView(
        InfrastructureCollaborationOpportunity opportunity,
        boolean includeSummaryLinks,
        Integer displayOrder,
        List<UploadedFileView> uploadedFileViews,
        Boolean isValid
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

      InfrastructureCollaborationOpportunityView view =
          (InfrastructureCollaborationOpportunityView) CollaborationOpportunityViewUtilCommon.populateView(
              new InfrastructureCollaborationOpportunityView(),
              opportunity,
              includeSummaryLinks,
              displayOrder,
              uploadedFileViews,
              editUrl,
              deleteUrl
          );
      view.setIsValid(isValid);
      return view;
    }
  }
}
