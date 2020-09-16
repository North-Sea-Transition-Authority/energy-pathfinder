package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
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

  @Transactional
  public CollaborationOpportunity createCollaborationOpportunity(ProjectDetail detail, CollaborationOpportunityForm form) {
    var opportunity = new CollaborationOpportunity(detail);
    if (SearchSelectorService.isManualEntry(form.getFunction())) {
      opportunity.setManualFunction(searchSelectorService.removePrefix(form.getFunction()));
    } else if (form.getFunction() != null) {
      opportunity.setFunction(Function.valueOf(form.getFunction()));
    }
    opportunity.setDescriptionOfWork(form.getDescriptionOfWork());
    opportunity.setEstimatedServiceDate(form.getEstimatedServiceDate().createDateOrNull());
    opportunity.setContractBand(form.getContractBand());

    var contactDetailForm = form.getContactDetail();
    opportunity.setContactName(contactDetailForm.getName());
    opportunity.setPhoneNumber(contactDetailForm.getPhoneNumber());
    opportunity.setJobTitle(contactDetailForm.getJobTitle());
    opportunity.setEmailAddress(contactDetailForm.getEmailAddress());

    return collaborationOpportunitiesRepository.save(opportunity);
  }

  public List<RestSearchItem> findFunctionsLikeWithManualEntry(String searchTerm) {
    return functionService.findFunctionsLikeWithManualEntry(searchTerm, FunctionType.COLLABORATION_OPPORTUNITY);
  }
}
