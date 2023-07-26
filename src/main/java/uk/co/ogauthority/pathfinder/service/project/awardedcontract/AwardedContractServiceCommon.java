package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.infrastructure.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.infrastructure.InfrastructureAwardedContractForm;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.infrastructure.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class AwardedContractServiceCommon {
  private final AwardedContractRepository awardedContractRepository;
  private final FunctionService functionService;
  private final TeamService teamService;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final AwardedContractFormValidator validator;

  public AwardedContractServiceCommon(AwardedContractRepository awardedContractRepository,
                                      FunctionService functionService,
                                      TeamService teamService,
                                      SearchSelectorService searchSelectorService,
                                      ValidationService validationService,
                                      AwardedContractFormValidator validator) {
    this.awardedContractRepository = awardedContractRepository;
    this.functionService = functionService;
    this.teamService = teamService;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.validator = validator;
  }

  public AwardedContractFormCommon getForm(Integer awardedContractId, ProjectDetail projectDetail) {
    var awardedContract = getAwardedContract(awardedContractId, projectDetail);
    return getForm(awardedContract);
  }

  public AwardedContractFormCommon getForm(AwardedContract awardedContract) {
    var awardedContractForm = new InfrastructureAwardedContractForm();
    awardedContractForm.setContractorName(awardedContract.getContractorName());

    String contractFunction = null;

    if (awardedContract.getContractFunction() != null) {
      contractFunction = awardedContract.getContractFunction().getSelectionId();
    } else if (awardedContract.getManualContractFunction() != null) {
      contractFunction = SearchSelectorService.getValueWithManualEntryPrefix(
          awardedContract.getManualContractFunction()
      );
    }
    awardedContractForm.setContractFunction(contractFunction);

    awardedContractForm.setDescriptionOfWork(awardedContract.getDescriptionOfWork());
    awardedContractForm.setDateAwarded(new ThreeFieldDateInput(awardedContract.getDateAwarded()));
    awardedContractForm.setContractBand(awardedContract.getContractBand());
    awardedContractForm.setContactDetail(new ContactDetailForm(awardedContract));

    return awardedContractForm;
  }

  /**
   * Search the Function enum displayNames for those that include searchTerm.
   * @param searchTerm Term to match against Function display names
   * @return return matching results plus manual entry
   */
  public List<RestSearchItem> findContractFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.AWARDED_CONTRACT);
  }

  public AwardedContract getAwardedContract(Integer awardedContractId, ProjectDetail projectDetail) {
    return awardedContractRepository.findByIdAndProjectDetail(awardedContractId, projectDetail)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(
            String.format(
                "No AwardedContract found with id %s for ProjectDetail with id %s",
                awardedContractId,
                projectDetail != null ? projectDetail.getId() : "null"
            )
        ));
  }

  @Transactional
  public AwardedContract createAwardedContract(ProjectDetail projectDetail,
                                               AwardedContractFormCommon form,
                                               AuthenticatedUserAccount userAccount) {
    var awardedContract = new AwardedContract(projectDetail);
    var portalOrganisationGroup = teamService.getContributorPortalOrganisationGroup(userAccount);

    awardedContract.setAddedByOrganisationGroup(portalOrganisationGroup.getOrgGrpId());
    return createOrUpdateAwardedContract(awardedContract, form);
  }

  @Transactional
  public AwardedContract updateAwardedContract(Integer awardedContractId,
                                               ProjectDetail projectDetail,
                                               AwardedContractFormCommon form) {
    var awardedContract = getAwardedContract(awardedContractId, projectDetail);
    return createOrUpdateAwardedContract(awardedContract, form);
  }

  private AwardedContract createOrUpdateAwardedContract(AwardedContract awardedContract, AwardedContractFormCommon form) {

    awardedContract.setContractorName(form.getContractorName());

    searchSelectorService.mapSearchSelectorFormEntryToEntity(
        form.getContractFunction(),
        Function.values(),
        awardedContract::setManualContractFunction,
        awardedContract::setContractFunction
    );

    awardedContract.setDescriptionOfWork(form.getDescriptionOfWork());
    awardedContract.setDateAwarded(form.getDateAwarded().createDateOrNull());
    awardedContract.setContractBand(form.getContractBand());

    var contactDetailForm = form.getContactDetail();
    awardedContract.setContactName(contactDetailForm.getName());
    awardedContract.setPhoneNumber(contactDetailForm.getPhoneNumber());
    awardedContract.setEmailAddress(contactDetailForm.getEmailAddress());
    awardedContract.setJobTitle(contactDetailForm.getJobTitle());

    return awardedContractRepository.save(awardedContract);
  }

  public Map<String, String> getPreSelectedContractFunction(AwardedContractFormCommon form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getContractFunction(), Function.values());
  }

  public List<AwardedContract> getAwardedContracts(ProjectDetail projectDetail) {
    return awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail);
  }

  public List<AwardedContract> getAwardedContractsByProjectAndVersion(Project project, Integer version) {
    return awardedContractRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version);
  }

  public boolean hasAwardedContracts(ProjectDetail projectDetail) {
    return awardedContractRepository.existsByProjectDetail(projectDetail);
  }

  public BindingResult validate(AwardedContractFormCommon form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var awardedContractValidationHint = new AwardedContractValidationHint(validationType);
    validator.validate(form, bindingResult, awardedContractValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(AwardedContract awardedContract, ValidationType validationType) {
    var form = getForm(awardedContract);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  @Transactional
  public void deleteAwardedContract(AwardedContract awardedContract) {
    awardedContractRepository.delete(awardedContract);
  }
}
