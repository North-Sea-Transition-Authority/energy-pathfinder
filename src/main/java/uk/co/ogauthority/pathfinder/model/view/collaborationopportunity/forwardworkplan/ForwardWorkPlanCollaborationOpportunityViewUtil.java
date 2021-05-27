package uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.forwardworkplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.ogauthority.pathfinder.controller.project.collaborationopportunites.forwardworkplan.ForwardWorkPlanCollaborationOpportunityController;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.CollaborationOpportunityViewUtilCommon;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class ForwardWorkPlanCollaborationOpportunityViewUtil {

  private ForwardWorkPlanCollaborationOpportunityViewUtil() {
    throw new IllegalStateException("ForwardWorkPlanCollaborationOpportunityViewUtil is a utility class and should not be instantiated");
  }

  public static ForwardWorkPlanCollaborationOpportunityView createView(
      ForwardWorkPlanCollaborationOpportunity opportunity,
      Integer displayOrder,
      List<UploadedFileView> uploadedFileViews
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

    return (ForwardWorkPlanCollaborationOpportunityView) CollaborationOpportunityViewUtilCommon.populateView(
        new ForwardWorkPlanCollaborationOpportunityView(),
        opportunity,
        displayOrder,
        uploadedFileViews,
        editUrl,
        deleteUrl
    );
  }

  public static ForwardWorkPlanCollaborationOpportunityView createView(
      ForwardWorkPlanCollaborationOpportunity opportunity,
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
