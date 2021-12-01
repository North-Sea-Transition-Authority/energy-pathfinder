package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.FunctionType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.fds.RestSearchItem;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityFormCommon;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.model.view.collaborationopportunity.TestCollaborationOpportunityCommon;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtilCommon;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunitiesServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  private TestCollaborationOpportunitiesService collaborationOpportunitiesService;

  @Before
  public void setup() {

    final var searchSelectorService = new SearchSelectorService();
    final var functionService = new FunctionService(searchSelectorService);

    collaborationOpportunitiesService = new TestCollaborationOpportunitiesService(
        searchSelectorService,
        functionService,
        projectSetupService,
        projectDetailFileService
    );
  }

  @Test
  public void populateCollaborationOpportunity_withFromListFunction_assertProperties() {

    final var selectedFunction = CollaborationOpportunityTestUtilCommon.FUNCTION;

    final var form = CollaborationOpportunityTestUtilCommon.populateCompleteForm(new TestCollaborationOpportunityForm());
    form.setFunction(selectedFunction.getSelectionId());

    final var entityToPopulate = new TestCollaborationOpportunityCommon();

    final var newCollaborationOpportunity = collaborationOpportunitiesService.populateCollaborationOpportunity(
        form,
        entityToPopulate
    );

    assertThat(newCollaborationOpportunity.getFunction()).isEqualTo(selectedFunction);
    checkCommonEntityFields(form, newCollaborationOpportunity);
  }

  @Test
  public void populateCollaborationOpportunity_withNotFromListFunction_assertProperties() {

    final var rawFunctionString = "Manual function";
    final var selectedFunction = String.format("%s%s", SearchSelectablePrefix.FREE_TEXT_PREFIX, rawFunctionString);

    final var form = CollaborationOpportunityTestUtilCommon.populateCompleteForm(new TestCollaborationOpportunityForm());
    form.setFunction(selectedFunction);

    final var entityToPopulate = new TestCollaborationOpportunityCommon();

    final var newCollaborationOpportunity = collaborationOpportunitiesService.populateCollaborationOpportunity(
        form,
        entityToPopulate
    );

    assertThat(newCollaborationOpportunity.getManualFunction()).isEqualTo(rawFunctionString);
    checkCommonEntityFields(form, newCollaborationOpportunity);
  }

  private void checkCommonEntityFields(
      CollaborationOpportunityFormCommon form,
      CollaborationOpportunityCommon entity
  ) {
    assertThat(entity.getDescriptionOfWork()).isEqualTo(form.getDescriptionOfWork());
    assertThat(entity.getUrgentResponseNeeded()).isEqualTo(form.getUrgentResponseNeeded());
    assertThat(entity.getContactName()).isEqualTo(form.getContactDetail().getName());
    assertThat(entity.getPhoneNumber()).isEqualTo(form.getContactDetail().getPhoneNumber());
    assertThat(entity.getJobTitle()).isEqualTo(form.getContactDetail().getJobTitle());
    assertThat(entity.getEmailAddress()).isEqualTo(form.getContactDetail().getEmailAddress());
  }

  @Test
  public void canShowInTaskList_whenTrue_thenTrue() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.COLLABORATION_OPPORTUNITIES)).thenReturn(true);

    final var canShowInTaskList = collaborationOpportunitiesService.canShowInTaskList(
        projectDetail,
        ProjectTask.COLLABORATION_OPPORTUNITIES
    );

    assertThat(canShowInTaskList).isTrue();
  }

  @Test
  public void canShowInTaskList_whenFalse_thenFalse() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.COLLABORATION_OPPORTUNITIES)).thenReturn(false);

    final var canShowInTaskList = collaborationOpportunitiesService.canShowInTaskList(
        projectDetail,
        ProjectTask.COLLABORATION_OPPORTUNITIES
    );

    assertThat(canShowInTaskList).isFalse();
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_whenFromList() {
    final var results = collaborationOpportunitiesService.findFunctionsLikeWithManualEntry(
        Function.FACILITIES_OFFSHORE.getDisplayName(),
        FunctionType.COLLABORATION_OPPORTUNITY
    );
    assertThat(results).extracting(RestSearchItem::getId).containsExactly(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_withManualEntry() {

    final var manualEntry = "manual entry";

    final var results = collaborationOpportunitiesService.findFunctionsLikeWithManualEntry(
        manualEntry,
        FunctionType.COLLABORATION_OPPORTUNITY
    );

    assertThat(results.size()).isEqualTo(1);
    assertThat(results).extracting(RestSearchItem::getId).containsExactly(
        String.format("%s%s", SearchSelectablePrefix.FREE_TEXT_PREFIX, manualEntry)
    );
  }

  @Test
  public void getPreSelectedCollaborationFunction_whenFunctionIsNull_themEmptyMap() {
    final var form = CollaborationOpportunityTestUtilCommon.populateCompleteForm(new TestCollaborationOpportunityForm());
    form.setFunction(null);

    final var preSelectedValues = collaborationOpportunitiesService.getPreSelectedCollaborationFunction(form, Function.values());
    assertThat(preSelectedValues).isEmpty();
  }

  @Test
  public void getPreSelectedCollaborationFunction_whenFunctionFromList_themReturnMapWithEntryFromList() {

    final var selectedFunction = CollaborationOpportunityTestUtilCommon.FUNCTION;

    final var form = CollaborationOpportunityTestUtilCommon.populateCompleteForm(new TestCollaborationOpportunityForm());
    form.setFunction(selectedFunction.getSelectionId());

    final var preSelectedValues = collaborationOpportunitiesService.getPreSelectedCollaborationFunction(form, Function.values());
    assertThat(preSelectedValues).containsExactly(
        entry(selectedFunction.getSelectionId(), selectedFunction.getSelectionText())
    );
  }

  @Test
  public void getPreSelectedCollaborationFunction_whenFunctionNotFromList_themReturnMapWithManualEntry() {

    final var selectedFunction = CollaborationOpportunityTestUtilCommon.MANUAL_FUNCTION;

    final var form = CollaborationOpportunityTestUtilCommon.populateCompleteForm(new TestCollaborationOpportunityForm());
    form.setFunction(selectedFunction);

    final var preSelectedValues = collaborationOpportunitiesService.getPreSelectedCollaborationFunction(form, Function.values());
    assertThat(preSelectedValues).containsExactly(
        entry(selectedFunction, SearchSelectorService.removePrefix(selectedFunction))
    );
  }

  @Test
  public void populateCollaborationOpportunityForm_whenFunctionFromList() {

    final var selectedFunction = Function.DRILLING;

    final var entity = CollaborationOpportunityTestUtilCommon.populateCollaborationOpportunity(new TestCollaborationOpportunityCommon());
    entity.setFunction(selectedFunction);
    entity.setManualFunction(null);

    final var form = new TestCollaborationOpportunityForm();

    collaborationOpportunitiesService.populateCollaborationOpportunityForm(
        entity,
        form,
        List.of(),
        ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY
    );

    assertThat(form.getFunction()).isEqualTo(selectedFunction.getSelectionId());
    checkCommonFormFields(entity, form);
  }

  @Test
  public void populateCollaborationOpportunityForm_whenFunctionNotFromList() {

    final var rawFunction = "manual";
    final var selectedFunction = String.format("%s%s", SearchSelectablePrefix.FREE_TEXT_PREFIX, rawFunction);

    final var entity = CollaborationOpportunityTestUtilCommon.populateCollaborationOpportunity(new TestCollaborationOpportunityCommon());
    entity.setFunction(null);
    entity.setManualFunction(rawFunction);

    final var form = new TestCollaborationOpportunityForm();

    collaborationOpportunitiesService.populateCollaborationOpportunityForm(
        entity,
        form,
        List.of(),
        ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY
    );

    assertThat(form.getFunction()).isEqualTo(selectedFunction);
    checkCommonFormFields(entity, form);
  }

  @Test
  public void populateCollaborationOpportunityForm_whenUploadedFiles() {

    final var entity = CollaborationOpportunityTestUtilCommon.populateCollaborationOpportunity(new TestCollaborationOpportunityCommon());

    final var form = new TestCollaborationOpportunityForm();

    final var uploadedFile = CollaborationOpportunityTestUtilCommon.populateCollaborationOpportunityFileLink(
        new TestCollaborationOpportunityFileLinkCommon()
    );

    final var uploadedFileView = new UploadedFileView(
        uploadedFile.getProjectDetailFile().getFileId(),
        "fileName",
        1L,
        uploadedFile.getProjectDetailFile().getDescription(),
        Instant.now(),
        "url"
    );

    when(projectDetailFileService.getUploadedFileView(
        any(),
        any(),
        any(),
        any()
    )).thenReturn(uploadedFileView);

    collaborationOpportunitiesService.populateCollaborationOpportunityForm(
        entity,
        form,
        List.of(uploadedFile),
        ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY
    );

    assertThat(form.getUploadedFileWithDescriptionForms()).containsExactly(
        new UploadFileWithDescriptionForm(
            uploadedFileView.getFileId(),
            uploadedFileView.getFileDescription(),
            uploadedFileView.getFileUploadedTime()
        )
    );

    checkCommonFormFields(entity, form);
  }

  private void checkCommonFormFields(
      CollaborationOpportunityCommon entity,
      CollaborationOpportunityFormCommon form
  ) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(entity.getDescriptionOfWork());
    assertThat(form.getUrgentResponseNeeded()).isEqualTo(entity.getUrgentResponseNeeded());
    assertThat(form.getContactDetail().getName()).isEqualTo(entity.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(entity.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(entity.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(entity.getEmailAddress());
  }

}