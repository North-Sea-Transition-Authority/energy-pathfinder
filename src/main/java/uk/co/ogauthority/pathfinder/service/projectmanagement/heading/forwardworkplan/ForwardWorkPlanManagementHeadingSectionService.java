package uk.co.ogauthority.pathfinder.service.projectmanagement.heading.forwardworkplan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.heading.ProjectManagementHeadingService;

@Service
public class ForwardWorkPlanManagementHeadingSectionService implements ProjectManagementHeadingService {

  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public ForwardWorkPlanManagementHeadingSectionService(ProjectOperatorService projectOperatorService) {
    this.projectOperatorService = projectOperatorService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.FORWARD_WORK_PLAN;
  }

  @Override
  public String getHeadingText(ProjectDetail projectDetail) {
    return getSupportedProjectType().getDisplayName();
  }

  @Override
  public String getCaptionText(ProjectDetail projectDetail) {
    return projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)
        .getOrganisationGroup()
        .getName();
  }
}
