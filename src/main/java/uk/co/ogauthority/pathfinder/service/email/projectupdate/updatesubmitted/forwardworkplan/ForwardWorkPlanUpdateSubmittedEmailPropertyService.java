package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.forwardworkplan.ForwardWorkPlanUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.UpdateSubmittedEmailPropertyProvider;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;

@Service
class ForwardWorkPlanUpdateSubmittedEmailPropertyService implements UpdateSubmittedEmailPropertyProvider {

  private final ProjectOperatorService projectOperatorService;

  private final LinkService linkService;

  @Autowired
  ForwardWorkPlanUpdateSubmittedEmailPropertyService(ProjectOperatorService projectOperatorService,
                                                     LinkService linkService) {
    this.projectOperatorService = projectOperatorService;
    this.linkService = linkService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public ProjectUpdateEmailProperties getUpdateSubmittedEmailProperties(ProjectDetail projectDetail) {

    final var projectOperatorName = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup()
        .getName();

    final var loginUrl = linkService.generateProjectManagementUrl(projectDetail.getProject());

    return new ForwardWorkPlanUpdateEmailProperties(
        loginUrl,
        projectOperatorName
    );
  }
}
