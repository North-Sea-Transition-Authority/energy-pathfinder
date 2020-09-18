package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContract;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractForm;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.repository.project.awardedcontract.AwardedContractRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class AwardedContractService {

  private final FunctionService functionService;
  private final ValidationService validationService;
  private final AwardedContractRepository awardedContractRepository;
  private final AwardedContractFormValidator awardedContractFormValidator;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public AwardedContractService(FunctionService functionService,
                                ValidationService validationService,
                                AwardedContractRepository awardedContractRepository,
                                AwardedContractFormValidator awardedContractFormValidator,
                                SearchSelectorService searchSelectorService) {
    this.functionService = functionService;
    this.validationService = validationService;
    this.awardedContractRepository = awardedContractRepository;
    this.awardedContractFormValidator = awardedContractFormValidator;
    this.searchSelectorService = searchSelectorService;
  }

  public AwardedContractForm getForm(Integer awardedContractId, ProjectDetail projectDetail) {
    var awardedContract = getAwardedContract(awardedContractId, projectDetail);
    return getForm(awardedContract);
  }

  public AwardedContractForm getForm(AwardedContract awardedContract) {
    var awardedContractForm = new AwardedContractForm();
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

  public BindingResult validate(AwardedContractForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    var awardedContractValidationHint = new AwardedContractValidationHint();
    awardedContractFormValidator.validate(form, bindingResult, awardedContractValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  @Transactional
  public AwardedContract createAwardedContract(ProjectDetail projectDetail, AwardedContractForm form) {
    var awardedContract = new AwardedContract(projectDetail);
    return createOrUpdateAwardedContract(awardedContract, form);
  }

  @Transactional
  public AwardedContract updateAwardedContract(Integer awardedContractId,
                                               ProjectDetail projectDetail,
                                               AwardedContractForm form) {
    var awardedContract = getAwardedContract(awardedContractId, projectDetail);
    return createOrUpdateAwardedContract(awardedContract, form);
  }

  @Transactional
  AwardedContract createOrUpdateAwardedContract(AwardedContract awardedContract, AwardedContractForm form) {

    awardedContract.setContractorName(form.getContractorName());

    if (SearchSelectorService.isManualEntry(form.getContractFunction())) {
      awardedContract.setManualContractFunction(SearchSelectorService.removePrefix(form.getContractFunction()));
      awardedContract.setContractFunction(null);
    } else if (form.getContractFunction() != null) {
      awardedContract.setContractFunction(Function.valueOf(form.getContractFunction()));
      awardedContract.setManualContractFunction(null);
    } else {
      awardedContract.setContractFunction(null);
      awardedContract.setManualContractFunction(null);
    }

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

  public Map<String, String> getPreSelectedContractFunction(AwardedContractForm form) {

    Map<String, String> preSelectedMap = Map.of();
    var contractFunction = form.getContractFunction();

    if (contractFunction != null) {
      if (SearchSelectorService.isManualEntry(contractFunction)) {
        preSelectedMap = searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(contractFunction),
            Map.of(contractFunction, contractFunction)
        );
      } else {
        preSelectedMap = searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(contractFunction),
            Map.of(contractFunction, Function.valueOf(contractFunction).getDisplayName())
        );
      }
    }

    return preSelectedMap;
  }

  public List<AwardedContract> getAwardedContracts(ProjectDetail projectDetail) {
    return awardedContractRepository.findByProjectDetailOrderByIdAsc(projectDetail);
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
  public void deleteAwardedContract(AwardedContract awardedContract) {
    awardedContractRepository.delete(awardedContract);
  }

  public boolean isValid(AwardedContract awardedContract, ValidationType validationType) {
    var form = getForm(awardedContract);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public boolean isComplete(ProjectDetail projectDetail) {
    var awardedContracts = getAwardedContracts(projectDetail);
    return !awardedContracts.isEmpty()
        && awardedContracts
        .stream()
        .allMatch(awardedContract -> isValid(awardedContract, ValidationType.FULL));
  }

}
