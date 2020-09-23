package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.contact.ContactDetailForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.ThreeFieldDateInput;
import uk.co.ogauthority.pathfinder.model.form.forminput.dateinput.validationhint.EmptyDateAcceptableHint;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;

@Service
public class CollaborationOpportunitiesService {


  private final SearchSelectorService searchSelectorService;
  private final FunctionService functionService;
  private final ValidationService validationService;
  private final CollaborationOpportunityFormValidator collaborationOpportunityFormValidator;
  private final CollaborationOpportunitiesRepository collaborationOpportunitiesRepository;

  @Autowired
  public CollaborationOpportunitiesService(SearchSelectorService searchSelectorService,
                                           FunctionService functionService,
                                           ValidationService validationService,
                                           CollaborationOpportunityFormValidator collaborationOpportunityFormValidator,
                                           CollaborationOpportunitiesRepository collaborationOpportunitiesRepository) {
    this.searchSelectorService = searchSelectorService;
    this.functionService = functionService;
    this.validationService = validationService;
    this.collaborationOpportunityFormValidator = collaborationOpportunityFormValidator;
    this.collaborationOpportunitiesRepository = collaborationOpportunitiesRepository;
  }


  public BindingResult validate(CollaborationOpportunityForm form,
                                BindingResult bindingResult,
                                ValidationType validationType) {
    if (validationType.equals(ValidationType.FULL)) {
      collaborationOpportunityFormValidator.validate(form, bindingResult);
    } else {
      collaborationOpportunityFormValidator.validate(form, bindingResult, new EmptyDateAcceptableHint());
    }

    return validationService.validate(form, bindingResult, validationType);
  }

  public boolean isValid(CollaborationOpportunity opportunity, ValidationType validationType) {
    var form = getForm(opportunity);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = validate(form, bindingResult, validationType);
    return !bindingResult.hasErrors();
  }

  public boolean isComplete(ProjectDetail detail) {
    var opportunities =  getOpportunitiesForDetail(detail);
    return !opportunities.isEmpty() && opportunities.stream()
        .allMatch(ut -> isValid(ut, ValidationType.FULL));
  }

  @Transactional
  public CollaborationOpportunity createCollaborationOpportunity(ProjectDetail detail, CollaborationOpportunityForm form) {
    var opportunity = new CollaborationOpportunity(detail);
    setCommonFields(opportunity, form);

    return collaborationOpportunitiesRepository.save(opportunity);
  }

  @Transactional
  public CollaborationOpportunity updateCollaborationOpportunity(CollaborationOpportunity opportunity,
                                                                 CollaborationOpportunityForm form) {
    setCommonFields(opportunity, form);
    return collaborationOpportunitiesRepository.save(opportunity);
  }

  private void setCommonFields(CollaborationOpportunity opportunity, CollaborationOpportunityForm form) {
    if (SearchSelectorService.isManualEntry(form.getFunction())) {
      opportunity.setManualFunction(SearchSelectorService.removePrefix(form.getFunction()));
      opportunity.setFunction(null);
    } else if (form.getFunction() != null) {
      opportunity.setFunction(Function.valueOf(form.getFunction()));
      opportunity.setManualFunction(null);
    }
    opportunity.setDescriptionOfWork(form.getDescriptionOfWork());
    opportunity.setEstimatedServiceDate(form.getEstimatedServiceDate().createDateOrNull());

    var contactDetailForm = form.getContactDetail();
    opportunity.setContactName(contactDetailForm.getName());
    opportunity.setPhoneNumber(contactDetailForm.getPhoneNumber());
    opportunity.setJobTitle(contactDetailForm.getJobTitle());
    opportunity.setEmailAddress(contactDetailForm.getEmailAddress());
  }

  @Transactional
  public void delete(CollaborationOpportunity opportunity) {
    collaborationOpportunitiesRepository.delete(opportunity);
  }

  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.COLLABORATION_OPPORTUNITY);
  }

  public List<CollaborationOpportunity> getOpportunitiesForDetail(ProjectDetail detail) {
    return collaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(detail);
  }

  public Map<String, String> getPreSelectedCollaborationFunction(CollaborationOpportunityForm form) {

    Map<String, String> preSelectedMap = Map.of();
    var function = form.getFunction();

    if (function != null) {
      return SearchSelectorService.isManualEntry(form.getFunction())
        ? searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(form.getFunction()),
            Map.of(form.getFunction(), form.getFunction())
          )
        : searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(form.getFunction()),
            Map.of(form.getFunction(), Function.valueOf(form.getFunction()).getDisplayName())
          );
    }

    return preSelectedMap;
  }

  public CollaborationOpportunity getOrError(Integer opportunityId) {
    return collaborationOpportunitiesRepository.findById(opportunityId)
        .orElseThrow(
            () -> new PathfinderEntityNotFoundException(
                String.format("Unable to find collaborationOpportunity with ID %d", opportunityId)
          )
        );
  }

  public CollaborationOpportunityForm getForm(CollaborationOpportunity opportunity) {
    var form = new CollaborationOpportunityForm();

    if (opportunity.getFunction() != null) {
      form.setFunction(opportunity.getFunction().name());
    } else if (opportunity.getManualFunction() != null) {
      form.setFunction(SearchSelectorService.getValueWithManualEntryPrefix(opportunity.getManualFunction()));
    }

    form.setEstimatedServiceDate(new ThreeFieldDateInput(opportunity.getEstimatedServiceDate()));
    form.setDescriptionOfWork(opportunity.getDescriptionOfWork());
    form.setContactDetail(new ContactDetailForm(opportunity));
    return form;
  }
}
