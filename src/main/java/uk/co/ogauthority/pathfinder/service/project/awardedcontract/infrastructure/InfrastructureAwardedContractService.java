package uk.co.ogauthority.pathfinder.service.project.awardedcontract.infrastructure;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.InfrastructureAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.infrastructure.InfrastructureAwardedContractForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.InfrastructureAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.project.tasks.ProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.util.projectcontext.UserToProjectRelationshipUtil;

@Service
public class InfrastructureAwardedContractService extends AwardedContractService implements ProjectFormSectionService {

  private final ProjectSetupService projectSetupService;
  private final EntityDuplicationService entityDuplicationService;
  private final InfrastructureAwardedContractRepository awardedContractRepository;

  @Autowired
  public InfrastructureAwardedContractService(TeamService teamService,
                                              SearchSelectorService searchSelectorService,
                                              ValidationService validationService,
                                              AwardedContractFormValidator validator,
                                              ProjectSetupService projectSetupService,
                                              EntityDuplicationService entityDuplicationService,
                                              InfrastructureAwardedContractRepository awardedContractRepository) {
    super(
        teamService,
        searchSelectorService,
        validationService,
        validator
    );
    this.projectSetupService = projectSetupService;
    this.entityDuplicationService = entityDuplicationService;
    this.awardedContractRepository = awardedContractRepository;
  }

  @Override
  public List<InfrastructureAwardedContract> getAwardedContracts(ProjectDetail projectDetail) {
    return awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  @Override
  public List<InfrastructureAwardedContract> getAwardedContractsByProjectAndVersion(Project project, Integer version) {
    return awardedContractRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  @Override
  public InfrastructureAwardedContract getAwardedContract(Integer awardedContractId, ProjectDetail projectDetail) {
    return awardedContractRepository.findByIdAndProjectDetail(awardedContractId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "No AwardedContract found with id %s for ProjectDetail with id %s",
                awardedContractId,
                projectDetail != null ? projectDetail.getId() : "null"
            )
        ));
  }

  @Override
  public AwardedContractFormCommon getForm(AwardedContractCommon awardedContract) {
    var awardedContractForm = new InfrastructureAwardedContractForm();
    return super.populateAwardedContractForm(awardedContract, awardedContractForm);
  }

  public boolean isValid(InfrastructureAwardedContract awardedContract, ValidationType validationType) {
    return super.isValid(awardedContract, validationType);
  }

  public BindingResult validate(InfrastructureAwardedContractForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return super.validate(form, bindingResult, validationType);
  }

  @Override
  public boolean isComplete(ProjectDetail projectDetail) {
    var awardedContracts = getAwardedContracts(projectDetail);
    return !awardedContracts.isEmpty()
        && awardedContracts
        .stream()
        .allMatch(awardedContract -> isValid(awardedContract, ValidationType.FULL));
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
        getAwardedContracts(fromDetail),
        toDetail,
        InfrastructureAwardedContract.class
    );
  }

  @Override
  public Set<ProjectType> getSupportedProjectTypes() {
    return Set.of(ProjectType.INFRASTRUCTURE);
  }

  @Transactional
  public InfrastructureAwardedContract createAwardedContract(ProjectDetail projectDetail,
                                                             InfrastructureAwardedContractForm form,
                                                             AuthenticatedUserAccount userAccount) {
    var awardedContract = new InfrastructureAwardedContract(projectDetail);
    var portalOrganisationGroup = teamService.getContributorPortalOrganisationGroup(userAccount);
    awardedContract.setAddedByOrganisationGroup(portalOrganisationGroup.getOrgGrpId());

    super.populateAwardedContract(form, awardedContract);
    return awardedContractRepository.save(awardedContract);
  }

  @Transactional
  public InfrastructureAwardedContract updateAwardedContract(Integer awardedContractId,
                                                             ProjectDetail projectDetail,
                                                             InfrastructureAwardedContractForm form) {
    var awardedContract = getAwardedContract(awardedContractId, projectDetail);
    super.populateAwardedContract(form, awardedContract);
    return awardedContractRepository.save(awardedContract);
  }

  @Transactional
  public void deleteAwardedContract(InfrastructureAwardedContract awardedContract) {
    awardedContractRepository.delete(awardedContract);
  }
}
