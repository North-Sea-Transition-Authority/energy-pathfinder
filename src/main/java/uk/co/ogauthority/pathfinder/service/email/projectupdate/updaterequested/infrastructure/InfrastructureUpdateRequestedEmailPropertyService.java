package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.ProjectUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.infrastructure.InfrastructureUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.UpdateRequestedEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Service
class InfrastructureUpdateRequestedEmailPropertyService implements UpdateRequestedEmailPropertyProvider {

  private final EmailLinkService emailLinkService;

  private final ServiceProperties serviceProperties;

  private final ProjectInformationService projectInformationService;

  @Autowired
  InfrastructureUpdateRequestedEmailPropertyService(EmailLinkService emailLinkService,
                                                    ServiceProperties serviceProperties,
                                                    ProjectInformationService projectInformationService) {
    this.emailLinkService = emailLinkService;
    this.serviceProperties = serviceProperties;
    this.projectInformationService = projectInformationService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public ProjectUpdateRequestedEmailProperties getUpdateRequestedEmailProperties(ProjectDetail projectDetail,
                                                                                 String updateReason,
                                                                                 String deadlineDate) {

    final var projectTitle = projectInformationService.getProjectTitle(projectDetail);

    final var projectManagementUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

    return new InfrastructureUpdateRequestedEmailProperties(
        updateReason,
        deadlineDate,
        projectManagementUrl,
        serviceProperties.getCustomerMnemonic(),
        projectTitle
    );
  }
}
