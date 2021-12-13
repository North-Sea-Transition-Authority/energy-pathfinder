package uk.co.ogauthority.pathfinder.service.email.projecttransfer;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.LinkService;

@Service
public class ProjectTransferEmailPropertyService {

  private final List<ProjectTransferEmailPropertyProvider> projectTransferEmailPropertyProviders;

  private final LinkService linkService;

  public ProjectTransferEmailPropertyService(
      List<ProjectTransferEmailPropertyProvider> projectTransferEmailPropertyProviders,
      LinkService linkService
  ) {
    this.projectTransferEmailPropertyProviders = projectTransferEmailPropertyProviders;
    this.linkService = linkService;
  }

  public IncomingOperatorProjectTransferEmailProperties getIncomingOperatorProjectTransferEmailProperties(
      ProjectDetail projectDetail,
      String transferReason,
      String previousOperatorName
  ) {

    final var incomingOperatorProjectTransferEmailPropertyService = projectTransferEmailPropertyProviders
        .stream()
        .filter(projectTransferEmailPropertyProvider ->
            projectTransferEmailPropertyProvider.getSupportedProjectType().equals(projectDetail.getProjectType())
        )
        .findFirst();

    if (incomingOperatorProjectTransferEmailPropertyService.isPresent()) {
      return incomingOperatorProjectTransferEmailPropertyService.get().getIncomingOperatorTransferEmailProperties(
          projectDetail,
          transferReason,
          previousOperatorName
      );
    } else {

      final var projectUrl = linkService.generateProjectManagementUrl(projectDetail.getProject());

      return new IncomingOperatorProjectTransferEmailProperties(
          transferReason,
          previousOperatorName,
          projectUrl
      );
    }
  }

  public OutgoingOperatorProjectTransferEmailProperties getOutgoingOperatorProjectTransferEmailProperties(
      ProjectDetail projectDetail,
      String transferReason,
      String currentOperatorName
  ) {

    final var outgoingOperatorProjectTransferEmailPropertyService = projectTransferEmailPropertyProviders
        .stream()
        .filter(projectTransferEmailPropertyProvider ->
            projectTransferEmailPropertyProvider.getSupportedProjectType().equals(projectDetail.getProjectType())
        )
        .findFirst();

    if (outgoingOperatorProjectTransferEmailPropertyService.isPresent()) {
      return outgoingOperatorProjectTransferEmailPropertyService.get().getOutgoingOperatorTransferEmailProperties(
          projectDetail,
          transferReason,
          currentOperatorName
      );
    } else {
      return new OutgoingOperatorProjectTransferEmailProperties(
          transferReason,
          currentOperatorName
      );
    }
  }
}
