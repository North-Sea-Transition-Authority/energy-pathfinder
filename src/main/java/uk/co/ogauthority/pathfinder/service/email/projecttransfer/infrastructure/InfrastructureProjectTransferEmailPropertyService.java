package uk.co.ogauthority.pathfinder.service.email.projecttransfer.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.IncomingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.OutgoingOperatorProjectTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.infrastructure.InfrastructureIncomingOperatorTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.transfer.infrastructure.InfrastructureOutgoingOperatorTransferEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.projecttransfer.ProjectTransferEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Service
class InfrastructureProjectTransferEmailPropertyService implements ProjectTransferEmailPropertyProvider {

  private final ServiceProperties serviceProperties;

  private final ProjectInformationService projectInformationService;

  private final EmailLinkService emailLinkService;

  @Autowired
  InfrastructureProjectTransferEmailPropertyService(ServiceProperties serviceProperties,
                                                    ProjectInformationService projectInformationService,
                                                    EmailLinkService emailLinkService) {
    this.serviceProperties = serviceProperties;
    this.projectInformationService = projectInformationService;
    this.emailLinkService = emailLinkService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public IncomingOperatorProjectTransferEmailProperties getIncomingOperatorTransferEmailProperties(
      ProjectDetail projectDetail,
      String transferReason,
      String previousOperatorName
  ) {

    final var projectUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

    return new InfrastructureIncomingOperatorTransferEmailProperties(
        transferReason,
        previousOperatorName,
        projectUrl,
        getCustomerMnemonic(),
        getProjectTitle(projectDetail)
    );
  }

  @Override
  public OutgoingOperatorProjectTransferEmailProperties getOutgoingOperatorTransferEmailProperties(
      ProjectDetail projectDetail,
      String transferReason,
      String currentOperatorName
  ) {
    return new InfrastructureOutgoingOperatorTransferEmailProperties(
        transferReason,
        currentOperatorName,
        getCustomerMnemonic(),
        getProjectTitle(projectDetail)
    );
  }

  private String getCustomerMnemonic() {
    return serviceProperties.getCustomerMnemonic();
  }

  private String getProjectTitle(ProjectDetail projectDetail) {
    return projectInformationService.getProjectTitle(projectDetail);
  }
}
