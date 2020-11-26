package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private CollaborationOpportunitiesRepository collaborationOpportunitiesRepository;

  @Mock
  private CollaborationOpportunityFormValidator collaborationOpportunityFormValidator;

  @Mock
  private CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ProjectSetupService projectSetupService;

  private CollaborationOpportunitiesService collaborationOpportunitiesService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final CollaborationOpportunity opportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(
      detail
  );

  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    collaborationOpportunitiesService = new CollaborationOpportunitiesService(
        searchSelectorService,
        functionService,
        validationService,
        collaborationOpportunityFormValidator,
        collaborationOpportunitiesRepository,
        collaborationOpportunityFileLinkService,
        projectDetailFileService,
        projectSetupService);

    when(collaborationOpportunitiesRepository.save(any(CollaborationOpportunity.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createCollaborationOpportunity() {
    var form = CollaborationOpportunityTestUtil.getCompleteForm();
    var newCollaborationOpportunity = collaborationOpportunitiesService.createCollaborationOpportunity(
        detail,
        form,
        userAccount
    );
    assertThat(newCollaborationOpportunity.getProjectDetail()).isEqualTo(detail);
    assertThat(newCollaborationOpportunity.getFunction()).isEqualTo(CollaborationOpportunityTestUtil.FUNCTION);
    checkCommonFields(form, newCollaborationOpportunity);
  }

  @Test
  public void createCollaborationOpportunity_manualFunction() {
    var form = CollaborationOpportunityTestUtil.getCompletedForm_manualEntry();
    var opportunity = collaborationOpportunitiesService.createCollaborationOpportunity(
        detail,
        form,
        userAccount
    );
    assertThat(opportunity.getProjectDetail()).isEqualTo(detail);
    checkCommonFields(form, opportunity);
  }

  @Test
  public void updateCollaborationOpportunity() {
    var form = CollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(Function.DRILLING.name());
    var existingOpportunity = opportunity;
    collaborationOpportunitiesService.updateCollaborationOpportunity(existingOpportunity, form, userAccount);
    assertThat(existingOpportunity.getProjectDetail()).isEqualTo(detail);
    assertThat(existingOpportunity.getFunction()).isEqualTo(Function.DRILLING);
    checkCommonFields(form, existingOpportunity);
  }

  @Test
  public void updateCollaborationOpportunity_manualFunction() {
    var form = CollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(CollaborationOpportunityTestUtil.MANUAL_FUNCTION);
    var existingOpportunity = opportunity;
    collaborationOpportunitiesService.updateCollaborationOpportunity(existingOpportunity, form, userAccount);
    assertThat(existingOpportunity.getProjectDetail()).isEqualTo(detail);
    assertThat(existingOpportunity.getManualFunction()).isEqualTo(SearchSelectorService.removePrefix(CollaborationOpportunityTestUtil.MANUAL_FUNCTION));
    checkCommonFields(form, existingOpportunity);
  }

  @Test
  public void getForm() {
    var form = collaborationOpportunitiesService.getForm(opportunity);
    assertThat(form.getFunction()).isEqualTo(opportunity.getFunction().name());
    checkCommonFormFields(form, opportunity);
  }

  @Test
  public void getFormManualEntry() {
    var manualEntryOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(detail);
    var form = collaborationOpportunitiesService.getForm(manualEntryOpportunity);
    assertThat(form.getFunction()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualEntryOpportunity.getManualFunction()));
    checkCommonFormFields(form, manualEntryOpportunity);
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
    var form = CollaborationOpportunityTestUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    collaborationOpportunitiesService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  private void checkCommonFields(CollaborationOpportunityForm form, CollaborationOpportunity collaborationOpportunity) {
    assertThat(collaborationOpportunity.getDescriptionOfWork()).isEqualTo(CollaborationOpportunityTestUtil.DESCRIPTION_OF_WORK);
    assertThat(collaborationOpportunity.getUrgentResponseNeeded()).isEqualTo(form.getUrgentResponseNeeded());
    assertThat(collaborationOpportunity.getContactName()).isEqualTo(form.getContactDetail().getName());
    assertThat(collaborationOpportunity.getPhoneNumber()).isEqualTo(form.getContactDetail().getPhoneNumber());
    assertThat(collaborationOpportunity.getJobTitle()).isEqualTo(form.getContactDetail().getJobTitle());
    assertThat(collaborationOpportunity.getEmailAddress()).isEqualTo(form.getContactDetail().getEmailAddress());
  }

  private void checkCommonFormFields(CollaborationOpportunityForm form, CollaborationOpportunity collaborationOpportunity) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(collaborationOpportunity.getDescriptionOfWork());
    assertThat(form.getUrgentResponseNeeded()).isEqualTo(collaborationOpportunity.getUrgentResponseNeeded());
    assertThat(form.getContactDetail().getName()).isEqualTo(collaborationOpportunity.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(collaborationOpportunity.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(collaborationOpportunity.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(collaborationOpportunity.getEmailAddress());
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

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.COLLABORATION_OPPORTUNITIES)).thenReturn(true);
    assertThat(collaborationOpportunitiesService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.COLLABORATION_OPPORTUNITIES)).thenReturn(false);
    assertThat(collaborationOpportunitiesService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    final var collaborationOpportunity1 = CollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    final var collaborationOpportunity2 = CollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    final var collaborationOpportunities = List.of(collaborationOpportunity1, collaborationOpportunity2);

    when(collaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(detail)).thenReturn(collaborationOpportunities);

    collaborationOpportunitiesService.removeSectionData(detail);

    verify(collaborationOpportunityFileLinkService, times(1)).removeCollaborationOpportunityFileLinks(
        collaborationOpportunities
    );
    verify(collaborationOpportunitiesRepository, times(1)).deleteAll(collaborationOpportunities);
  }

}
