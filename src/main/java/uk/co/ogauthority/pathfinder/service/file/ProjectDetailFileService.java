package uk.co.ogauthority.pathfinder.service.file;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.config.file.FileUploadResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.Person;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.file.UploadedFile;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadFileWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadMultipleFilesWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.repository.file.ProjectDetailFileRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.team.TeamService;

@Service
public class ProjectDetailFileService {

  private final FileUploadService fileUploadService;
  private final ProjectDetailFileRepository projectDetailFileRepository;
  private final EntityDuplicationService entityDuplicationService;
  private final ProjectService projectService;
  private final TeamService teamService;

  @Autowired
  public ProjectDetailFileService(FileUploadService fileUploadService,
                                  ProjectDetailFileRepository projectDetailFileRepository,
                                  EntityDuplicationService entityDuplicationService,
                                  ProjectService projectService,
                                  TeamService teamService) {
    this.fileUploadService = fileUploadService;
    this.projectDetailFileRepository = projectDetailFileRepository;
    this.entityDuplicationService = entityDuplicationService;
    this.projectService = projectService;
    this.teamService = teamService;
  }

  /**
   * Retrieve a list of file views for every file that is present on the provided file upload form.
   *
   * @param uploadForm containing files
   * @param projectDetail to get files for
   * @param purpose to get files for
   * @return list of files with populated descriptions
   */
  public List<UploadedFileView> getFileViewsLinkedToForm(UploadMultipleFilesWithDescriptionForm uploadForm,
                                                         ProjectDetail projectDetail,
                                                         ProjectDetailFilePurpose purpose) {

    Map<String, UploadFileWithDescriptionForm> fileIdToFormMap = getFileIdToFormMap(uploadForm);

    var formFileViewList = getUploadedFileViews(projectDetail, purpose, FileLinkStatus.ALL).stream()
        .filter(fileView -> fileIdToFormMap.containsKey(fileView.getFileId()))
        .collect(Collectors.toList());

    formFileViewList
        .forEach(fileView -> fileView.setFileDescription(
            fileIdToFormMap.get(fileView.getFileId()).getUploadedFileDescription()));

    return formFileViewList;

  }

  /**
   * Populate a file upload form with fully linked project files which have a specific purpose.
   *
   * @param uploadForm to populate
   * @param projectDetail we are getting files for
   * @param purpose of the files we are getting
   */
  public void mapFilesToForm(UploadMultipleFilesWithDescriptionForm uploadForm,
                             ProjectDetail projectDetail,
                             ProjectDetailFilePurpose purpose) {

    List<UploadFileWithDescriptionForm> fileFormList = getUploadedFileViews(
        projectDetail,
        purpose,
        FileLinkStatus.FULL
    )
        .stream()
        .map(fileUploadService::createUploadFileWithDescriptionFormFromView)
        .collect(Collectors.toList());

    uploadForm.setUploadedFileWithDescriptionForms(fileFormList);

  }

  /**
   * Upload a file and create a temporary link between the file and the application it was uploaded to, alongside the file purpose.
   *
   * @param file being uploaded
   * @param projectDetail to link it to
   * @param purpose for the file
   * @param user uploading the file
   * @return a successful (or failed) upload result
   */
  @Transactional
  public FileUploadResult processInitialUpload(MultipartFile file,
                                               ProjectDetail projectDetail,
                                               ProjectDetailFilePurpose purpose,
                                               WebUserAccount user) {

    var result = fileUploadService.processUpload(file, user);

    if (result.isValid()) {
      var uploadedFile = fileUploadService.getFileById(result.getFileId().orElseThrow());
      var projectDetailFile = new ProjectDetailFile(projectDetail, uploadedFile, purpose, FileLinkStatus.TEMPORARY);
      projectDetailFileRepository.save(projectDetailFile);
    }

    return result;

  }

  /**
   * Fully link temporary files that are still present, update file descriptions, delete files that have been deleted onscreen.
   *
   * @param uploadForm containing files to update
   * @param projectDetail we are updating files for
   * @param updateMode the mode used when updating files
   * @param purpose of files being updated
   * @param user updating the files
   */
  @Transactional
  public List<ProjectDetailFile> updateFiles(UploadMultipleFilesWithDescriptionForm uploadForm,
                                             ProjectDetail projectDetail,
                                             ProjectDetailFilePurpose purpose,
                                             FileUpdateMode updateMode,
                                             WebUserAccount user) {

    Map<String, UploadFileWithDescriptionForm> uploadedFileIdToFormMap = getFileIdToFormMap(uploadForm);

    var existingLinkedFiles = projectDetailFileRepository.findAllByProjectDetailAndPurpose(
        projectDetail,
        purpose
    );

    var filesToUpdate = new HashSet<ProjectDetailFile>();
    var filesToRemove = new HashSet<ProjectDetailFile>();

    // if file is still in list of uploaded files, update description and add to update set
    // else file can be deleted so add to remove set
    existingLinkedFiles.forEach(existingFile -> {

      if (uploadedFileIdToFormMap.containsKey(existingFile.getFileId())) {

        updateFileDescriptionAndFullyLink(existingFile, uploadedFileIdToFormMap.get(existingFile.getFileId()));
        filesToUpdate.add(existingFile);

      } else {
        filesToRemove.add(existingFile);
      }

    });

    var persistedFiles = projectDetailFileRepository.saveAll(filesToUpdate);

    if (updateMode.equals(FileUpdateMode.DELETE_UNLINKED_FILES)) {
      deleteProjectDetailFileLinksAndUploadedFiles(filesToRemove, user);
    }

    return StreamSupport.stream(persistedFiles.spliterator(), false).collect(Collectors.toList());

  }

  public Map<String, UploadFileWithDescriptionForm> getFileIdToFormMap(
      UploadMultipleFilesWithDescriptionForm uploadForm) {
    return uploadForm.getUploadedFileWithDescriptionForms().stream()
        .collect(Collectors.toMap(UploadFileWithDescriptionForm::getUploadedFileId, f -> f));
  }

  private void updateFileDescriptionAndFullyLink(ProjectDetailFile projectDetailFile,
                                                 UploadFileWithDescriptionForm fileForm) {
    projectDetailFile.setDescription(fileForm.getUploadedFileDescription());
    projectDetailFile.setFileLinkStatus(FileLinkStatus.FULL);
  }

  void deleteProjectDetailFileLinksAndUploadedFiles(Iterable<ProjectDetailFile> filesToBeRemoved,
                                                    WebUserAccount user) {

    filesToBeRemoved.forEach(fileToRemove -> {
      var result = fileUploadService.deleteUploadedFile(fileToRemove.getFileId(), user);
      if (!result.isValid()) {
        throw new RuntimeException("Could not delete uploaded file with Id:" + fileToRemove.getFileId());
      }
    });

    projectDetailFileRepository.deleteAll(filesToBeRemoved);

  }


  public UploadedFile getUploadedFileById(String fileId) {
    return fileUploadService.getFileById(fileId);
  }

  /**
   * Get files for an application with a specified purpose and link status as uploaded file views.
   */
  public List<UploadedFileView> getUploadedFileViews(ProjectDetail projectDetail,
                                                     ProjectDetailFilePurpose purpose,
                                                     FileLinkStatus fileLinkStatus) {

    var uploadedFileViews = projectDetailFileRepository.findAllAsFileViewByProjectDetailAndPurposeAndFileLinkStatus(
        projectDetail, purpose, fileLinkStatus);

    uploadedFileViews
        .forEach(ufv -> ufv.setFileUrl(getDownloadUrl(projectDetail, purpose, ufv.getFileId())));

    return uploadedFileViews;
  }

  /**
   * Get a file for a project detail with a specified purpose and link status as an uploaded file view.
   */
  public UploadedFileView getUploadedFileView(ProjectDetail projectDetail,
                                              String fileId,
                                              ProjectDetailFilePurpose purpose,
                                              FileLinkStatus fileLinkStatus) {

    var fileView = projectDetailFileRepository.findAsFileViewByProjectDetailAndFileIdAndPurposeAndFileLinkStatus(
        projectDetail,
        fileId,
        purpose,
        fileLinkStatus
    );

    fileView.setFileUrl(getDownloadUrl(projectDetail, purpose, fileView.getFileId()));
    return fileView;

  }

  private String getDownloadUrl(ProjectDetail projectDetail, ProjectDetailFilePurpose purpose, String fileId) {
    return ReverseRouter.route(on(purpose.getFileControllerClass()).handleDownload(
        projectDetail.getProject().getId(),
        projectDetail.getVersion(),
        fileId,
        null
    ));
  }

  /**
   * Delete an individual file for a project.
   *
   * @param projectDetailFile file being deleted
   * @param user deleting file
   * @param actionBeforeDelete a consumer to run if the result is valid, prior to deletion.
   * @return a successful (or failed) file delete result
   */
  @Transactional
  public FileDeleteResult processFileDeletionWithPreDeleteAction(ProjectDetailFile projectDetailFile,
                                                                 WebUserAccount user,
                                                                 Consumer<ProjectDetailFile> actionBeforeDelete) {
    var result = fileUploadService.deleteUploadedFile(projectDetailFile.getFileId(), user);

    if (result.isValid()) {
      actionBeforeDelete.accept(projectDetailFile);
      projectDetailFileRepository.delete(projectDetailFile);
    }

    return result;
  }

  /**
   * Delete an individual file for an application.
   *
   * @param projectDetailFile file being deleted
   * @param user deleting file
   * @return a successful (or failed) file delete result
   */
  @Transactional
  public FileDeleteResult processFileDeletion(ProjectDetailFile projectDetailFile,
                                              WebUserAccount user) {
    return processFileDeletionWithPreDeleteAction(projectDetailFile, user, padFileArg -> {
    });
  }

  public ProjectDetailFile getProjectDetailFileByProjectDetailAndFileId(ProjectDetail projectDetail,
                                                                        String fileId) {
    return projectDetailFileRepository.findByProjectDetailAndUploadedFile_FileId(projectDetail, fileId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "Couldn't find a ProjectDetailFile for project detail with ID: %s and fileId: %s",
            projectDetail.getId(),
            fileId)));
  }

  public ProjectDetailFile getProjectDetailFileByProjectDetailVersionAndFileId(Project project,
                                                                               Integer projectDetailVersion,
                                                                               String fileId) {
    var detail = projectService.getDetailOrError(project.getId(), projectDetailVersion);
    return projectDetailFileRepository.findByProjectDetailAndUploadedFile_FileId(detail, fileId)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "Couldn't find a ProjectDetailFile for project detail with ID: %s and version %s and fileId: %s",
            detail.getId(),
            projectDetailVersion,
            fileId))
        );
  }

  /**
   * Remove ProjectDetailFile that are linked to a detail and purpose and are not in a specified list.
   * @param projectDetail detail for app to cleanup files for
   * @param purpose of files we're looking at
   * @param excludeProjectDetailFileIds list of ids for ProjectDetailFile we don't want to remove
   */
  @Transactional
  public void cleanupFiles(ProjectDetail projectDetail,
                           ProjectDetailFilePurpose purpose,
                           List<Integer> excludeProjectDetailFileIds) {

    List<ProjectDetailFile> filesToCleanup;

    if (excludeProjectDetailFileIds.isEmpty()) {
      filesToCleanup = projectDetailFileRepository.findAllByProjectDetailAndPurpose(projectDetail, purpose);
    } else {
      filesToCleanup = projectDetailFileRepository.findAllByProjectDetailAndFilePurposeAndIdNotIn(
          projectDetail,
          purpose,
          excludeProjectDetailFileIds
      );
    }

    projectDetailFileRepository.deleteAll(filesToCleanup);

  }

  @Transactional
  public void removeProjectDetailFiles(List<ProjectDetailFile> projectDetailFilesToRemove) {
    projectDetailFileRepository.deleteAll(projectDetailFilesToRemove);
  }

  public Map<ProjectDetailFile, ProjectDetailFile> copyProjectDetailFileData(
      ProjectDetail fromDetail,
      ProjectDetail toDetail,
      ProjectDetailFilePurpose projectDetailFilePurpose
  ) {
    final var duplicateProjectDetailFiles = entityDuplicationService.duplicateEntitiesAndSetNewParent(
        projectDetailFileRepository.findAllByProjectDetailAndPurpose(fromDetail, projectDetailFilePurpose),
        toDetail,
        ProjectDetailFile.class
    );

    return entityDuplicationService.createDuplicatedEntityPairingMap(duplicateProjectDetailFiles);
  }

  public boolean canAccessFiles(ProjectDetail projectDetail, Person person) {
    var isRegulator = teamService.isPersonMemberOfRegulatorTeam(person);
    return !(projectDetail.getStatus().equals(ProjectStatus.DRAFT) && isRegulator);
  }

}
