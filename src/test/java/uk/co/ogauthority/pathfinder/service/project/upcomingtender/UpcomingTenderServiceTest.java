package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.organisation.PortalOrganisationGroup;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.enums.project.Function;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderFormValidator;
import uk.co.ogauthority.pathfinder.model.searchselector.SearchSelectablePrefix;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.service.project.FunctionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectSectionItemOwnershipService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.UserToProjectRelationship;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.service.scheduler.reminders.quarterlyupdate.RemindableProject;
import uk.co.ogauthority.pathfinder.service.searchselector.SearchSelectorService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;
import uk.co.ogauthority.pathfinder.service.validation.ValidationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectFormSectionServiceTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.TeamTestingUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@ExtendWith(MockitoExtension.class)
class UpcomingTenderServiceTest {

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
  private TeamService teamService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Mock
  private ProjectSectionItemOwnershipService projectSectionItemOwnershipService;

  private UpcomingTenderService upcomingTenderService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  private final UpcomingTender upcomingTender = UpcomingTenderUtil.getUpcomingTender(detail);

  private final AuthenticatedUserAccount authenticatedUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

  private final PortalOrganisationGroup portalOrganisationGroup = TeamTestingUtil.generateOrganisationGroup(1, "org", "org");

  @BeforeEach
  void setUp() {
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
        entityDuplicationService,
        teamService,
        projectSectionItemOwnershipService
        );
  }


  @Test
  void createUpcomingTender() {
    when(teamService.getContributorPortalOrganisationGroup(authenticatedUserAccount)).thenReturn(portalOrganisationGroup);
    when(upcomingTenderRepository.save(any(UpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
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
  void createUpcomingTender_manualFunction() {
    when(teamService.getContributorPortalOrganisationGroup(authenticatedUserAccount)).thenReturn(portalOrganisationGroup);
    when(upcomingTenderRepository.save(any(UpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);
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
  void updateUpcomingTender() {
    when(upcomingTenderRepository.save(any(UpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

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
  void updateUpcomingTender_manualFunction() {
    when(upcomingTenderRepository.save(any(UpcomingTender.class)))
        .thenAnswer(invocation -> invocation.getArguments()[0]);

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
  void getForm() {
    var form = upcomingTenderService.getForm(upcomingTender);
    assertThat(form.getTenderFunction()).isEqualTo(upcomingTender.getTenderFunction().name());
    checkCommonFormFields(form, upcomingTender);
  }

  @Test
  void getForm_manualEntry() {
    var manualEntryTender = UpcomingTenderUtil.getUpcomingTender_manualEntry(detail);
    var form = upcomingTenderService.getForm(manualEntryTender);
    assertThat(form.getTenderFunction()).isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(manualEntryTender.getManualTenderFunction()));
    checkCommonFormFields(form, upcomingTender);
  }

  @Test
  void getForm_withFile() {
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
  void getForm_withoutFile() {
    when(upcomingTenderFileLinkService.getAllByUpcomingTender(upcomingTender)).thenReturn(List.of());

    var form = upcomingTenderService.getForm(upcomingTender);
    checkCommonFormFields(form, upcomingTender);
    assertThat(form.getUploadedFileWithDescriptionForms()).isEmpty();

  }

  @Test
  void validate_partial() {
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
  void validate_full() {
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
    assertThat(newUpcomingTender.getAddedByOrganisationGroup()).isEqualTo(portalOrganisationGroup.getOrgGrpId());
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
  void findTenderFunctionsLikeWithManualEntry() {
    var results = upcomingTenderService.findTenderFunctionsLikeWithManualEntry(Function.FACILITIES_OFFSHORE.getDisplayName());
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(Function.FACILITIES_OFFSHORE.name());
  }

  @Test
  void findTenderFunctionsLikeWithManualEntry_withManualEntry() {
    var manualEntry = "manual entry";
    var results = upcomingTenderService.findTenderFunctionsLikeWithManualEntry(manualEntry);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).getId()).isEqualTo(SearchSelectablePrefix.FREE_TEXT_PREFIX+manualEntry);
  }

  @Test
  void deleteUpcomingTenderFile_whenTemporaryFileStatus_dontRemoveFileLink() {
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
  void deleteUpcomingTenderFile_whenFullFileStatus_thenRemoveFileLink() {
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
  void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(true);
    assertThat(upcomingTenderService.canShowInTaskList(detail, Set.of(UserToProjectRelationship.OPERATOR))).isTrue();
  }

  @Test
  void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(false);
    assertThat(upcomingTenderService.canShowInTaskList(detail, Set.of(UserToProjectRelationship.OPERATOR))).isFalse();
  }

  @Test
  void canShowInTaskList_userToProjectRelationshipSmokeTest() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(true);
    ProjectFormSectionServiceTestUtil.canShowInTaskList_userToProjectRelationshipSmokeTest(
        upcomingTenderService,
        detail,
        Set.of(UserToProjectRelationship.OPERATOR, UserToProjectRelationship.CONTRIBUTOR)
    );
  }

  @Test
  void isTaskValidForProjectDetail_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(true);
    assertThat(upcomingTenderService.isTaskValidForProjectDetail(detail)).isTrue();
  }

  @Test
  void isTaskValidForProjectDetail_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.UPCOMING_TENDERS)).thenReturn(false);
    assertThat(upcomingTenderService.isTaskValidForProjectDetail(detail)).isFalse();
  }

  @Test
  void removeSectionData_verifyInteractions() {
    final var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(detail);
    final var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(detail);
    final var upcomingTenders = List.of(upcomingTender1, upcomingTender2);

    when(upcomingTenderRepository.findByProjectDetailOrderByIdAsc(detail)).thenReturn(upcomingTenders);

    upcomingTenderService.removeSectionData(detail);

    verify(upcomingTenderFileLinkService, times(1)).removeUpcomingTenderFileLinks(upcomingTenders);
    verify(upcomingTenderRepository, times(1)).deleteAll(upcomingTenders);
  }

  @Test
  void copySectionData_verifyDuplicationServiceInteraction() {
    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    final var upcomingTenders = List.of(
        UpcomingTenderUtil.getUpcomingTender(fromProjectDetail)
    );

    when(upcomingTenderRepository.findByProjectDetailOrderByIdAsc(fromProjectDetail)).thenReturn(upcomingTenders);

    upcomingTenderService.copySectionData(fromProjectDetail, toProjectDetail);

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        upcomingTenders,
        toProjectDetail,
        UpcomingTender.class
    );

    verify(entityDuplicationService, times(1)).createDuplicatedEntityPairingMap(any());

    verify(upcomingTenderFileLinkService, times(1)).copyUpcomingTenderFileLinkData(
        eq(fromProjectDetail),
        eq(toProjectDetail),
        anyMap()
    );
  }

  @Test
  void getUpcomingTendersForProjectVersion_whenFound_thenReturnPopulatedList() {
    final var upcomingTender = UpcomingTenderUtil.getUpcomingTender(detail);
    final var upcomingTenderList = List.of(upcomingTender);

    final var project = detail.getProject();
    final var version = detail.getVersion();

    when(upcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(upcomingTenderList);

    var result = upcomingTenderService.getUpcomingTendersForProjectVersion(project, version);
    assertThat(result).containsExactly(upcomingTenderList.get(0));
  }

  @Test
  void getUpcomingTendersForProjectVersion_whenNotFound_thenReturnEmptyList() {
    final var project = detail.getProject();
    final var version = detail.getVersion();

    when(upcomingTenderRepository.findByProjectDetail_ProjectAndProjectDetail_VersionOrderByIdAsc(project, version))
        .thenReturn(Collections.emptyList());

    var result = upcomingTenderService.getUpcomingTendersForProjectVersion(project, version);
    assertThat(result).isEmpty();
  }

  @Test
  void getSupportedProjectTypes_verifyInfrastructure() {
    assertThat(upcomingTenderService.getSupportedProjectTypes()).containsExactly(ProjectType.INFRASTRUCTURE);
  }

  @Test
  void alwaysCopySectionData_verifyFalse() {
    assertThat(upcomingTenderService.alwaysCopySectionData(detail)).isFalse();
  }

  @Test
  void allowSectionDataCleanUp_verifyIsTrue() {
    final var allowSectionDateCleanUp = upcomingTenderService.allowSectionDataCleanUp(detail);
    assertThat(allowSectionDateCleanUp).isTrue();
  }

  @Test
  void canCurrentUserAccessTender_whenCurrentUserHasAccessToProjectSectionInfo_thenTrue() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(detail), any())).thenReturn(true);

    assertThat(upcomingTenderService.canCurrentUserAccessTender(upcomingTender)).isTrue();
  }

  @Test
  void canCurrentUserAccessTender_whenCurrentUserHasNoAccessToProjectSectionInfo_thenTrue() {
    when(projectSectionItemOwnershipService.canCurrentUserAccessProjectSectionInfo(eq(detail), any())).thenReturn(false);

    assertThat(upcomingTenderService.canCurrentUserAccessTender(upcomingTender)).isFalse();
  }

  @Test
  void getPastUpcomingTendersForRemindableProjects_whenFound_thenReturn() {
    var currentDate = LocalDate.now();

    var remindableProjectWithPastUpcomingTender = mock(RemindableProject.class);
    when(remindableProjectWithPastUpcomingTender.getProjectDetailId()).thenReturn(1);
    var upcomingTenderInPast = mock(UpcomingTender.class);
    when(upcomingTenderInPast.getEstimatedTenderDate()).thenReturn(currentDate.minusDays(1));

    var remindableProjectWithNoPastUpcomingTenders = mock(RemindableProject.class);
    when(remindableProjectWithNoPastUpcomingTenders.getProjectDetailId()).thenReturn(2);
    var upcomingTenderInFuture = mock(UpcomingTender.class);
    when(upcomingTenderInFuture.getEstimatedTenderDate()).thenReturn(currentDate.plusDays(5));

    var remindableProjectWithNoUpcomingTenders = mock(RemindableProject.class);
    when(remindableProjectWithNoUpcomingTenders.getProjectDetailId()).thenReturn(3);

    when(upcomingTenderRepository.findAllByProjectDetail_IdIn(List.of(1,2,3))).thenReturn(List.of(upcomingTenderInPast, upcomingTenderInFuture));

    var result = upcomingTenderService.getPastUpcomingTendersForRemindableProjects(
        List.of(remindableProjectWithPastUpcomingTender, remindableProjectWithNoPastUpcomingTenders, remindableProjectWithNoUpcomingTenders)
    );

    assertThat(result).containsOnly(upcomingTenderInPast);
  }

  @Test
  void getPastUpcomingTendersForRemindableProjects_whenNoneFound_thenReturnEmptyList() {
    var projectDetailIdList = List.of(1);
    when(upcomingTenderRepository.findAllByProjectDetail_IdIn(projectDetailIdList)).thenReturn(Collections.emptyList());

    var remindableProjectWithNoUpcomingTenders = mock(RemindableProject.class);
    when(remindableProjectWithNoUpcomingTenders.getProjectDetailId()).thenReturn(1);

    var result = upcomingTenderService.getPastUpcomingTendersForRemindableProjects(List.of(remindableProjectWithNoUpcomingTenders));

    assertThat(result).isEmpty();
  }
}
