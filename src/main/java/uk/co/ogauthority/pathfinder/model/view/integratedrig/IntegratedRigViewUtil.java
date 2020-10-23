package uk.co.ogauthority.pathfinder.model.view.integratedrig;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import uk.co.ogauthority.pathfinder.controller.project.integratedrig.IntegratedRigController;
import uk.co.ogauthority.pathfinder.model.entity.project.integratedrig.IntegratedRig;
import uk.co.ogauthority.pathfinder.model.view.SummaryLink;
import uk.co.ogauthority.pathfinder.model.view.SummaryLinkText;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;

public class IntegratedRigViewUtil {

  private IntegratedRigViewUtil() {
    throw new IllegalStateException("IntegratedRigViewUtil is a util class and should not be instantiated");
  }

  public static IntegratedRigView from(IntegratedRig integratedRig, Integer displayOrder) {
    return from(integratedRig, displayOrder, true);
  }

  public static IntegratedRigView from(IntegratedRig integratedRig,
                                       Integer displayOrder,
                                       boolean isValid) {
    var integratedRigView = new IntegratedRigView();
    integratedRigView.setDisplayOrder(displayOrder);
    integratedRigView.setId(integratedRig.getId());

    var projectId = integratedRig.getProjectDetail().getProject().getId();
    integratedRigView.setProjectId(projectId);

    var structure = (integratedRig.getFacility() != null)
        ? integratedRig.getFacility().getSelectionText()
        : integratedRig.getManualFacility();
    integratedRigView.setStructure(structure);

    integratedRigView.setName(integratedRig.getName());

    var status = (integratedRig.getStatus() != null)
        ? integratedRig.getStatus().getDisplayName()
        : null;
    integratedRigView.setStatus(status);

    var intentionToReactivate = (integratedRig.getIntentionToReactivate() != null)
        ? integratedRig.getIntentionToReactivate().getDisplayName()
        : null;
    integratedRigView.setIntentionToReactivate(intentionToReactivate);

    var summaryLinks = new ArrayList<SummaryLink>();
    summaryLinks.add(getEditSummaryLink(projectId, integratedRig.getId()));
    summaryLinks.add(getDeleteSummaryLink(projectId, integratedRig.getId(), displayOrder));
    integratedRigView.setSummaryLinks(summaryLinks);

    integratedRigView.setIsValid(isValid);

    return integratedRigView;
  }

  private static SummaryLink getEditSummaryLink(Integer projectId, Integer integratedRigId) {
    return new SummaryLink(
        SummaryLinkText.EDIT.getDisplayName(),
        ReverseRouter.route(on(IntegratedRigController.class).getIntegratedRig(
            projectId,
            integratedRigId,
            null
        ))
    );
  }

  private static SummaryLink getDeleteSummaryLink(Integer projectId,
                                                  Integer integratedRigId,
                                                  Integer displayOrder) {
    return new SummaryLink(
        SummaryLinkText.DELETE.getDisplayName(),
        ReverseRouter.route(on(IntegratedRigController.class).removeIntegratedRigsConfirmation(
            projectId,
            integratedRigId,
            displayOrder,
            null
        ))
    );
  }
}
