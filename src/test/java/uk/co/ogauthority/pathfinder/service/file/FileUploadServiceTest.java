package uk.co.ogauthority.pathfinder.service.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.co.ogauthority.pathfinder.config.file.FileUploadProperties;
import uk.co.ogauthority.pathfinder.config.file.UploadErrorType;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileUploadStatus;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.file.UploadedFileRepository;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadServiceTest {

  public static final List<String> ALLOWED_TEST_EXTENSIONS = List.of("txt", "xls", "doc");
  public static final String TEST_FILENAME_FILTER = "[/\\?%*:|\"<>]";
  public static final Long MAX_TEST_FILE_SIZE = 100L;
  public static final Long INVALID_TEST_FILE_SIZE = MAX_TEST_FILE_SIZE + 1L;

  @Mock
  private UploadedFileRepository uploadedFileRepository;

  @Mock
  private VirusScanService virusScanService;

  private FileUploadService fileUploadService;

  private MultipartFile multiPartFile;

  @Before
  public void setup() throws IOException {

    FileUploadProperties fileUploadProperties = new FileUploadProperties();
    fileUploadProperties.setMaxFileSize(MAX_TEST_FILE_SIZE);
    fileUploadProperties.setAllowedExtensions(ALLOWED_TEST_EXTENSIONS);
    fileUploadProperties.setFileNameFilter(TEST_FILENAME_FILTER);

    fileUploadService = new FileUploadService(fileUploadProperties, uploadedFileRepository, virusScanService);

    multiPartFile = mock(MultipartFile.class);
    when(multiPartFile.getOriginalFilename()).thenReturn("test.txt");
    when(multiPartFile.getBytes()).thenReturn(new byte[1]);
  }

  @Test
  public void createUploadFileWithDescriptionFormFromView() {
    UploadedFileView uploadedFileView = UploadedFileUtil.createUploadedFileView();
    var form = fileUploadService.createUploadFileWithDescriptionFormFromView(uploadedFileView);

    assertThat(form.getUploadedFileId()).isEqualTo(uploadedFileView.getFileId());
    assertThat(form.getUploadedFileDescription()).isEqualTo(uploadedFileView.getFileDescription());
    assertThat(form.getUploadedFileInstant()).isEqualTo(uploadedFileView.getFileUploadedTime());
  }

  @Test
  public void getFileById_whenExists_thenReturn() {

    var uploadedFile = UploadedFileUtil.createUploadedFile();

    when(uploadedFileRepository.findById(uploadedFile.getFileId())).thenReturn(Optional.of(uploadedFile));

    var resultingUploadedFile = fileUploadService.getFileById(uploadedFile.getFileId());

    assertThat(resultingUploadedFile.getFileId()).isEqualTo(uploadedFile.getFileId());
    assertThat(resultingUploadedFile.getFileName()).isEqualTo(uploadedFile.getFileName());
    assertThat(resultingUploadedFile.getFileData()).isEqualTo(uploadedFile.getFileData());
    assertThat(resultingUploadedFile.getContentType()).isEqualTo(uploadedFile.getContentType());
    assertThat(resultingUploadedFile.getFileSize()).isEqualTo(uploadedFile.getFileSize());
    assertThat(resultingUploadedFile.getUploadDatetime()).isEqualTo(uploadedFile.getUploadDatetime());
    assertThat(resultingUploadedFile.getUploadedByWuaId()).isEqualTo(uploadedFile.getUploadedByWuaId());
    assertThat(resultingUploadedFile.getLastUpdatedByWuaId()).isEqualTo(uploadedFile.getLastUpdatedByWuaId());
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void getFileById_whenNotExists_thenException() {

    var uploadedFile = UploadedFileUtil.createUploadedFile();
    when(uploadedFileRepository.findById(uploadedFile.getFileId())).thenReturn(Optional.empty());

    fileUploadService.getFileById(uploadedFile.getFileId());
  }

  @Test
  public void processUpload_whenExtensionNotAllowed_thenUploadError() {

    final String invalidFileExtension = "bat";
    when(multiPartFile.getOriginalFilename()).thenReturn("test." + invalidFileExtension);

    var fileUploadResult = fileUploadService.processUpload(
        multiPartFile,
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileUploadResult.getErrorType()).isEqualTo(Optional.of(UploadErrorType.EXTENSION_NOT_ALLOWED));
    verify(uploadedFileRepository, times(0)).save(any());
  }

  @Test
  public void processUpload_whenFileSizeTooBig_thenUploadError() {

    when(multiPartFile.getSize()).thenReturn(INVALID_TEST_FILE_SIZE);

    var fileUploadResult = fileUploadService.processUpload(
        multiPartFile,
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileUploadResult.getErrorType()).isEqualTo(Optional.of(UploadErrorType.MAX_FILE_SIZE_EXCEEDED));
    verify(uploadedFileRepository, times(0)).save(any());
  }

  @Test
  public void processUpload_whenFileHasVirus_thenUploadError() {

    when(virusScanService.hasVirus(multiPartFile)).thenReturn(true);

    var fileUploadResult = fileUploadService.processUpload(
        multiPartFile,
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileUploadResult.getErrorType()).isEqualTo(Optional.of(UploadErrorType.VIRUS_FOUND_IN_FILE));
    verify(uploadedFileRepository, times(0)).save(any());
  }

  @Test
  public void processUpload_whenInternalError_thenUploadError() throws IOException {

    when(multiPartFile.getBytes()).thenReturn(null);

    var fileUploadResult = fileUploadService.processUpload(
        multiPartFile,
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileUploadResult.getErrorType()).isEqualTo(Optional.of(UploadErrorType.INTERNAL_SERVER_ERROR));
    verify(uploadedFileRepository, times(0)).save(any());
  }

  @Test
  public void processUpload_whenValidFile_thenNoUploadError() {

    var fileUploadResult = fileUploadService.processUpload(
        multiPartFile,
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileUploadResult.getErrorType()).isEmpty();

    verify(uploadedFileRepository, times(1)).save(any());
  }

  @Test
  public void deleteUploadedFile_whenValid_thenDelete() {
    var uploadedFile = UploadedFileUtil.createUploadedFile();
    when(uploadedFileRepository.findById(uploadedFile.getFileId())).thenReturn(Optional.of(uploadedFile));

    var fileDeleteResult = fileUploadService.deleteUploadedFile(
        uploadedFile.getFileId(),
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileDeleteResult.isValid()).isTrue();
    assertThat(uploadedFile.getStatus()).isEqualTo(FileUploadStatus.DELETED);
    verify(uploadedFileRepository, times(1)).save(uploadedFile);
  }

  @Test
  public void deleteUploadedFile_whenError_thenNoDelete() {

    var uploadedFile = UploadedFileUtil.createUploadedFile();
    when(uploadedFileRepository.findById(uploadedFile.getFileId())).thenReturn(Optional.of(uploadedFile));
    when(uploadedFileRepository.save(any())).thenThrow(new RuntimeException("error"));

    var fileDeleteResult = fileUploadService.deleteUploadedFile(
        uploadedFile.getFileId(),
        UserTestingUtil.getWebUserAccount()
    );

    assertThat(fileDeleteResult.isValid()).isFalse();
  }

}