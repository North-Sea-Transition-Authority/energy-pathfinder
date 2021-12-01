package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.forwardworkplan.ForwardWorkPlanNoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.NoUpdateNotificationEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;

@Service
class ForwardWorkPlanNoUpdateNotificationEmailPropertyService implements NoUpdateNotificationEmailPropertyProvider {

  private final ProjectOperatorService projectOperatorService;

  private final EmailLinkService emailLinkService;

  @Autowired
  ForwardWorkPlanNoUpdateNotificationEmailPropertyService(ProjectOperatorService projectOperatorService,
                                                          EmailLinkService emailLinkService) {
    this.projectOperatorService = projectOperatorService;
    this.emailLinkService = emailLinkService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public NoUpdateNotificationEmailProperties getNoUpdateNotificationEmailProperties(ProjectDetail projectDetail,
                                                                                    String noUpdateReason) {

    final var operatorName = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup()
        .getName();

    final var projectManagementUrl = emailLinkService.generateProjectManagementUrl(projectDetail.getProject());

    return new ForwardWorkPlanNoUpdateNotificationEmailProperties(
        projectManagementUrl,
        noUpdateReason,
        operatorName
    );
  }
}
