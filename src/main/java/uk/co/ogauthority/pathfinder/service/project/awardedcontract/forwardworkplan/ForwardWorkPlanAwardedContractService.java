package uk.co.ogauthority.pathfinder.service.project.awardedcontract.forwardworkplan;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.forwardworkplan.ForwardWorkPlanAwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.awardedcontract.AwardedContractService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class ForwardWorkPlanAwardedContractService extends AwardedContractService {

  private final ForwardWorkPlanAwardedContractRepository awardedContractRepository;

  @Autowired
  ForwardWorkPlanAwardedContractService(TeamService teamService,
                                        SearchSelectorService searchSelectorService,
                                        ValidationService validationService,
                                        AwardedContractFormValidator validator,
                                        ForwardWorkPlanAwardedContractRepository awardedContractRepository) {
    super(
        teamService,
        searchSelectorService,
        validationService,
        validator
    );
    this.awardedContractRepository = awardedContractRepository;
  }

  @Override
  public List<ForwardWorkPlanAwardedContract> getAwardedContracts(ProjectDetail projectDetail) {
    return awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  @Override
  public List<ForwardWorkPlanAwardedContract> getAwardedContractsByProjectAndVersion(Project project, Integer version) {
    return awardedContractRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  @Override
  public ForwardWorkPlanAwardedContract getAwardedContract(Integer awardedContractId, ProjectDetail projectDetail) {
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
    var awardedContractForm = new ForwardWorkPlanAwardedContractForm();
    return super.populateAwardedContractForm(awardedContract, awardedContractForm);
  }

  public boolean hasAwardedContracts(ProjectDetail projectDetail) {
    return awardedContractRepository.existsByProjectDetail(projectDetail);
  }

  public boolean isValid(ForwardWorkPlanAwardedContract awardedContract, ValidationType validationType) {
    return super.isValid(awardedContract, validationType);
  }

  public BindingResult validate(ForwardWorkPlanAwardedContractForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    return super.validate(form, bindingResult, validationType);
  }

  @Transactional
  public ForwardWorkPlanAwardedContract createAwardedContract(ProjectDetail projectDetail,
                                                              ForwardWorkPlanAwardedContractForm form,
                                                              AuthenticatedUserAccount userAccount) {
    var awardedContract = new ForwardWorkPlanAwardedContract(projectDetail);
    var portalOrganisationGroup = teamService.getContributorPortalOrganisationGroup(userAccount);
    awardedContract.setAddedByOrganisationGroup(portalOrganisationGroup.getOrgGrpId());

    super.populateAwardedContract(form, awardedContract);
    return awardedContractRepository.save(awardedContract);
  }

  @Transactional
  public ForwardWorkPlanAwardedContract updateAwardedContract(Integer awardedContractId,
                                                              ProjectDetail projectDetail,
                                                              ForwardWorkPlanAwardedContractForm form) {
    var awardedContract = getAwardedContract(awardedContractId, projectDetail);
    super.populateAwardedContract(form, awardedContract);
    return awardedContractRepository.save(awardedContract);
  }

  @Transactional
  public void deleteAwardedContract(ForwardWorkPlanAwardedContract awardedContract) {
    awardedContractRepository.delete(awardedContract);
  }

  public void deleteAllByProjectDetail(ProjectDetail projectDetail) {
    awardedContractRepository.deleteAllByProjectDetail(projectDetail);
  }

}
