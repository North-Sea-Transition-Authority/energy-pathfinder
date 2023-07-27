package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractSetup;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;
import uk.co.ogauthority.pathfinder.util.validation.ValidationResult;

@Service
public class ForwardWorkPlanAwardedContractFormSectionService implements ProjectFormSectionService {

  private final ForwardWorkPlanAwardedContractSetupService setupService;
  private final ForwardWorkPlanAwardedContractSummaryService awardedContractSummaryService;
  private final ForwardWorkPlanAwardedContractService awardedContractService;
  private final EntityDuplicationService entityDuplicationService;

  ForwardWorkPlanAwardedContractFormSectionService(ForwardWorkPlanAwardedContractSetupService setupService,
                                                   ForwardWorkPlanAwardedContractSummaryService awardedContractSummaryService,
                                                   ForwardWorkPlanAwardedContractService awardedContractService,
                                                   EntityDuplicationService entityDuplicationService) {
    this.setupService = setupService;
    this.awardedContractSummaryService = awardedContractSummaryService;
    this.awardedContractService = awardedContractService;
    this.entityDuplicationService = entityDuplicationService;
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

    var awardContracts = awardedContractService.getAwardedContracts(fromDetail);
    if (!awardContracts.isEmpty()) {
      entityDuplicationService.duplicateEntitiesAndSetNewParent(
          awardContracts,
          toDetail,
          ForwardWorkPlanAwardedContract.class
      );
    }
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

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    setupService.deleteAllByProjectDetail(projectDetail);
    awardedContractService.deleteAllByProjectDetail(projectDetail);
  }
}
