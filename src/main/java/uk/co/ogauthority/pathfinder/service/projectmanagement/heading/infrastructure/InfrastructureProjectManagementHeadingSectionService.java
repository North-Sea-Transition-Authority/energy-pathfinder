package uk.co.ogauthority.pathfinder.service.projectmanagement.heading.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorDisplayNameUtil;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.heading.ProjectManagementHeadingService;

@Service
public class InfrastructureProjectManagementHeadingSectionService implements ProjectManagementHeadingService {

  private final ProjectInformationService projectInformationService;
  private final ProjectOperatorService projectOperatorService;

  @Autowired
  public InfrastructureProjectManagementHeadingSectionService(ProjectInformationService projectInformationService,
                                                              ProjectOperatorService projectOperatorService) {
    this.projectInformationService = projectInformationService;
    this.projectOperatorService = projectOperatorService;
  }

  @Override
  public ProjectType getSupportedProjectType() {
    return ProjectType.INFRASTRUCTURE;
  }

  @Override
  public String getHeadingText(ProjectDetail projectDetail) {
    final var projectTitle = projectInformationService
        .getProjectInformationOrError(projectDetail)
        .getProjectTitle();

    return String.format("%s: %s", getSupportedProjectType().getDisplayName(), projectTitle);
  }

  @Override
  public String getCaptionText(ProjectDetail projectDetail) {

    var projectOperator = projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail);

    return ProjectOperatorDisplayNameUtil.getProjectOperatorDisplayName(
        projectOperator.getOrganisationGroup(),
        projectOperator.getPublishableOrganisationUnit()
    );
  }
}
