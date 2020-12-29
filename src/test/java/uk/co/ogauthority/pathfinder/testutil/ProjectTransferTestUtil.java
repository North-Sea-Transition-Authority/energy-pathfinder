package uk.co.ogauthority.pathfinder.testutil;

import java.time.Instant;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.projecttransfer.ProjectTransfer;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;

public class ProjectTransferTestUtil {

  private static final PortalOrganisationGroup FROM_ORGANISATION_GROUP = ProjectOperatorTestUtil.getOrgGroup("Org 1");
  private static final PortalOrganisationGroup TO_ORGANISATION_GROUP = ProjectOperatorTestUtil.getOrgGroup("Org 2");
  private static final String TRANSFER_REASON = "Transfer reason";
  private static final Integer TRANSFERRED_BY_WUA_ID = 1;

  private ProjectTransferTestUtil() {
    throw new IllegalStateException("ProjectTransferTestUtil is a utility class and should not be instantiated");
  }

  public static ProjectTransfer createProjectTransfer() {
    var projectTransfer = new ProjectTransfer();
    projectTransfer.setProjectDetail(ProjectUtil.getProjectDetails());
    projectTransfer.setFromOrganisationGroup(FROM_ORGANISATION_GROUP);
    projectTransfer.setToOrganisationGroup(TO_ORGANISATION_GROUP);
    projectTransfer.setTransferReason(TRANSFER_REASON);
    projectTransfer.setTransferredInstant(Instant.now());
    projectTransfer.setTransferredByWuaId(TRANSFERRED_BY_WUA_ID);
    return projectTransfer;
  }

  public static ProjectTransferForm createProjectTransferForm() {
    var form = new ProjectTransferForm();
    form.setNewOrganisationGroup(TO_ORGANISATION_GROUP.getSelectionId());
    form.setTransferReason(TRANSFER_REASON);
    return form;
  }
}
