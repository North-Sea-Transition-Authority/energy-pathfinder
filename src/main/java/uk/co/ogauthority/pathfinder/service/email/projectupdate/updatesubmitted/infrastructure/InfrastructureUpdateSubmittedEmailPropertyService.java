package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.infrastructure.InfrastructureUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.UpdateSubmittedEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;

@Service
class InfrastructureUpdateSubmittedEmailPropertyService implements UpdateSubmittedEmailPropertyProvider {

  private final ProjectOperatorService projectOperatorService;

  private final EmailLinkService emailLinkService;

  private final ProjectInformationService projectInformationService;

  @Autowired
  InfrastructureUpdateSubmittedEmailPropertyService(ProjectOperatorService projectOperatorService,
                                                    EmailLinkService emailLinkService,
                                                    ProjectInformationService projectInformationService) {
    this.projectOperatorService = projectOperatorService;
    this.emailLinkService = emailLinkService;
    this.projectInformationService = projectInformationService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public ProjectUpdateEmailProperties getUpdateSubmittedEmailProperties(ProjectDetail projectDetail) {

    final var projectOperatorName = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup()
        .getName();

    final var projectTitle = projectInformationService.getProjectTitle(projectDetail);

    final var loginUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

    return new InfrastructureUpdateEmailProperties(
        loginUrl,
        projectOperatorName,
        projectTitle
    );
  }
}
