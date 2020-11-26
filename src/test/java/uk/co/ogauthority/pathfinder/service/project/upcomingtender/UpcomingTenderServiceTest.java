package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderServiceTest {

  @Mock
  private UpcomingTenderRepository upcomingTenderRepository;

  @Mock
  private ValidationService validationService;

  @Mock
  private UpcomingTenderFormValidator upcomingTenderFormValidator;

  @Mock
  private UpcomingTenderFileLinkService upcomingTenderFileLinkService;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private ProjectSetupService projectSetupService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private UpcomingTenderService upcomingTenderService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final UpcomingTender upcomingTender = UpcomingTenderUtil.getUpcomingTender(detail);

  private final AuthenticatedUserAccount authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() {

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    FunctionService functionService = new FunctionService(searchSelectorService);

    upcomingTenderService = new UpcomingTenderService(
        upcomingTenderRepository,
        validationService,
        upcomingTenderFormValidator,
        functionService,
        searchSelectorService,
        projectDetailFileService,
        upcomingTenderFileLinkService,
        projectSetupService,
        entityDuplicationService
    );

    when(upcomingTenderRepository.save(any(UpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
  }


  @Test
  public void createUpcomingTender() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var newUpcomingTender = upcomingTenderService.createUpcomingTender(
        detail,
        form,
        authenticatedUserAccount
    );
    assertThat(newUpcomingTender.getProjectDetail()).isEqualTo(detail);
    assertThat(newUpcomingTender.getTenderFunction()).isEqualTo(UpcomingTenderUtil.TENDER_FUNCTION);
    checkCommonFields(form, newUpcomingTender);
  }

  @Test
  public void createUpcomingTender_manualFunction() {
    var form = UpcomingTenderUtil.getCompletedForm_manualEntry();
    var newUpcomingTender = upcomingTenderService.createUpcomingTender(
        detail,
        form,
        authenticatedUserAccount
    );
    assertThat(newUpcomingTender.getProjectDetail()).isEqualTo(detail);
    checkCommonFields(form, newUpcomingTender);
  }

  @Test
  public void updateUpcomingTender() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setTenderFunction(Function.DRILLING.name());
    var existingUpcomingTender = upcomingTender;
    upcomingTenderService.updateUpcomingTender(
        existingUpcomingTender,
        form,
        authenticatedUserAccount
    );
    assertThat(existingUpcomingTender.getProjectDetail()).isEqualTo(detail);
    assertThat(existingUpcomingTender.getTenderFunction()).isEqualTo(Function.DRILLING);
    checkCommonFields(form, existingUpcomingTender);
  }

  @Test
  public void updateUpcomingTender_manualFunction() {
    var form = UpcomingTenderUtil.getCompleteForm();
    form.setTenderFunction(null);
    form.setTenderFunction(UpcomingTenderUtil.MANUAL_TENDER_FUNCTION);
    var existingUpcomingTender = upcomingTender;
    upcomingTenderService.updateUpcomingTender(existingUpcomingTender, form, authenticatedUserAccount);
    assertThat(existingUpcomingTender.getProjectDetail()).isEqualTo(detail);
    assertThat(existingUpcomingTender.getManualTenderFunction()).isEqualTo(SearchSelectorService.removePrefix(UpcomingTenderUtil.MANUAL_TENDER_FUNCTION));
    checkCommonFields(form, existingUpcomingTender);
  }

  @Test
  public void getForm() {
    var form = upcomingTenderService.getForm(upcomingTender);
    assertThat(form.getTenderFunction()).isEqualTo(upcomingTender.getTenderFunction().name());
    checkCommonFormFields(form, upcomingTender);
  }

  @Test
  public void getForm_manualEntry() {
    var manualEntryTender = UpcomingTenderUtil.getUpcomingTender_manualEntry(detail);
    var form = upcomingTenderService.getForm(manualEntryTender);
    assertThat(form.getTenderFunction()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualEntryTender.getManualTenderFunction()));
    checkCommonFormFields(form, upcomingTender);
  }

  @Test
  public void getForm_withFile() {

    var uploadedFileView = UploadedFileUtil.createUploadedFileView();
    var upcomingTenderLink = new UpcomingTenderFileLink();
    upcomingTenderLink.setProjectDetailFile(new ProjectDetailFile());
    upcomingTenderLink.setUpcomingTender(upcomingTender);

    when(upcomingTenderFileLinkService.getAllByUpcomingTender(upcomingTender)).thenReturn(List.of(upcomingTenderLink));
    when(projectDetailFileService.getUploadedFileView(any(), any(), any(), any())).thenReturn(uploadedFileView);

    var form = upcomingTenderService.getForm(upcomingTender);
    checkCommonFormFields(form, upcomingTender);
    assertThat(form.getUploadedFileWithDescriptionForms()).hasSize(1);

    var fileForm = form.getUploadedFileWithDescriptionForms().get(0);
    assertThat(fileForm.getUploadedFileId()).isEqualTo(uploadedFileView.getFileId());

  }

  @Test
  public void getForm_withoutFile() {

    when(upcomingTenderFileLinkService.getAllByUpcomingTender(upcomingTender)).thenReturn(List.of());

    var form = upcomingTenderService.getForm(upcomingTender);
    checkCommonFormFields(form, upcomingTender);
    assertThat(form.getUploadedFileWithDescriptionForms()).isEmpty();

  }

  @Test
  public void validate_partial() {
    var form = new UpcomingTenderForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    upcomingTenderService.validate(
        form,
        bindingResult,
        ValidationType.PARTIAL
    );
    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.PARTIAL);
  }

  @Test
  public void validate_full() {
    var form = UpcomingTenderUtil.getCompleteForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    upcomingTenderService.validate(
        form,
        bindingResult,
        ValidationType.FULL
    );

    verify(validationService, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  private void checkCommonFields(UpcomingTenderForm form, UpcomingTender newUpcomingTender) {
    assertThat(newUpcomingTender.getDescriptionOfWork()).isEqualTo(UpcomingTenderUtil.DESCRIPTION_OF_WORK);
    assertThat(newUpcomingTender.getEstimatedTenderDate()).isEqualTo(form.getEstimatedTenderDate().createDateOrNull());
    assertThat(newUpcomingTender.getContractBand()).isEqualTo(UpcomingTenderUtil.CONTRACT_BAND);
    assertThat(newUpcomingTender.getContactName()).isEqualTo(UpcomingTenderUtil.CONTACT_NAME);
    assertThat(newUpcomingTender.getPhoneNumber()).isEqualTo(UpcomingTenderUtil.PHONE_NUMBER);
    assertThat(newUpcomingTender.getJobTitle()).isEqualTo(UpcomingTenderUtil.JOB_TITLE);
    assertThat(newUpcomingTender.getEmailAddress()).isEqualTo(UpcomingTenderUtil.EMAIL);
  }

  private void checkCommonFormFields(UpcomingTenderForm form, UpcomingTender upcomingTender) {
    assertThat(form.getDescriptionOfWork()).isEqualTo(upcomingTender.getDescriptionOfWork());
    assertThat(form.getEstimatedTenderDate().createDateOrNull()).isEqualTo(upcomingTender.getEstimatedTenderDate());
    assertThat(form.getContractBand()).isEqualTo(upcomingTender.getContractBand());
    assertThat(form.getContactDetail().getName()).isEqualTo(upcomingTender.getContactName());
    assertThat(form.getContactDetail().getPhoneNumber()).isEqualTo(upcomingTender.getPhoneNumber());
    assertThat(form.getContactDetail().getJobTitle()).isEqualTo(upcomingTender.getJobTitle());
    assertThat(form.getContactDetail().getEmailAddress()).isEqualTo(upcomingTender.getEmailAddress());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry() {
    var results = upcomingTenderService.findTenderFunctionsLikeWithManualEntry(Function.FACILITIES_OFFSHORE.getDisplayName());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  public void findTenderFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = upcomingTenderService.findTenderFunctionsLikeWithManualEntry(manualEntry);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }

  @Test
  public void deleteUpcomingTenderFile_whenTemporaryFileStatus_dontRemoveFileLink() {

    var fileId = "fileId";
    var projectDetail = ProjectUtil.getProjectDetails();
    var authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    var projectDetailFile = new ProjectDetailFile();
    projectDetailFile.setFileLinkStatus(FileLinkStatus.TEMPORARY);

    when(projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(any(), any())).thenReturn(projectDetailFile);

    upcomingTenderService.deleteUpcomingTenderFile(fileId, projectDetail, authenticatedUserAccount);

    verify(upcomingTenderFileLinkService, times(0)).removeUpcomingTenderFileLink(projectDetailFile);
    verify(projectDetailFileService, times(1)).processFileDeletion(projectDetailFile, authenticatedUserAccount);

  }

  @Test
  public void deleteUpcomingTenderFile_whenFullFileStatus_thenRemoveFileLink() {

    var fileId = "fileId";
    var projectDetail = ProjectUtil.getProjectDetails();
    var authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    var projectDetailFile = new ProjectDetailFile();
    projectDetailFile.setFileLinkStatus(FileLinkStatus.FULL);

    when(projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(any(), any())).thenReturn(projectDetailFile);

    upcomingTenderService.deleteUpcomingTenderFile(fileId, projectDetail, authenticatedUserAccount);

    verify(upcomingTenderFileLinkService, times(1)).removeUpcomingTenderFileLink(projectDetailFile);
    verify(projectDetailFileService, times(1)).processFileDeletion(projectDetailFile, authenticatedUserAccount);

  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(true);
    assertThat(upcomingTenderService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(false);
    assertThat(upcomingTenderService.canShowInTaskList(detail)).isFalse();
  }

  @Test
  public void removeSectionData_verifyInteractions() {

    final var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(detail);
    final var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(detail);
    final var upcomingTenders = List.of(upcomingTender1, upcomingTender2);

    when(upcomingTenderRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(upcomingTenders);

    upcomingTenderService.removeSectionData(detail);

    verify(upcomingTenderFileLinkService, times(1)).removeUpcomingTenderFileLinks(upcomingTenders);
    verify(upcomingTenderRepository, times(1)).deleteAll(upcomingTenders);
  }
}
