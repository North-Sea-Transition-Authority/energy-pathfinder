package uk.co.ogauthority.pathfinder.service.project.projectcontribution;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class ProjectContributorsFormSectionService implements ProjectFormSectionService {

  private final ProjectSetupService projectSetupService;
  private final ProjectContributorsManagementService projectContributorsManagementService;
  private final EntityDuplicationService entityDuplicationService;
  private final ProjectContributorsCommonService projectContributorsCommonService;

  @Autowired
  public ProjectContributorsFormSectionService(
      ProjectSetupService projectSetupService,
      ProjectContributorsManagementService projectContributorsManagementService,
      EntityDuplicationService entityDuplicationService,
      ProjectContributorsCommonService projectContributorsCommonService) {
    this.projectSetupService = projectSetupService;
    this.projectContributorsManagementService = projectContributorsManagementService;
    this.entityDuplicationService = entityDuplicationService;
    this.projectContributorsCommonService = projectContributorsCommonService;
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return projectContributorsManagementService.isValid(detail, ValidationType.FULL);
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.PROJECT_CONTRIBUTORS);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        projectContributorsCommonService.getProjectContributorsForDetail(fromDetail),
        toDetail,
        ProjectContributor.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.PROJECT_CONTRIBUTORS,
        userToProjectRelationships);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    if (projectDetail.getProjectType().equals(ProjectType.INFRASTRUCTURE)) {
      projectContributorsManagementService.removeProjectContributorsForDetail(projectDetail);
    }
  }
}
