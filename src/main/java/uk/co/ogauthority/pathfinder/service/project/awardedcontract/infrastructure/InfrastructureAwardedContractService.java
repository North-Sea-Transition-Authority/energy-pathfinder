package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractServiceCommon;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class InfrastructureAwardedContractService implements ProjectFormSectionService {

  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;
  private final AwardedContractServiceCommon awardedContractServiceCommon;
  private final AwardedContractRepository awardedContractRepository;

  @Autowired
  public InfrastructureAwardedContractService(ProjectSetupService projectSetupService,
                                              EntityDuplicationService entityDuplicationService,
                                              AwardedContractServiceCommon awardedContractServiceCommon,
                                              AwardedContractRepository awardedContractRepository) {
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
    this.awardedContractServiceCommon = awardedContractServiceCommon;
    this.awardedContractRepository = awardedContractRepository;
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var awardedContracts = awardedContractServiceCommon.getAwardedContracts(projectDetail);
    return !awardedContracts.isEmpty()
        && awardedContracts
        .stream()
        .allMatch(awardedContract -> awardedContractServiceCommon.isValid(awardedContract, ValidationType.FULL));
  }

  @Override
  public boolean canShowInTaskList(ProjectDetail detail, Set<UserToProjectRelationship> userToProjectRelationships) {
    return isTaskValidForProjectDetail(detail)
        && UserToProjectRelationshipUtil.canAccessProjectTask(ProjectTask.AWARDED_CONTRACTS, userToProjectRelationships);
  }

  @Override
  public boolean isTaskValidForProjectDetail(ProjectDetail detail) {
    return projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.AWARDED_CONTRACTS);
  }

  @Override
  public void removeSectionData(ProjectDetail projectDetail) {
    awardedContractRepository.deleteAllByProjectDetail(projectDetail);
  }

  @Override
  public void copySectionData(ProjectDetail fromDetail, ProjectDetail toDetail) {
    entityDuplicationService.duplicateEntitiesAndSetNewParent(
        awardedContractServiceCommon.getAwardedContracts(fromDetail),
        toDetail,
        AwardedContract.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }
}
