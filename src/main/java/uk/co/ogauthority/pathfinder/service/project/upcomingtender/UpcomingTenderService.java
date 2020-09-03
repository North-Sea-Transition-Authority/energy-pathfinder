package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.TenderFunction;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class UpcomingTenderService {

  private final UpcomingTenderRepository upcomingTenderRepository;
  private final SearchSelectorService searchSelectorService;
  private final ValidationService validationService;
  private final UpcomingTenderFormValidator upcomingTenderFormValidator;

  @Autowired
  public UpcomingTenderService(UpcomingTenderRepository upcomingTenderRepository,
                               SearchSelectorService searchSelectorService,
                               ValidationService validationService,
                               UpcomingTenderFormValidator upcomingTenderFormValidator) {
    this.upcomingTenderRepository = upcomingTenderRepository;
    this.searchSelectorService = searchSelectorService;
    this.validationService = validationService;
    this.upcomingTenderFormValidator = upcomingTenderFormValidator;
  }


  @Transactional
  public UpcomingTender createUpcomingTender(ProjectDetail detail, UpcomingTenderForm form) {
    var upcomingTender = new UpcomingTender(detail);

    if (SearchSelectorService.isManualEntry(form.getTenderFunction())) {
      upcomingTender.setManualTenderFunction(form.getTenderFunction());
    } else if (form.getTenderFunction() != null) {
      upcomingTender.setTenderFunction(TenderFunction.valueOf(form.getTenderFunction()));
    }
    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDate(form.getEstimatedTenderDate().createDateOrNull());
    upcomingTender.setContractBand(form.getContractBand());
    upcomingTender.setContactName(form.getName());
    upcomingTender.setPhoneNumber(form.getPhoneNumber());
    upcomingTender.setJobTitle(form.getJobTitle());
    upcomingTender.setEmailAddress(form.getEmailAddress());

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
    List<RestSearchItem> results = searchSelectorService.search(
        searchTerm,
        Arrays.asList(TenderFunction.values().clone())
      )
        .stream().sorted(Comparator.comparing(RestSearchItem::getText))
        .collect(Collectors.toList());
    searchSelectorService.addManualEntry(searchTerm, results);
    return results;
  }

}
