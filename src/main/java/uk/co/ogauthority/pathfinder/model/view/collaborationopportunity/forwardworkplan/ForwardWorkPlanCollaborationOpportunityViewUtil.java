package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewUtilCommon;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class ForwardWorkPlanCollaborationOpportunityViewUtil {

  private ForwardWorkPlanCollaborationOpportunityViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanCollaborationOpportunityViewUtil is a utility class and should not be instantiated");
  }

  public static class ForwardWorkPlanCollaborationOpportunityViewBuilder {
    private final ForwardWorkPlanCollaborationOpportunity opportunity;
    private final Integer displayOrder;
    private final List<UploadedFileView> uploadedFileViews;
    private final PortalOrganisationGroup addedByPortalOrganisationGroup;
    private Boolean isValid;
    private boolean includeSummaryViews = false;

    public ForwardWorkPlanCollaborationOpportunityViewBuilder(
        ForwardWorkPlanCollaborationOpportunity opportunity,
        Integer displayOrder,
        List<UploadedFileView> uploadedFileViews,
        PortalOrganisationGroup addedByPortalOrganisationGroup) {
      this.opportunity = opportunity;
      this.displayOrder = displayOrder;
      this.uploadedFileViews = uploadedFileViews;
      this.addedByPortalOrganisationGroup = addedByPortalOrganisationGroup;
    }

    public ForwardWorkPlanCollaborationOpportunityViewBuilder isValid(Boolean isValid) {
      this.isValid = isValid;
      return this;
    }

    public ForwardWorkPlanCollaborationOpportunityViewBuilder includeSummaryLinks(boolean includeSummaryViews) {
      this.includeSummaryViews = includeSummaryViews;
      return this;
    }

    public ForwardWorkPlanCollaborationOpportunityView build() {
      return createView(
          opportunity,
          includeSummaryViews,
          displayOrder,
          uploadedFileViews,
          addedByPortalOrganisationGroup
      );
    }

    private ForwardWorkPlanCollaborationOpportunityView createView(
        ForwardWorkPlanCollaborationOpportunity opportunity,
        boolean includeSummaryLinks,
        Integer displayOrder,
        List<UploadedFileView> uploadedFileViews,
        PortalOrganisationGroup addedByPortalOrganisationGroup
    ) {

      var projectId = opportunity.getProjectDetail().getProject().getId();

      final var editUrl = ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
          .editCollaborationOpportunity(
              projectId,
              opportunity.getId(),
              null
          )
      );

      final var deleteUrl = ReverseRouter.route(on(ForwardWorkPlanCollaborationOpportunityController.class)
          .removeCollaborationOpportunityConfirm(
              projectId,
              opportunity.getId(),
              displayOrder,
              null
          )
      );

      var view = (ForwardWorkPlanCollaborationOpportunityView) CollaborationOpportunityViewUtilCommon.populateView(
          new ForwardWorkPlanCollaborationOpportunityView(),
          opportunity,
          includeSummaryLinks,
          displayOrder,
          uploadedFileViews,
          editUrl,
          deleteUrl,
          addedByPortalOrganisationGroup
      );

      view.setIsValid(this.isValid);

      return view;
    }
  }
}
