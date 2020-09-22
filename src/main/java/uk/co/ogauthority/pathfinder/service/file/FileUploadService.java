package uk.co.ogauthority.pathfinder.service.file;

import java.sql.Blob;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadProperties;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.config.file.UploadErrorType;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileUploadStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.file.UploadedFileRepository;

@Service
public class FileUploadService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadService.class);

  private final FileUploadProperties fileUploadProperties;
  private final UploadedFileRepository uploadedFileRepository;
  private final VirusScanService virusScanService;
  private final List<String> allowedExtensions;

  @Autowired
  public FileUploadService(FileUploadProperties fileUploadProperties,
                           UploadedFileRepository uploaded,
                           VirusScanService virusScanService) {
    this.fileUploadProperties = fileUploadProperties;
    this.uploadedFileRepository = uploaded;
    this.allowedExtensions = fileUploadProperties.getAllowedExtensions();
    this.virusScanService = virusScanService;
  }

  public UploadFileWithDescriptionForm createUploadFileWithDescriptionFormFromView(UploadedFileView uploadedFileView) {
    var form = new UploadFileWithDescriptionForm();
    form.setUploadedFileDescription(uploadedFileView.getFileDescription());
    form.setUploadedFileId(uploadedFileView.getFileId());
    form.setUploadedFileInstant(uploadedFileView.getFileUploadedTime());
    return form;
  }

  public UploadedFile getFileById(String fileId) {
    return uploadedFileRepository.findById(fileId).orElseThrow(
        () -> new PathfinderEntityNotFoundException(String.format("File with id %s not found", fileId)));
  }

  private String sanitiseFilename(String filename) {
    return filename.replaceAll(fileUploadProperties.getFileNameFilter(), "_");
  }

  /**
   * Construct file upload result from multipartfile.
   *
   * @param file the multipart file
   * @param user the logged in user
   * @return the FileUploadResult object storing the details of the uploaded file
   */
  @Transactional
  public FileUploadResult processUpload(MultipartFile file, WebUserAccount user) {
    String fileId = generateFileId();
    String filename = sanitiseFilename(Objects.requireNonNull(file.getOriginalFilename()));

    if (!isFileExtensionAllowed(filename)) {
      return FileUploadResult.generateFailedFileUploadResult(filename, file, UploadErrorType.EXTENSION_NOT_ALLOWED);
    }

    if (!isFileSizeAllowed(file.getSize())) {
      return FileUploadResult.generateFailedFileUploadResult(filename, file, UploadErrorType.MAX_FILE_SIZE_EXCEEDED);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("Starting virus scan for file: %s", filename));
    }

    if (virusScanService.hasVirus(file)) {
      if (LOGGER.isWarnEnabled()) {
        LOGGER.warn(String.format("Virus found in uploaded file: %s with fileId: %s", filename, fileId));
      }
      return FileUploadResult.generateFailedFileUploadResult(filename, file, UploadErrorType.VIRUS_FOUND_IN_FILE);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format(
          "Completed virus scan for file: %s with fileID: %s Upload request by user: %s",
          filename,
          fileId,
          user.getWuaId()
      ));
    }

    try {
      Blob blob = new SerialBlob(file.getBytes());
      var uploadedFile = createUploadedFile(fileId, filename, file, blob, user);

      uploadedFileRepository.save(uploadedFile);
      blob.free();

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(String.format(
            "Completed upload of file: %s with fileId: %s Uploaded by User: %s",
            filename,
            fileId,
            user.getWuaId()
        ));
      }
      return FileUploadResult.generateSuccessfulFileUploadResult(
          uploadedFile.getFileId(),
          filename,
          file.getSize(),
          file.getContentType()
      );
    } catch (Exception e) {

      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(String.format("Failed to upload file with name %s", filename), e);
      }

      return FileUploadResult.generateFailedFileUploadResult(filename, file, UploadErrorType.INTERNAL_SERVER_ERROR);
    }
  }

  private UploadedFile createUploadedFile(String fileId,
                                          String filename,
                                          MultipartFile file,
                                          Blob blobData,
                                          WebUserAccount user) {
    var uploadedFile = new UploadedFile();
    uploadedFile.setFileId(fileId);
    uploadedFile.setFileName(filename);
    uploadedFile.setFileData(blobData);
    uploadedFile.setContentType(file.getContentType());
    uploadedFile.setFileSize(file.getSize());
    uploadedFile.setUploadDatetime(Instant.now());
    uploadedFile.setUploadedByWuaId(user.getWuaId());
    uploadedFile.setLastUpdatedByWuaId(user.getWuaId());
    uploadedFile.setStatus(FileUploadStatus.CURRENT);
    return uploadedFile;
  }

  private boolean isFileExtensionAllowed(String filename) {
    String lowercase = filename.toLowerCase();
    return allowedExtensions.stream()
        .anyMatch(lowercase::endsWith);
  }

  private boolean isFileSizeAllowed(long fileSize) {
    return fileSize <= fileUploadProperties.getMaxFileSize();
  }

  private String generateFileId() {
    return "file_" + UUID.randomUUID().toString();
  }

  @Transactional
  public FileDeleteResult deleteUploadedFile(String fileId, WebUserAccount lastUpdatedByWua) {
    UploadedFile file = getFileById(fileId);
    return processDelete(file, lastUpdatedByWua);
  }

  private FileDeleteResult processDelete(UploadedFile file, WebUserAccount lastUpdatedByWua) {
    try {
      deleteFile(file, lastUpdatedByWua);
      return FileDeleteResult.generateSuccessfulFileDeleteResult(file.getFileId());
    } catch (Exception e) {
      LOGGER.error("Failed to delete file: " + file.getFileName(), e);
      return FileDeleteResult.generateFailedFileDeleteResult(file.getFileId());
    }
  }

  private void deleteFile(UploadedFile file, WebUserAccount lastUpdatedByWua) {
    file.setStatus(FileUploadStatus.DELETED);
    file.setLastUpdatedByWuaId(lastUpdatedByWua.getWuaId());
    uploadedFileRepository.save(file);
  }
}
