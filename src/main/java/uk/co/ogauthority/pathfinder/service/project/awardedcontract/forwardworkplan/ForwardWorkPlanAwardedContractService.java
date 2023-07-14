package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class ForwardWorkPlanAwardedContractService implements ProjectFormSectionService {

  private final EntityDuplicationService entityDuplicationService;
  private final ForwardWorkPlanAwardedContractSetupService setupService;

  @Autowired
  ForwardWorkPlanAwardedContractService(EntityDuplicationService entityDuplicationService,
                                        ForwardWorkPlanAwardedContractSetupService setupService) {
    this.entityDuplicationService = entityDuplicationService;
    this.setupService = setupService;
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    // TODO (EDU-6597): This will be updated in another branch when the rest of the awarded contracts is added
    return setupService.isValid(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    var awardedContractSetupOptional = setupService.getForwardWorkPlanAwardedContractSetup(fromDetail);
    awardedContractSetupOptional.ifPresent(
        awardedContractSetup -> entityDuplicationService.duplicateEntityAndSetNewParent(
            awardedContractSetup,
            toDetail,
            ForwardWorkPlanAwardedContractSetup.class
        ));
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.FORWARD_WORK_PLAN);
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.WORK_PLAN_AWARDED_CONTRACTS, userToProjectRelationships);
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return ProjectService.isForwardWorkPlanProject(detail);
  }
}
