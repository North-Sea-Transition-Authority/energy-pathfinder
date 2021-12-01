package uk.co.ogauthority.pathfinder.service.email.projecttransfer;

import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;

@Service
class TestProjectTransferEmailPropertyProvider implements ProjectTransferEmailPropertyProvider {

  @Override
  public ProjectType getSupportedProjectType() {
    return null;
  }

  @Override
  public IncomingOperatorProjectTransferEmailProperties getIncomingOperatorTransferEmailProperties(
      ProjectDetail projectDetail,
      String transferReason,
      String previousOperatorName
  ) {
    return null;
  }

  @Override
  public OutgoingOperatorProjectTransferEmailProperties getOutgoingOperatorTransferEmailProperties(
      ProjectDetail projectDetail,
      String transferReason,
      String currentOperatorName
  ) {
    return null;
  }
}
