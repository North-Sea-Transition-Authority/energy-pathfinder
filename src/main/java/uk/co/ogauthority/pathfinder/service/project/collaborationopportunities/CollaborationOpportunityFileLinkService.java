package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.file.EntityWithLinkedFile;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;
import uk.co.ogauthority.pathfinder.service.file.FileLinkService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;

@Service
public class CollaborationOpportunityFileLinkService extends FileLinkService {

  public static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY;

  private final CollaborationOpportunityFileLinkRepository collaborationOpportunityFileLinkRepository;

  @Autowired
  public CollaborationOpportunityFileLinkService(
      CollaborationOpportunityFileLinkRepository collaborationOpportunityFileLinkRepository,
      ProjectDetailFileService projectDetailFileService
  ) {
    super(projectDetailFileService);
    this.collaborationOpportunityFileLinkRepository = collaborationOpportunityFileLinkRepository;
  }

  @Transactional
  public void updateCollaborationOpportunityFileLinks(CollaborationOpportunity collaborationOpportunity,
                                                      CollaborationOpportunityForm form,
                                                      AuthenticatedUserAccount authenticatedUserAccount) {
    updateFileLinks(collaborationOpportunity, form, authenticatedUserAccount);
  }

  /**
   * Remove an CollaborationOpportunityFileLink related to a ProjectDetailFile.
   * @param projectDetailFile the project detail file to identify the CollaborationOpportunityFileLink
   */
  @Transactional
  public void removeCollaborationOpportunityFileLink(ProjectDetailFile projectDetailFile) {
    deleteFileLinkByProjectDetailFile(projectDetailFile);
  }

  /**
   * Remove all collaboration opportunity file links associated to a collaboration opportunity.
   * @param collaborationOpportunity the collaboration opportunity to to remove all the file links from
   */
  @Transactional
  public void removeCollaborationOpportunityFileLinks(CollaborationOpportunity collaborationOpportunity) {
    deleteAllFileLinksAndProjectDetailFilesLinkedToEntity(collaborationOpportunity);
  }

  /**
   * Retrieve a list of UploadedFileView objects for documents linked to a collaboration opportunity.
   * @param collaborationOpportunity the collaboration opportunity to get the file views for
   * @return a list of UploadedFileView linked to an collaboration opportunity
   */
  public List<UploadedFileView> getFileUploadViewsLinkedToOpportunity(CollaborationOpportunity collaborationOpportunity) {
    return getFileUploadViewsLinkedEntity(collaborationOpportunity);
  }

  /**
   * Retrieve all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity.
   * @param collaborationOpportunity the collaboration opportunity to retrieve the file links for
   * @return all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity
   */
  public List<CollaborationOpportunityFileLink> getAllByCollaborationOpportunity(
      CollaborationOpportunity collaborationOpportunity
  ) {
    return findAllByFileLinkableEntity(collaborationOpportunity);
  }

  @Override
  public ProjectDetailFilePurpose getFilePurpose() {
    return FILE_PURPOSE;
  }

  @Override
  public List<CollaborationOpportunityFileLink> findAllByFileLinkableEntity(
      EntityWithLinkedFile entityWithLinkedFile
  ) {
    return collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(
        (CollaborationOpportunity) entityWithLinkedFile
    );
  }

  @Override
  @Transactional
  public void deleteAllFileLinksLinkedToEntity(EntityWithLinkedFile entityWithLinkedFile) {
    var existingFileLinks = findAllByFileLinkableEntity(entityWithLinkedFile);
    collaborationOpportunityFileLinkRepository.deleteAll(existingFileLinks);
  }

  @Override
  public FileLinkEntity createFileLinkEntity(EntityWithLinkedFile entityWithLinkedFile,
                                             ProjectDetailFile projectDetailFile) {
    var collaborationOpportunityFileLink = new CollaborationOpportunityFileLink();
    collaborationOpportunityFileLink.setCollaborationOpportunity((CollaborationOpportunity) entityWithLinkedFile);
    collaborationOpportunityFileLink.setProjectDetailFile(projectDetailFile);
    return collaborationOpportunityFileLink;
  }

  @Override
  @Transactional
  public void saveFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var collaborationOpportunityFileLinks = mapFileLinkEntityList(fileLinkEntities);
    collaborationOpportunityFileLinkRepository.saveAll(collaborationOpportunityFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var collaborationOpportunityFileLinks = mapFileLinkEntityList(fileLinkEntities);
    collaborationOpportunityFileLinkRepository.deleteAll(collaborationOpportunityFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinkByProjectDetailFile(ProjectDetailFile projectDetailFile) {
    var collaborationOpportunityFileLink =
        collaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)
            .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
                "CollaborationOpportunityFileLink for project detail file with file id %s could not be found",
                projectDetailFile.getFileId()
            )));
    collaborationOpportunityFileLinkRepository.delete(collaborationOpportunityFileLink);
  }

  private List<CollaborationOpportunityFileLink> mapFileLinkEntityList(List<? extends FileLinkEntity> fileLinkEntities) {
    return fileLinkEntities
        .stream()
        .map(fileLinkEntity -> (CollaborationOpportunityFileLink) fileLinkEntity)
        .collect(Collectors.toList());
  }

}
