package uk.co.ogauthority.pathfinder.service.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.config.file.UploadErrorType;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.file.ProjectDetailFileRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailFileServiceTest {

  private final String FILE_ID = "1234567890qwertyuiop";

  @Mock
  private ProjectDetailFileRepository projectDetailFileRepository;

  @Mock
  private FileUploadService fileUploadService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  @Captor
  private ArgumentCaptor<ProjectDetailFile> projectDetailFileArgumentCaptor;

  @Captor
  private ArgumentCaptor<Set<ProjectDetailFile>> projectDetailFileSetCaptor;

  private ProjectDetailFileService projectDetailFileService;

  private ProjectDetail projectDetail;

  private ProjectDetailFile file;

  private final WebUserAccount wua = new WebUserAccount(1);

  private final UploadedFileView fileView = new UploadedFileView(
      FILE_ID,
      "NAME",
      100L,
      "DESC",
      Instant.now(),
      "");

  @Before
  public void setUp() {

    projectDetailFileService = new ProjectDetailFileService(
        fileUploadService,
        projectDetailFileRepository,
        entityDuplicationService
    );

    projectDetail = ProjectUtil.getProjectDetails();
    file = new ProjectDetailFile();
    file.setFileId(FILE_ID);
    file.setPurpose(ProjectDetailFilePurpose.PLACEHOLDER);

    when(fileUploadService.deleteUploadedFile(any(), any())).thenAnswer(invocation ->
        FileDeleteResult.generateSuccessfulFileDeleteResult(invocation.getArgument(0))
    );

    when(projectDetailFileRepository.findAllByProjectDetailAndPurpose(projectDetail, ProjectDetailFilePurpose.PLACEHOLDER))
        .thenReturn(List.of(file));

    when(fileUploadService.createUploadFileWithDescriptionFormFromView(any())).thenCallRealMethod();

  }

  @Test
  public void mapFilesToForm() {

    var form = new TestFileUploadForm();

    when(projectDetailFileRepository.findAllAsFileViewByProjectDetailAndPurposeAndFileLinkStatus(
        projectDetail, ProjectDetailFilePurpose.PLACEHOLDER, FileLinkStatus.FULL)).thenReturn(List.of(fileView));

    projectDetailFileService.mapFilesToForm(form, projectDetail, ProjectDetailFilePurpose.PLACEHOLDER);

    assertThat(form.getUploadedFileWithDescriptionForms().size()).isEqualTo(1);

    var uploadForm = form.getUploadedFileWithDescriptionForms().get(0);

    assertThat(uploadForm.getUploadedFileId()).isEqualTo(fileView.getFileId());
    assertThat(uploadForm.getUploadedFileDescription()).isEqualTo(fileView.getFileDescription());
    assertThat(uploadForm.getUploadedFileInstant()).isEqualTo(fileView.getFileUploadedTime());

  }

  @Test
  public void processInitialUpload_success() {

    var multiPartFile = mock(MultipartFile.class);

    when(fileUploadService.processUpload(multiPartFile, wua)).thenReturn(
        FileUploadResult.generateSuccessfulFileUploadResult(
            file.getFileId(),
            fileView.getFileName(),
            0,
            "content"
        )
    );

    var fileUploadResult = projectDetailFileService.processInitialUpload(
        multiPartFile,
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        wua
    );

    assertThat(fileUploadResult.isValid()).isTrue();

    verify(fileUploadService, times(1)).processUpload(multiPartFile, wua);
    verify(projectDetailFileRepository, times(1)).save(projectDetailFileArgumentCaptor.capture());

    var newFile = projectDetailFileArgumentCaptor.getValue();

    assertThat(newFile.getProjectDetail()).isEqualTo(projectDetail);
    assertThat(newFile.getFileId()).isEqualTo(file.getFileId());
    assertThat(newFile.getDescription()).isNull();
    assertThat(newFile.getPurpose()).isEqualTo(ProjectDetailFilePurpose.PLACEHOLDER);
    assertThat(newFile.getFileLinkStatus()).isEqualTo(FileLinkStatus.TEMPORARY);

  }

  @Test
  public void processInitialUpload_failed() {

    var multiPartFile = mock(MultipartFile.class);

    var failedResult = FileUploadResult.generateFailedFileUploadResult(
        multiPartFile.getOriginalFilename(), multiPartFile, UploadErrorType.EXTENSION_NOT_ALLOWED);

    when(fileUploadService.processUpload(multiPartFile, wua)).thenReturn(failedResult);

    var fileUploadResult = projectDetailFileService.processInitialUpload(
        multiPartFile,
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        wua
    );

    assertThat(fileUploadResult.isValid()).isFalse();

    verify(fileUploadService, times(1)).processUpload(multiPartFile, wua);
    verifyNoInteractions(projectDetailFileRepository);

  }

  @Test
  public void updateFiles_whenFilesNotOnForm_thenFilesAreDeleted() {

    var form = new TestFileUploadForm();
    projectDetailFileService.updateFiles(
        form,
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        FileUpdateMode.DELETE_UNLINKED_FILES,
        wua
    );

    verify(fileUploadService, times(1)).deleteUploadedFile(FILE_ID, wua);
    verify(projectDetailFileRepository, times(1)).deleteAll(Set.of(file));

  }

  @Test
  public void updateFiles_whenFileOnFormThenUpdatedDescriptionSaved_andLinkIsFull() {

    var form = new TestFileUploadForm();
    var fileForm = new UploadFileWithDescriptionForm(FILE_ID, "New Description", Instant.now());
    form.setUploadedFileWithDescriptionForms(List.of(fileForm));

    projectDetailFileService.updateFiles(
        form,
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        FileUpdateMode.DELETE_UNLINKED_FILES,
        wua
    );

    verify(projectDetailFileRepository, times(1)).saveAll(projectDetailFileSetCaptor.capture());

    var savedFiles = projectDetailFileSetCaptor.getValue();

    assertThat(savedFiles)
        .hasSize(1)
        .allSatisfy(savedFile -> {
          assertThat(savedFile.getDescription()).isEqualTo("New Description");
          assertThat(savedFile.getFileLinkStatus()).isEqualTo(FileLinkStatus.FULL);
        });

    verify(projectDetailFileRepository, times(1)).deleteAll(Collections.emptySet());

  }

  @Test
  public void updateFiles_whenNoExistingFiles() {

    var form = new TestFileUploadForm();

    when(projectDetailFileRepository.findAllByProjectDetailAndPurpose(
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER
    ))
        .thenReturn(List.of());

    projectDetailFileService.updateFiles(
        form,
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        FileUpdateMode.DELETE_UNLINKED_FILES,
        wua
    );

    verifyNoInteractions(fileUploadService);
    verify(projectDetailFileRepository, times(1)).saveAll(eq(Set.of()));
    verify(projectDetailFileRepository, times(1)).deleteAll(eq(Set.of()));

  }

  @Test
  public void deleteFileLinksAndUploadedFiles_uploadedFileRemoveSuccessful() {
    projectDetailFileService.deleteProjectDetailFileLinksAndUploadedFiles(List.of(file), wua);
    verify(projectDetailFileRepository).deleteAll(eq(List.of(file)));
  }

  @Test(expected = RuntimeException.class)
  public void deleteFileLinksAndUploadedFiles_uploadedFileRemoveFail() {

    when(fileUploadService.deleteUploadedFile(any(), any())).thenAnswer(invocation ->
        FileDeleteResult.generateFailedFileDeleteResult(invocation.getArgument(0))
    );

    projectDetailFileService.deleteProjectDetailFileLinksAndUploadedFiles(List.of(file), wua);

  }

  @Test
  public void processFileDeletion_verifyServiceInteractions() {

    projectDetailFileService.processFileDeletion(file, wua);

    verify(projectDetailFileRepository, times(1)).delete(file);
    verify(fileUploadService, times(1)).deleteUploadedFile(file.getFileId(), wua);

  }

  @Test
  public void getFilesLinkedToForm() {

    var form = new TestFileUploadForm();
    var fileForm = new UploadFileWithDescriptionForm(FILE_ID, "New Description", Instant.now());
    form.setUploadedFileWithDescriptionForms(List.of(fileForm));

    when(projectDetailFileRepository.findAllAsFileViewByProjectDetailAndPurposeAndFileLinkStatus(
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        FileLinkStatus.ALL)
    ).thenReturn(List.of(fileView));

    var result = projectDetailFileService.getFileViewsLinkedToForm(
        form,
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER
    );

    assertThat(result.get(0).getFileDescription()).isEqualTo("New Description");

  }

  @Test
  public void cleanupFiles_filesToKeep() {

    var file4 = new ProjectDetailFile();
    file4.setPurpose(ProjectDetailFilePurpose.PLACEHOLDER);
    file4.setId(4);

    var file5 = new ProjectDetailFile();
    file5.setPurpose(ProjectDetailFilePurpose.PLACEHOLDER);
    file5.setId(5);

    when(projectDetailFileRepository.findAllByProjectDetailAndFilePurposeAndIdNotIn(
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        List.of(1, 2, 3)
    ))
        .thenReturn(List.of(file4, file5));

    projectDetailFileService.cleanupFiles(
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER,
        List.of(1, 2, 3)
    );

    verify(projectDetailFileRepository, times(1)).deleteAll(eq(List.of(file4, file5)));

  }

  @Test
  public void cleanupFiles_noFilesToKeep() {

    var file1 = new ProjectDetailFile();
    file1.setPurpose(ProjectDetailFilePurpose.PLACEHOLDER);
    file1.setId(1);

    var file2 = new ProjectDetailFile();
    file2.setPurpose(ProjectDetailFilePurpose.PLACEHOLDER);
    file2.setId(2);

    var file3 = new ProjectDetailFile();
    file3.setPurpose(ProjectDetailFilePurpose.PLACEHOLDER);
    file3.setId(3);

    when(projectDetailFileRepository.findAllByProjectDetailAndPurpose(
        projectDetail,
        ProjectDetailFilePurpose.PLACEHOLDER
    ))
        .thenReturn(List.of(file1, file2, file3));

    projectDetailFileService.cleanupFiles(projectDetail, ProjectDetailFilePurpose.PLACEHOLDER, List.of());

    verify(projectDetailFileRepository, times(1)).deleteAll(eq(List.of(file1, file2, file3)));

  }

  @Test
  public void removeProjectDetailFiles() {
    var file1 = new ProjectDetailFile();
    var file2 = new ProjectDetailFile();

    projectDetailFileService.removeProjectDetailFiles(List.of(file1, file2));
    
    verify(projectDetailFileRepository).deleteAll(List.of(file1, file2));
  }

  @Test
  public void copyProjectDetailFileData() {
    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
    final var projectDetailFilePurpose = ProjectDetailFilePurpose.PLACEHOLDER;

    final var projectDetailFiles = List.of(new ProjectDetailFile());

    when(projectDetailFileRepository.findAllByProjectDetailAndPurpose(fromProjectDetail, projectDetailFilePurpose))
        .thenReturn(projectDetailFiles);

    projectDetailFileService.copyProjectDetailFileData(
        fromProjectDetail,
        toProjectDetail,
        projectDetailFilePurpose
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        projectDetailFiles,
        toProjectDetail,
        ProjectDetailFile.class
    );

    verify(entityDuplicationService, times(1)).createDuplicatedEntityPairingMap(any());
  }

}