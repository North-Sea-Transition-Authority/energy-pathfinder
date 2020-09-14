package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class UpcomingTenderService {

  private final UpcomingTenderRepository upcomingTenderRepository;
  private final ValidationService validationService;
  private final UpcomingTenderFormValidator upcomingTenderFormValidator;
  private final FunctionService functionService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public UpcomingTenderService(UpcomingTenderRepository upcomingTenderRepository,
                               ValidationService validationService,
                               UpcomingTenderFormValidator upcomingTenderFormValidator,
                               FunctionService functionService,
                               SearchSelectorService searchSelectorService) {
    this.upcomingTenderRepository = upcomingTenderRepository;
    this.validationService = validationService;
    this.upcomingTenderFormValidator = upcomingTenderFormValidator;
    this.functionService = functionService;
    this.searchSelectorService = searchSelectorService;
  }


  @Transactional
  public UpcomingTender createUpcomingTender(ProjectDetail detail, UpcomingTenderForm form) {
    var upcomingTender = new UpcomingTender(detail);

    if (SearchSelectorService.isManualEntry(form.getTenderFunction())) {
      upcomingTender.setManualTenderFunction(searchSelectorService.removePrefix(form.getTenderFunction()));
    } else if (form.getTenderFunction() != null) {
      upcomingTender.setTenderFunction(Function.valueOf(form.getTenderFunction()));
    }
    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDate(form.getEstimatedTenderDate().createDateOrNull());
    upcomingTender.setContractBand(form.getContractBand());

    var contactDetailForm = form.getContactDetail();
    upcomingTender.setContactName(contactDetailForm.getName());
    upcomingTender.setPhoneNumber(contactDetailForm.getPhoneNumber());
    upcomingTender.setJobTitle(contactDetailForm.getJobTitle());
    upcomingTender.setEmailAddress(contactDetailForm.getEmailAddress());

    return upcomingTenderRepository.save(upcomingTender);
  }

  public BindingResult validate(UpcomingTenderForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    if (validationType.equals(ValidationType.FULL)) {
      upcomingTenderFormValidator.validate(form, bindingResult);
    } else {
      upcomingTenderFormValidator.validate(form, bindingResult, new EmptyDateAcceptableHint());
    }

    return validationService.validate(form, bindingResult, validationType);
  }

  /**
   * Search the TenderFunction enum displayNames for those that include searchTerm.
   * @param searchTerm Term to match against TenderFunction display names
   * @return return matching results plus manual entry
   */
  public List<RestSearchItem> findTenderFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.UPCOMING_TENDER);
  }

}
