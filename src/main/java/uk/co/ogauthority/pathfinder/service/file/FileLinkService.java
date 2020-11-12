package uk.co.ogauthority.pathfinder.service.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.form.forminput.file.UploadMultipleFilesWithDescriptionForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;

@Service
public abstract class FileLinkService {

  private final ProjectDetailFileService projectDetailFileService;

  @Autowired
  public FileLinkService(ProjectDetailFileService projectDetailFileService) {
    this.projectDetailFileService = projectDetailFileService;
  }

  protected abstract ProjectDetailFilePurpose getFilePurpose();

  /**
   * Find all the FileLinkEntity which link to EntityWithLinkedFile.
   * @param entityWithLinkedFile the entity to use to search for lined FileLinkEntity
   * @return a list of all the FileLinkEntity which link to EntityWithLinkedFile
   */
  protected abstract List<? extends FileLinkEntity> findAllByFileLinkableEntity(
      EntityWithLinkedFile entityWithLinkedFile
  );

  /**
   * Delete all of the FileLinkEntity which link to EntityWithLinkedFile.
   * @param entityWithLinkedFile the entity to use to search for lined FileLinkEntity
   */
  protected abstract void deleteAllFileLinksLinkedToEntity(EntityWithLinkedFile entityWithLinkedFile);

  /**
   * Create a new FileLinkEntity instance.
   * @param entityWithLinkedFile the entity the file is linked to
   * @param projectDetailFile the project detail file the file links to
   * @return a populated FileLinkEntity
   */
  protected abstract FileLinkEntity createFileLinkEntity(EntityWithLinkedFile entityWithLinkedFile,
                                                         ProjectDetailFile projectDetailFile);

  /**
   * Save all the fileLinkEntities back to the database.
   * @param fileLinkEntities a list of FileLinkEntity to save
   */
  protected abstract void saveFileLinks(List<? extends FileLinkEntity> fileLinkEntities);

  /**
   * Delete all the fileLinkEntities from the database.
   * @param fileLinkEntities a list of FileLinkEntity to delete
   */
  protected abstract void deleteFileLinks(List<? extends FileLinkEntity> fileLinkEntities);

  /**
   * Delete the FileLinkEntity associated to the provided projectDetailFile.
   * @param projectDetailFile the ProjectDetailFile to use to find the FileLinkEntity to delete
   */
  protected abstract void deleteFileLinkByProjectDetailFile(ProjectDetailFile projectDetailFile);

  /**
   * Method to update the file links based on the form provided.
   * @param entityWithLinkedFile The entity to link the files within the form to
   * @param form The form which contains the files
   * @param userAccount The user undertaking the action
   */
  @Transactional
  public void updateFileLinks(EntityWithLinkedFile entityWithLinkedFile,
                              UploadMultipleFilesWithDescriptionForm form,
                              AuthenticatedUserAccount userAccount) {

    deleteAllFileLinksLinkedToEntity(entityWithLinkedFile);

    // update the project detail files with files from from
    var persistedProjectDetailFileMap = projectDetailFileService.updateFiles(
        form,
        entityWithLinkedFile.getProjectDetail(),
        getFilePurpose(),
        FileUpdateMode.KEEP_UNLINKED_FILES,
        userAccount
    )
        .stream()
        .collect(Collectors.toMap(ProjectDetailFile::getFileId, file -> file));

    var fileLinkEntities = new ArrayList<FileLinkEntity>();

    // for each file in the form create a link to this file link entity
    form.getUploadedFileWithDescriptionForms()
        .forEach(uploadFileWithDescriptionForm -> {
          var projectDetailFile = persistedProjectDetailFileMap.get(uploadFileWithDescriptionForm.getUploadedFileId());
          var fileLink = createFileLinkEntity(entityWithLinkedFile, projectDetailFile);
          fileLinkEntities.add(fileLink);
        });

    saveFileLinks(fileLinkEntities);
  }

  /**
   * Method to remove all the file links associated to the provided entityWithLinkedFile.
   * @param entityWithLinkedFile The EntityWithLinkedFile to use to identify the associated file links
   */
  protected void deleteAllFileLinksAndProjectDetailFilesLinkedToEntity(EntityWithLinkedFile entityWithLinkedFile) {

    var fileLinkEntities = findAllByFileLinkableEntity(entityWithLinkedFile);
    deleteFileLinks(fileLinkEntities);

    // remove the project detail files no longer required
    var projectDetailFiles = fileLinkEntities
        .stream()
        .map(FileLinkEntity::getProjectDetailFile)
        .collect(Collectors.toList());

    projectDetailFileService.removeProjectDetailFiles(projectDetailFiles);
  }

  /**
   * Retrieve a list of UploadedFileView for files linked to the provided entityWithLinkedFile.
   * @param entityWithLinkedFile The EntityWithLinkedFile to use to find associated files
   * @return a list of UploadedFileView relating to the provided EntityWithLinkedFile
   */
  protected List<UploadedFileView> getFileUploadViewsLinkedEntity(EntityWithLinkedFile entityWithLinkedFile) {
    return findAllByFileLinkableEntity(entityWithLinkedFile)
        .stream()
        .map(fileLink ->
            projectDetailFileService.getUploadedFileView(
                entityWithLinkedFile.getProjectDetail(),
                fileLink.getProjectDetailFile().getFileId(),
                getFilePurpose(),
                FileLinkStatus.FULL
            )
        )
        .collect(Collectors.toList());
  }
}
