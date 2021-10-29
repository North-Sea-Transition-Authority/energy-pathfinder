package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunitiesRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureCollaborationOpportunitiesServiceTest {

  @Mock
  private ValidationService validationService;

  @Mock
  private InfrastructureCollaborationOpportunitiesRepository infrastructureCollaborationOpportunitiesRepository;

  @Mock
  private InfrastructureCollaborationOpportunityFormValidator infrastructureCollaborationOpportunityFormValidator;

  @Mock
  private InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private InfrastructureCollaborationOpportunitiesService infrastructureCollaborationOpportunitiesService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final InfrastructureCollaborationOpportunity opportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(
      detail
  );

  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    infrastructureCollaborationOpportunitiesService = new InfrastructureCollaborationOpportunitiesService(
        searchSelectorService,
        functionService,
        validationService,
        infrastructureCollaborationOpportunityFormValidator,
        infrastructureCollaborationOpportunitiesRepository,
        infrastructureCollaborationOpportunityFileLinkService,
        projectDetailFileService,
        projectSetupService,
        entityDuplicationService
    );

    when(infrastructureCollaborationOpportunitiesRepository.save(any(InfrastructureCollaborationOpportunity.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createCollaborationOpportunity() {

    final var selectedFunction = InfrastructureCollaborationOpportunityTestUtil.FUNCTION;

    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(selectedFunction.getSelectionId());

    var newCollaborationOpportunity = infrastructureCollaborationOpportunitiesService.createCollaborationOpportunity(
        detail,
        form,
        userAccount
    );
    assertThat(newCollaborationOpportunity.getProjectDetail()).isEqualTo(detail);
    assertThat(newCollaborationOpportunity.getFunction()).isEqualTo(selectedFunction);
    checkCommonFields(form, newCollaborationOpportunity);
  }

  @Test
  public void createCollaborationOpportunity_manualFunction() {
    var form = InfrastructureCollaborationOpportunityTestUtil.getCompletedForm_manualEntry();
    var opportunity = infrastructureCollaborationOpportunitiesService.createCollaborationOpportunity(
        detail,
        form,
        userAccount
    );
    assertThat(opportunity.getProjectDetail()).isEqualTo(detail);
    checkCommonFields(form, opportunity);
  }

  @Test
  public void updateCollaborationOpportunity() {
    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(Function.DRILLING.name());
    var existingOpportunity = opportunity;
    infrastructureCollaborationOpportunitiesService.updateCollaborationOpportunity(existingOpportunity, form, userAccount);
    assertThat(existingOpportunity.getProjectDetail()).isEqualTo(detail);
    assertThat(existingOpportunity.getFunction()).isEqualTo(Function.DRILLING);
    checkCommonFields(form, existingOpportunity);
  }

  @Test
  public void updateCollaborationOpportunity_manualFunction() {

    final var selectedFunction = InfrastructureCollaborationOpportunityTestUtil.MANUAL_FUNCTION;

    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(selectedFunction);

    var existingOpportunity = opportunity;
    infrastructureCollaborationOpportunitiesService.updateCollaborationOpportunity(existingOpportunity, form, userAccount);
    assertThat(existingOpportunity.getProjectDetail()).isEqualTo(detail);
    assertThat(existingOpportunity.getManualFunction()).isEqualTo(SearchSelectorService.removePrefix(selectedFunction));
    checkCommonFields(form, existingOpportunity);
  }

  @Test
  public void getForm() {
    var form = infrastructureCollaborationOpportunitiesService.getForm(opportunity);
    assertThat(form.getFunction()).isEqualTo(opportunity.getFunction().name());
    checkCommonFormFields(form, opportunity);
  }

  @Test
  public void getFormManualEntry() {
    var manualEntryOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(detail);
    var form = infrastructureCollaborationOpportunitiesService.getForm(manualEntryOpportunity);
    assertThat(form.getFunction()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualEntryOpportunity.getManualFunction()));
    checkCommonFormFields(form, manualEntryOpportunity);
  }

  @Test
  public void validate_partial() {
    var form = new InfrastructureCollaborationOpportunityForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    infrastructureCollaborationOpportunitiesService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    infrastructureCollaborationOpportunitiesService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  private void checkCommonFields(InfrastructureCollaborationOpportunityForm form, InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity) {
    assertThat(infrastructureCollaborationOpportunity.getDescriptionOfWork()).isEqualTo(form.getDescriptionOfWork());
    assertThat(infrastructureCollaborationOpportunity.getUrgentResponseNeeded()).isEqualTo(form.getUrgentResponseNeeded());
    assertThat(infrastructureCollaborationOpportunity.getContactName()).isEqualTo(form.getContactDetail().getName());
    assertThat(infrastructureCollaborationOpportunity.getPhoneNumber()).isEqualTo(form.getContactDetail().getPhoneNumber());
    assertThat(infrastructureCollaborationOpportunity.getJobTitle()).isEqualTo(form.getContactDetail().getJobTitle());
    assertThat(infrastructureCollaborationOpportunity.getEmailAddress()).isEqualTo(form.getContactDetail().getEmailAddress());
  }

  private void checkCommonFormFields(InfrastructureCollaborationOpportunityForm form, InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(infrastructureCollaborationOpportunity.getDescriptionOfWork());
    assertThat(form.getUrgentResponseNeeded()).isEqualTo(infrastructureCollaborationOpportunity.getUrgentResponseNeeded());
    assertThat(form.getContactDetail().getName()).isEqualTo(infrastructureCollaborationOpportunity.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(infrastructureCollaborationOpportunity.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(infrastructureCollaborationOpportunity.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(infrastructureCollaborationOpportunity.getEmailAddress());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry() {
    var results = infrastructureCollaborationOpportunitiesService.findFunctionsLikeWithManualEntry(Function.FACILITIES_OFFSHORE.getDisplayName());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = infrastructureCollaborationOpportunitiesService.findFunctionsLikeWithManualEntry(manualEntry);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.COLLABORATION_OPPORTUNITIES)).thenReturn(true);
    assertThat(infrastructureCollaborationOpportunitiesService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.COLLABORATION_OPPORTUNITIES)).thenReturn(false);
    assertThat(infrastructureCollaborationOpportunitiesService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {
    final var collaborationOpportunity1 = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    final var collaborationOpportunity2 = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    final var collaborationOpportunities = List.of(collaborationOpportunity1, collaborationOpportunity2);

    when(infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(detail)).thenReturn(collaborationOpportunities);

    infrastructureCollaborationOpportunitiesService.removeSectionData(detail);

    verify(infrastructureCollaborationOpportunityFileLinkService, times(1)).removeCollaborationOpportunityFileLinks(
        collaborationOpportunities
    );
    verify(infrastructureCollaborationOpportunitiesRepository, times(1)).deleteAll(collaborationOpportunities);
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    final var collaborationOpportunities = List.of(
        InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail)
    );

    when(infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetailOrderByIdAsc(fromProjectDetail))
        .thenReturn(collaborationOpportunities);

    infrastructureCollaborationOpportunitiesService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        collaborationOpportunities,
        toProjectDetail,
        InfrastructureCollaborationOpportunity.class
    );

    verify(entityDuplicationService, times(1)).createDuplicatedEntityPairingMap(any());

    verify(infrastructureCollaborationOpportunityFileLinkService, times(1)).copyCollaborationOpportunityFileLinkData(
        eq(fromProjectDetail),
        eq(toProjectDetail),
        anyMap()
    );

  }

  @Test
  public void getOpportunitiesForProjectVersion_whenFound_thenReturnPopulatedList() {

    final var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(detail);
    final var collaborationOpportunityList = List.of(collaborationOpportunity);

    final var project = detail.getProject();
    final var version = detail.getVersion();

    when(infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(collaborationOpportunityList);

    var result = infrastructureCollaborationOpportunitiesService.getOpportunitiesForProjectVersion(project, version);
    assertThat(result).containsExactly(collaborationOpportunityList.get(0));
  }

  @Test
  public void getOpportunitiesForProjectVersion_whenNotFound_thenReturnEmptyList() {

    final var project = detail.getProject();
    final var version = detail.getVersion();

    when(infrastructureCollaborationOpportunitiesRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(Collections.emptyList());

    var result = infrastructureCollaborationOpportunitiesService.getOpportunitiesForProjectVersion(project, version);
    assertThat(result).isEmpty();
  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(infrastructureCollaborationOpportunitiesService.alwaysCopySectionData(detail)).isFalse();
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = infrastructureCollaborationOpportunitiesService.allowSectionDataCleanUp(detail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  @Test
  public void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(infrastructureCollaborationOpportunitiesService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }


}
