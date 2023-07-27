package uk.co.ogauthority.pathfinder.service.project.awardedcontract;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.awardedcontract.AwardedContractCommon;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormCommon;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractFormValidator;
import uk.co.ogauthority.pathfinder.model.form.project.awardedcontract.AwardedContractValidationHint;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public abstract class AwardedContractService {

  private final ValidationService validationService;
  private final AwardedContractFormValidator validator;

  protected final TeamService teamService;
  protected final SearchSelectorService searchSelectorService;

  @Autowired
  public AwardedContractService(TeamService teamService,
                                SearchSelectorService searchSelectorService,
                                ValidationService validationService,
                                AwardedContractFormValidator validator) {
    this.teamService = teamService;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.validator = validator;
  }

  public abstract List<? extends AwardedContractCommon> getAwardedContracts(ProjectDetail projectDetail);

  public abstract List<? extends AwardedContractCommon> getAwardedContractsByProjectAndVersion(Project project, Integer version);

  public abstract AwardedContractCommon getAwardedContract(Integer awardedContractId, ProjectDetail projectDetail);

  public abstract <E extends AwardedContractCommon> AwardedContractFormCommon getForm(E awardedContract);

  protected <F extends AwardedContractFormCommon> BindingResult validate(F form,
                                                                         BindingResult bindingResult,
                                                                         ValidationType validationType) {
    var awardedContractValidationHint = new AwardedContractValidationHint(validationType);
    validator.validate(form, bindingResult, awardedContractValidationHint);
    return validationService.validate(form, bindingResult, validationType);
  }

  protected AwardedContractFormCommon populateAwardedContractForm(AwardedContractCommon awardedContract,
                                                                  AwardedContractFormCommon form) {
    form.setContractorName(awardedContract.getContractorName());

    String contractFunction = null;

    if (awardedContract.getContractFunction() != null) {
      contractFunction = awardedContract.getContractFunction().getSelectionId();
    } else if (awardedContract.getManualContractFunction() != null) {
      contractFunction = SearchSelectorService.getValueWithManualEntryPrefix(
          awardedContract.getManualContractFunction()
      );
    }
    form.setContractFunction(contractFunction);

    form.setDescriptionOfWork(awardedContract.getDescriptionOfWork());
    form.setDateAwarded(new ThreeFieldDateInput(awardedContract.getDateAwarded()));
    form.setContractBand(awardedContract.getContractBand());
    form.setContactDetail(new ContactDetailForm(awardedContract));

    return form;
  }

  protected void populateAwardedContract(AwardedContractFormCommon form,
                                         AwardedContractCommon awardedContract) {
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
  }

  public boolean isValid(AwardedContractCommon awardedContract, ValidationType validationType) {
    var form = getForm(awardedContract);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public Map<String, String> getPreSelectedContractFunction(AwardedContractFormCommon form) {
    return searchSelectorService.getPreSelectedSearchSelectorValue(form.getContractFunction(), Function.values());
  }
}
