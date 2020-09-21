package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private CollaborationOpportunitiesRepository collaborationOpportunitiesRepository;

  @Mock
  private CollaborationOpportunityFormValidator collaborationOpportunityFormValidator;

  private CollaborationOpportunitiesService collaborationOpportunitiesService;

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    collaborationOpportunitiesService = new CollaborationOpportunitiesService(
        searchSelectorService,
        functionService,
        validationService,
        collaborationOpportunityFormValidator,
        collaborationOpportunitiesRepository
    );

    when(collaborationOpportunitiesRepository.save(any(CollaborationOpportunity.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createCollaborationOpportunity() {
    var form = CollaborationOpportunityUtil.getCompleteForm();
    var newCollaborationOpportunity = collaborationOpportunitiesService.createCollaborationOpportunity(details, form);
    assertThat(newCollaborationOpportunity.getProjectDetail()).isEqualTo(details);
    assertThat(newCollaborationOpportunity.getFunction()).isEqualTo(CollaborationOpportunityUtil.FUNCTION);
    checkCommonFields(form, newCollaborationOpportunity);
  }

  @Test
  public void createCollaborationOpportunity_manualFunction() {
    var form = CollaborationOpportunityUtil.getCompletedForm_manualEntry();
    var opportunity = collaborationOpportunitiesService.createCollaborationOpportunity(details, form);
    assertThat(opportunity.getProjectDetail()).isEqualTo(details);
    checkCommonFields(form, opportunity);
  }

  @Test
  public void validate_partial() {
    var form = new CollaborationOpportunityForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    collaborationOpportunitiesService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = CollaborationOpportunityUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    collaborationOpportunitiesService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  private void checkCommonFields(CollaborationOpportunityForm form, CollaborationOpportunity collaborationOpportunity) {
    assertThat(collaborationOpportunity.getDescriptionOfWork()).isEqualTo(CollaborationOpportunityUtil.DESCRIPTION_OF_WORK);
    assertThat(collaborationOpportunity.getEstimatedServiceDate()).isEqualTo(form.getEstimatedServiceDate().createDateOrNull());
    assertThat(collaborationOpportunity.getContactName()).isEqualTo(CollaborationOpportunityUtil.CONTACT_NAME);
    assertThat(collaborationOpportunity.getPhoneNumber()).isEqualTo(CollaborationOpportunityUtil.PHONE_NUMBER);
    assertThat(collaborationOpportunity.getJobTitle()).isEqualTo(CollaborationOpportunityUtil.JOB_TITLE);
    assertThat(collaborationOpportunity.getEmailAddress()).isEqualTo(CollaborationOpportunityUtil.EMAIL);
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry() {
    var results = collaborationOpportunitiesService.findFunctionsLikeWithManualEntry(Function.FACILITIES_OFFSHORE.getDisplayName());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = collaborationOpportunitiesService.findFunctionsLikeWithManualEntry(manualEntry);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }

}
