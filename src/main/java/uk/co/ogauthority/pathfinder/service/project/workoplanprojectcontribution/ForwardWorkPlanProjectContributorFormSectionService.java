package uk.co.ogauthority.pathfinder.service.project.workoplanprojectcontribution;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.projectcontribution.ProjectContributor;
import uk.co.ogauthority.pathfinder.model.entity.project.workplanprojectcontribution.ForwardWorkPlanContributorDetails;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontribution.ProjectContributorsCommonService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;

@Service
public class ForwardWorkPlanProjectContributorFormSectionService implements ProjectFormSectionService {

  private final ForwardWorkPlanProjectContributorManagementService forwardWorkPlanProjectContributorManagementService;
  private final EntityDuplicationService entityDuplicationService;
  private final ProjectContributorsCommonService projectContributorsCommonService;

  @Autowired
  public ForwardWorkPlanProjectContributorFormSectionService(
      ForwardWorkPlanProjectContributorManagementService forwardWorkPlanProjectContributorManagementService,
      EntityDuplicationService entityDuplicationService,
      ProjectContributorsCommonService projectContributorsCommonService) {
    this.forwardWorkPlanProjectContributorManagementService = forwardWorkPlanProjectContributorManagementService;
    this.entityDuplicationService = entityDuplicationService;
    this.projectContributorsCommonService = projectContributorsCommonService;
  }

  @Override
  public boolean isComplete(ProjectDetail detail) {
    return forwardWorkPlanProjectContributorManagementService.isValid(detail, ValidationType.FULL);
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    if (projectDetail.getProjectType().equals(ProjectType.FORWARD_WORK_PLAN)) {
      forwardWorkPlanProjectContributorManagementService.removeForwardProjectContributorsForDetail(projectDetail);
    }
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        projectContributorsCommonService.getProjectContributorsForDetail(fromDetail),
        toDetail,
        ProjectContributor.class
    );

    entityDuplicationService.duplicateEntityAndSetNewParent(
        forwardWorkPlanProjectContributorManagementService.getForwardProjectContributorForDetail(fromDetail),
        toDetail,
        ForwardWorkPlanContributorDetails.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.FORWARD_WORK_PLAN);
  }
}
