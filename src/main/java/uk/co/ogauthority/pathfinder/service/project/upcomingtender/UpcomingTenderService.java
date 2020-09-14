package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.TenderFunction;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
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

    setCommonFields(upcomingTender, form);
    return upcomingTenderRepository.save(upcomingTender);
  }

  @Transactional
  public UpcomingTender updateUpcomingTender(UpcomingTender upcomingTender, UpcomingTenderForm form) {
    setCommonFields(upcomingTender, form);
    return upcomingTenderRepository.save(upcomingTender);
  }

  private void setCommonFields(UpcomingTender upcomingTender, UpcomingTenderForm form) {
    if (SearchSelectorService.isManualEntry(form.getTenderFunction())) {
      upcomingTender.setManualTenderFunction(SearchSelectorService.removePrefix(form.getTenderFunction()));
      upcomingTender.setTenderFunction(null);
    } else if (form.getTenderFunction() != null) {
      upcomingTender.setTenderFunction(getFunctionFromString(form.getTenderFunction()));
      upcomingTender.setManualTenderFunction(null);
    }
    upcomingTender.setDescriptionOfWork(form.getDescriptionOfWork());
    upcomingTender.setEstimatedTenderDate(form.getEstimatedTenderDate().createDateOrNull());
    upcomingTender.setContractBand(form.getContractBand());

    var contactDetailForm = form.getContactDetail();
    upcomingTender.setContactName(contactDetailForm.getName());
    upcomingTender.setPhoneNumber(contactDetailForm.getPhoneNumber());
    upcomingTender.setJobTitle(contactDetailForm.getJobTitle());
    upcomingTender.setEmailAddress(contactDetailForm.getEmailAddress());
  }

  public UpcomingTender getOrError(Integer upcomingTenderId) {
    return upcomingTenderRepository.findById(upcomingTenderId).orElseThrow(() ->
        new PathfinderEntityNotFoundException(
            String.format("Unable to find tender with id: %s", upcomingTenderId)
        )
    );
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

  public boolean isValid(UpcomingTender upcomingTender, ValidationType validationType) {
    var form = getForm(upcomingTender);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public UpcomingTenderForm getForm(UpcomingTender upcomingTender) {
    var form = new UpcomingTenderForm();

    if (upcomingTender.getManualTenderFunction() != null) {
      form.setTenderFunction(SearchSelectorService.getValueWithManualEntryPrefix(upcomingTender.getManualTenderFunction()));
    } else {
      form.setTenderFunction(upcomingTender.getTenderFunction().name());
    }

    form.setEstimatedTenderDate(new ThreeFieldDateInput(upcomingTender.getEstimatedTenderDate()));
    form.setDescriptionOfWork(upcomingTender.getDescriptionOfWork());
    form.setContractBand(upcomingTender.getContractBand());
    form.setContactDetail(new ContactDetailForm(upcomingTender));
    return form;
  }

  public List<UpcomingTender> getUpcomingTendersForDetail(ProjectDetail detail) {
    return upcomingTenderRepository.findByProjectDetailOrderByIdAsc(detail);
  }

  /**
   * If there's data in the form turn it back into a format the searchSelector can parse.
   * @param form valid or invalid UpcomingTenderForm
   * @return id and display name of the search selector items empty map if there's no form data.
   */
  public Map<String, String> getPreSelectedFunction(UpcomingTenderForm form) {
    if (form.getTenderFunction() != null) {
      return SearchSelectorService.isManualEntry(form.getTenderFunction())
          ? searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(form.getTenderFunction()),
              Map.of(form.getTenderFunction(), form.getTenderFunction())
            )
          : searchSelectorService.buildPrePopulatedSelections(
              Collections.singletonList(form.getTenderFunction()),
              Map.of(form.getTenderFunction(), getFunctionFromString(form.getTenderFunction()).getDisplayName())
            );

    }
    return Map.of();
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

  private TenderFunction getFunctionFromString(String s) {
    return TenderFunction.valueOf(s);
  }

}
