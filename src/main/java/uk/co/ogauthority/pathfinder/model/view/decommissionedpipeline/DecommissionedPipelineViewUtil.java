package uk.co.ogauthority.pathfinder.model.view.decommissionedpipeline;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.controller.project.decommissionedpipeline.DecommissionedPipelineController;
import uk.co.ogauthority.pathfinder.model.entity.project.decommissionedpipeline.DecommissionedPipeline;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class DecommissionedPipelineViewUtil {

  private DecommissionedPipelineViewUtil() {
    throw new IllegalStateException("DecommissionedPipelineViewUtil is a util class and should not be instantiated");
  }

  public static DecommissionedPipelineView from(DecommissionedPipeline decommissionedPipeline, Integer displayOrder) {
    return from(decommissionedPipeline, displayOrder, true);
  }

  public static DecommissionedPipelineView from(DecommissionedPipeline decommissionedPipeline,
                                                Integer displayOrder,
                                                boolean isValid) {
    var decommissionedPipelineView = new DecommissionedPipelineView();
    decommissionedPipelineView.setDisplayOrder(displayOrder);
    decommissionedPipelineView.setId(decommissionedPipeline.getId());

    var projectId = decommissionedPipeline.getProjectDetail().getProject().getId();
    decommissionedPipelineView.setProjectId(projectId);

    var pipeline = (decommissionedPipeline.getPipeline() != null)
        ? decommissionedPipeline.getPipeline().getSelectionText()
        : null;
    decommissionedPipelineView.setPipeline(pipeline);

    decommissionedPipelineView.setMaterialType(decommissionedPipeline.getMaterialType());

    var status = (decommissionedPipeline.getStatus() != null)
        ? decommissionedPipeline.getStatus().getDisplayName()
        : null;
    decommissionedPipelineView.setStatus(status);

    decommissionedPipelineView.setDecommissioningEarliestYear(decommissionedPipeline.getEarliestRemovalYear());
    decommissionedPipelineView.setDecommissioningLatestYear(decommissionedPipeline.getLatestRemovalYear());

    var removalPremise = (decommissionedPipeline.getRemovalPremise() != null)
        ? decommissionedPipeline.getRemovalPremise().getDisplayName()
        : null;
    decommissionedPipelineView.setRemovalPremise(removalPremise);

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(getEditSummaryLink(projectId, decommissionedPipeline.getId()));
    summaryLinks.add(getDeleteSummaryLink(projectId, decommissionedPipeline.getId(), displayOrder));
    decommissionedPipelineView.setSummaryLinks(summaryLinks);

    decommissionedPipelineView.setIsValid(isValid);

    return decommissionedPipelineView;
  }

  private static SummaryLink getEditSummaryLink(Integer projectId, Integer decommissionedPipelineId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(DecommissionedPipelineController.class).getPipeline(
            projectId,
            decommissionedPipelineId,
            null
        ))
    );
  }

  private static SummaryLink getDeleteSummaryLink(Integer projectId,
                                                  Integer decommissionedPipelineId,
                                                  Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(DecommissionedPipelineController.class).removePipelineConfirmation(
            projectId,
            decommissionedPipelineId,
            displayOrder,
            null
        ))
    );
  }
}
