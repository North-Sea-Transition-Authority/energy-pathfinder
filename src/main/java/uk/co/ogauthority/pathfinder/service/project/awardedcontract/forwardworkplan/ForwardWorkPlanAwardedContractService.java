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
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractSummaryService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanAwardedContractService implements ProjectFormSectionService {

  private final EntityDuplicationService entityDuplicationService;
  private final ForwardWorkPlanAwardedContractSetupService setupService;
  private final AwardedContractSummaryService awardedContractSummaryService;

  @Autowired
  ForwardWorkPlanAwardedContractService(EntityDuplicationService entityDuplicationService,
                                        ForwardWorkPlanAwardedContractSetupService setupService,
                                        AwardedContractSummaryService awardedContractSummaryService) {
    this.entityDuplicationService = entityDuplicationService;
    this.setupService = setupService;
    this.awardedContractSummaryService = awardedContractSummaryService;
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var awardedContractSetupOptional = setupService.getForwardWorkPlanAwardedContractSetup(projectDetail);
    if (awardedContractSetupOptional.isEmpty()) {
      return false;
    }
    var awardedContractSetup = awardedContractSetupOptional.get();
    if (Boolean.FALSE.equals(awardedContractSetup.getHasContractToAdd())) {
      return true;
    }

    var awardedContractViews = awardedContractSummaryService.getValidatedAwardedContractViews(projectDetail);
    var validationResult = awardedContractSummaryService.validateViews(awardedContractViews);
    return ValidationResult.VALID.equals(validationResult)
        && Boolean.FALSE.equals(awardedContractSetup.getHasOtherContractToAdd())
        && !awardedContractViews.isEmpty();
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
