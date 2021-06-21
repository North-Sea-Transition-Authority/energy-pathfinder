package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityRepository forwardWorkPlanCollaborationOpportunityRepository;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFormValidator forwardWorkPlanCollaborationOpportunityFormValidator;

  @Mock
  private ValidationService validationService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final ForwardWorkPlanCollaborationOpportunity opportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
      projectDetail
  );

  private final AuthenticatedUserAccount userAccount = UserTestingUtil.getAuthenticatedUserAccount();

  private ForwardWorkPlanCollaborationOpportunityService forwardWorkPlanCollaborationOpportunityService;

  @Before
  public void setup() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    forwardWorkPlanCollaborationOpportunityService = new ForwardWorkPlanCollaborationOpportunityService(
        searchSelectorService,
        functionService,
        projectSetupService,
        projectDetailFileService,
        forwardWorkPlanCollaborationOpportunityRepository,
        forwardWorkPlanCollaborationOpportunityFileLinkService,
        forwardWorkPlanCollaborationOpportunityFormValidator,
        validationService,
        entityDuplicationService
    );

    when(forwardWorkPlanCollaborationOpportunityRepository.save(any(ForwardWorkPlanCollaborationOpportunity.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void canShowInTaskList_smokeTestProjectTypes_assertOnlyForwardWorkPlanAllowed() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var projectTypesToShowInTaskList = Set.of(ProjectType.FORWARD_WORK_PLAN);

    Arrays.asList(ProjectType.values()).forEach(projectType -> {

      projectDetail.setProjectType(projectType);

      final var canShowInTaskList = forwardWorkPlanCollaborationOpportunityService.canShowInTaskList(projectDetail);

      if (projectTypesToShowInTaskList.contains(projectType)) {
        assertThat(canShowInTaskList).isTrue();
      } else {
        assertThat(canShowInTaskList).isFalse();
      }
    });
  }

  @Test
  public void createCollaborationOpportunity_whenFromListFunction() {

    final var selectedFunction = ForwardWorkPlanCollaborationOpportunityTestUtil.FUNCTION;

    var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(selectedFunction.getSelectionId());

    var newCollaborationOpportunity = forwardWorkPlanCollaborationOpportunityService.createCollaborationOpportunity(
        projectDetail,
        form,
        userAccount
    );
    assertThat(newCollaborationOpportunity.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(newCollaborationOpportunity.getFunction()).isEqualTo(selectedFunction);
    checkCommonFields(form, newCollaborationOpportunity);
  }

  @Test
  public void createCollaborationOpportunity_manualFunction() {
    var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompletedForm_manualEntry();
    var opportunity = forwardWorkPlanCollaborationOpportunityService.createCollaborationOpportunity(
        projectDetail,
        form,
        userAccount
    );
    assertThat(opportunity.getProjectDetail()).isEqualTo(projectDetail);
    checkCommonFields(form, opportunity);
  }

  @Test
  public void updateCollaborationOpportunity() {
    var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(Function.DRILLING.name());

    var existingOpportunity = opportunity;

    forwardWorkPlanCollaborationOpportunityService.updateCollaborationOpportunity(existingOpportunity, form, userAccount);

    assertThat(existingOpportunity.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(existingOpportunity.getFunction()).isEqualTo(Function.DRILLING);
    checkCommonFields(form, existingOpportunity);
  }

  @Test
  public void updateCollaborationOpportunity_manualFunction() {

    final var selectedFunction = ForwardWorkPlanCollaborationOpportunityTestUtil.MANUAL_FUNCTION;

    var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    form.setFunction(selectedFunction);

    var existingOpportunity = opportunity;

    forwardWorkPlanCollaborationOpportunityService.updateCollaborationOpportunity(existingOpportunity, form, userAccount);

    assertThat(existingOpportunity.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(existingOpportunity.getManualFunction()).isEqualTo(SearchSelectorService.removePrefix(selectedFunction));
    checkCommonFields(form, existingOpportunity);
  }

  @Test
  public void getForm() {
    var form = (ForwardWorkPlanCollaborationOpportunityForm) forwardWorkPlanCollaborationOpportunityService.getForm(opportunity);
    assertThat(form.getFunction()).isEqualTo(opportunity.getFunction().name());
    checkCommonFormFields(form, opportunity);
  }

  @Test
  public void getFormManualEntry() {
    var manualEntryOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(projectDetail);

    var form = (ForwardWorkPlanCollaborationOpportunityForm) forwardWorkPlanCollaborationOpportunityService.getForm(manualEntryOpportunity);

    assertThat(form.getFunction()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualEntryOpportunity.getManualFunction()));
    checkCommonFormFields(form, manualEntryOpportunity);
  }

  @Test
  public void validate_partial() {
    var form = new ForwardWorkPlanCollaborationOpportunityForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    forwardWorkPlanCollaborationOpportunityService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    forwardWorkPlanCollaborationOpportunityService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  private void checkCommonFields(ForwardWorkPlanCollaborationOpportunityForm form,
                                 ForwardWorkPlanCollaborationOpportunity collaborationOpportunity) {
    assertThat(collaborationOpportunity.getDescriptionOfWork()).isEqualTo(form.getDescriptionOfWork());
    assertThat(collaborationOpportunity.getUrgentResponseNeeded()).isEqualTo(form.getUrgentResponseNeeded());
    assertThat(collaborationOpportunity.getContactName()).isEqualTo(form.getContactDetail().getName());
    assertThat(collaborationOpportunity.getPhoneNumber()).isEqualTo(form.getContactDetail().getPhoneNumber());
    assertThat(collaborationOpportunity.getJobTitle()).isEqualTo(form.getContactDetail().getJobTitle());
    assertThat(collaborationOpportunity.getEmailAddress()).isEqualTo(form.getContactDetail().getEmailAddress());
  }

  private void checkCommonFormFields(ForwardWorkPlanCollaborationOpportunityForm form,
                                     ForwardWorkPlanCollaborationOpportunity collaborationOpportunity) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(collaborationOpportunity.getDescriptionOfWork());
    assertThat(form.getUrgentResponseNeeded()).isEqualTo(collaborationOpportunity.getUrgentResponseNeeded());
    assertThat(form.getContactDetail().getName()).isEqualTo(collaborationOpportunity.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(collaborationOpportunity.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(collaborationOpportunity.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(collaborationOpportunity.getEmailAddress());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry() {
    final var results = forwardWorkPlanCollaborationOpportunityService.findFunctionsLikeWithManualEntry(
        Function.FACILITIES_OFFSHORE.getDisplayName()
    );
    assertThat(results).extracting(RestSearchItem::getId).containsExactly(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_withManualEntry() {
    final var manualEntry = "manual entry";
    final var results = forwardWorkPlanCollaborationOpportunityService.findFunctionsLikeWithManualEntry(manualEntry);
    assertThat(results).extracting(RestSearchItem::getId).containsExactly(
        String.format("%s%s", SearchSelectablePrefix.FREE_TEXT_PREFIX, manualEntry)
    );
  }

  @Test
  public void copySectionData_verifyDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    final var collaborationOpportunities = List.of(
        ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail)
    );

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetailOrderByIdAsc(fromProjectDetail))
        .thenReturn(collaborationOpportunities);

    forwardWorkPlanCollaborationOpportunityService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        collaborationOpportunities,
        toProjectDetail,
        ForwardWorkPlanCollaborationOpportunity.class
    );

    verify(entityDuplicationService, times(1)).createDuplicatedEntityPairingMap(any());

    verify(forwardWorkPlanCollaborationOpportunityFileLinkService, times(1)).copyCollaborationOpportunityFileLinkData(
        eq(fromProjectDetail),
        eq(toProjectDetail),
        anyMap()
    );

  }

  @Test
  public void alwaysCopySectionData_verifyFalse() {
    assertThat(forwardWorkPlanCollaborationOpportunityService.alwaysCopySectionData(projectDetail)).isFalse();
  }

  @Test
  public void getOpportunitiesForProjectVersion_whenNoResults_thenEmptyList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var project = projectDetail.getProject();
    final var version = projectDetail.getVersion();

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    )).thenReturn(Collections.emptyList());

    final var opportunityList = forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForProjectVersion(
        project,
        version
    );

    assertThat(opportunityList).isEmpty();
  }

  @Test
  public void getOpportunitiesForProjectVersion_whenResults_thenPopulatedList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var project = projectDetail.getProject();
    final var version = projectDetail.getVersion();

    final var opportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(
        project,
        version
    )).thenReturn(List.of(opportunity));

    final var opportunityList = forwardWorkPlanCollaborationOpportunityService.getOpportunitiesForProjectVersion(
        project,
        version
    );

    assertThat(opportunityList).containsExactly(opportunity);
  }

  @Test
  public void delete_verifyInteractions() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var opportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    forwardWorkPlanCollaborationOpportunityService.delete(opportunity);

    verify(forwardWorkPlanCollaborationOpportunityFileLinkService, times(1)).removeCollaborationOpportunityFileLinks(opportunity);
    verify(forwardWorkPlanCollaborationOpportunityRepository, times(1)).delete(opportunity);
  }

  @Test
  public void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = forwardWorkPlanCollaborationOpportunityService.allowSectionDataCleanUp(projectDetail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  @Test
  public void removeSectionData_verifyInteractions() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var opportunity1 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);
    final var opportunity2 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(projectDetail);

    final var opportunitiesForDetail = List.of(opportunity1, opportunity2);

    when(forwardWorkPlanCollaborationOpportunityRepository.findAllByProjectDetailOrderByIdAsc(projectDetail))
        .thenReturn(opportunitiesForDetail);

    forwardWorkPlanCollaborationOpportunityService.removeSectionData(projectDetail);

    verify(forwardWorkPlanCollaborationOpportunityFileLinkService, times(1)).removeCollaborationOpportunityFileLinks(opportunitiesForDetail);
    verify(forwardWorkPlanCollaborationOpportunityRepository, times(1)).deleteAll(opportunitiesForDetail);

  }

}