package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.infrastructure.InfrastructureNoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.NoUpdateNotificationEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Service
class InfrastructureNoUpdateNotificationEmailPropertyService implements NoUpdateNotificationEmailPropertyProvider {

  private final ProjectOperatorService projectOperatorService;

  private final LinkService linkService;

  private final ProjectInformationService projectInformationService;

  @Autowired
  InfrastructureNoUpdateNotificationEmailPropertyService(ProjectOperatorService projectOperatorService,
                                                         LinkService linkService,
                                                         ProjectInformationService projectInformationService) {
    this.projectOperatorService = projectOperatorService;
    this.linkService = linkService;
    this.projectInformationService = projectInformationService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public NoUpdateNotificationEmailProperties getNoUpdateNotificationEmailProperties(ProjectDetail projectDetail,
                                                                                    String noUpdateReason) {

    final var operatorName = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup()
        .getName();

    final var projectManagementUrl = linkService.generateProjectManagementUrl(projectDetail.getProject());

    final var projectTitle = projectInformationService.getProjectTitle(projectDetail);

    return new InfrastructureNoUpdateNotificationEmailProperties(
        projectTitle,
        projectManagementUrl,
        noUpdateReason,
        operatorName
    );
  }
}
