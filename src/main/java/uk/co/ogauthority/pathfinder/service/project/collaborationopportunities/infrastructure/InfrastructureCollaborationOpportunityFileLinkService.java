package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.EntityWithLinkedFile;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;
import uk.co.ogauthority.pathfinder.service.file.FileLinkService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;

@Service
public class InfrastructureCollaborationOpportunityFileLinkService extends FileLinkService {

  public static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY;

  private final InfrastructureCollaborationOpportunityFileLinkRepository infrastructureCollaborationOpportunityFileLinkRepository;

  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public InfrastructureCollaborationOpportunityFileLinkService(
      InfrastructureCollaborationOpportunityFileLinkRepository infrastructureCollaborationOpportunityFileLinkRepository,
      ProjectDetailFileService projectDetailFileService,
      EntityDuplicationService entityDuplicationService
  ) {
    super(projectDetailFileService);
    this.infrastructureCollaborationOpportunityFileLinkRepository = infrastructureCollaborationOpportunityFileLinkRepository;
    this.entityDuplicationService = entityDuplicationService;
  }

  @Transactional
  public void updateCollaborationOpportunityFileLinks(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity,
      InfrastructureCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount) {
    updateFileLinks(infrastructureCollaborationOpportunity, form, authenticatedUserAccount);
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
   * @param infrastructureCollaborationOpportunity the collaboration opportunity to to remove all the file links from
   */
  @Transactional
  public void removeCollaborationOpportunityFileLinks(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity) {
    deleteAllFileLinksAndProjectDetailFilesLinkedToEntity(infrastructureCollaborationOpportunity);
  }

  /**
   * Remove all collaboration opportunity file links associated to the collection of collaboration opportunity.
   * @param collaborationOpportunities the collaboration opportunities to to remove all the file links from
   */
  public void removeCollaborationOpportunityFileLinks(List<InfrastructureCollaborationOpportunity> collaborationOpportunities) {
    collaborationOpportunities.forEach(this::deleteAllFileLinksAndProjectDetailFilesLinkedToEntity);
  }

  /**
   * Retrieve a list of UploadedFileView objects for documents linked to a collaboration opportunity.
   * @param infrastructureCollaborationOpportunity the collaboration opportunity to get the file views for
   * @return a list of UploadedFileView linked to an collaboration opportunity
   */
  public List<UploadedFileView> getFileUploadViewsLinkedToOpportunity(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity) {
    return getFileUploadViewsLinkedEntity(infrastructureCollaborationOpportunity);
  }

  /**
   * Retrieve all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity.
   * @param infrastructureCollaborationOpportunity the collaboration opportunity to retrieve the file links for
   * @return all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity
   */
  public List<InfrastructureCollaborationOpportunityFileLink> getAllByCollaborationOpportunity(
      InfrastructureCollaborationOpportunity infrastructureCollaborationOpportunity
  ) {
    return findAllByFileLinkableEntity(infrastructureCollaborationOpportunity);
  }

  public List<InfrastructureCollaborationOpportunityFileLink> findAllByProjectDetail(ProjectDetail projectDetail) {
    return infrastructureCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail);
  }

  @Override
  public ProjectDetailFilePurpose getFilePurpose() {
    return FILE_PURPOSE;
  }

  @Override
  public List<InfrastructureCollaborationOpportunityFileLink> findAllByFileLinkableEntity(
      EntityWithLinkedFile entityWithLinkedFile
  ) {
    return infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(
        (InfrastructureCollaborationOpportunity) entityWithLinkedFile
    );
  }

  @Override
  @Transactional
  public void deleteAllFileLinksLinkedToEntity(EntityWithLinkedFile entityWithLinkedFile) {
    var existingFileLinks = findAllByFileLinkableEntity(entityWithLinkedFile);
    infrastructureCollaborationOpportunityFileLinkRepository.deleteAll(existingFileLinks);
  }

  @Override
  public FileLinkEntity createFileLinkEntity(EntityWithLinkedFile entityWithLinkedFile,
                                             ProjectDetailFile projectDetailFile) {
    var collaborationOpportunityFileLink = new InfrastructureCollaborationOpportunityFileLink();
    collaborationOpportunityFileLink.setCollaborationOpportunity((InfrastructureCollaborationOpportunity) entityWithLinkedFile);
    collaborationOpportunityFileLink.setProjectDetailFile(projectDetailFile);
    return collaborationOpportunityFileLink;
  }

  @Override
  @Transactional
  public void saveFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var collaborationOpportunityFileLinks = mapFileLinkEntityList(fileLinkEntities);
    infrastructureCollaborationOpportunityFileLinkRepository.saveAll(collaborationOpportunityFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var collaborationOpportunityFileLinks = mapFileLinkEntityList(fileLinkEntities);
    infrastructureCollaborationOpportunityFileLinkRepository.deleteAll(collaborationOpportunityFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinkByProjectDetailFile(ProjectDetailFile projectDetailFile) {
    var collaborationOpportunityFileLink =
        infrastructureCollaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)
            .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
                "CollaborationOpportunityFileLink for project detail file with file id %s could not be found",
                projectDetailFile.getFileId()
            )));
    infrastructureCollaborationOpportunityFileLinkRepository.delete(collaborationOpportunityFileLink);
  }

  public void copyCollaborationOpportunityFileLinkData(
      ProjectDetail fromDetail,
      ProjectDetail toDetail,
      Map<InfrastructureCollaborationOpportunity, InfrastructureCollaborationOpportunity> duplicatedOpportunityLookup
  ) {

    // get a map of all the file links associated to the fromDetail
    var originalOpportunityToFileLinkLookup = findAllByProjectDetail(fromDetail)
        .stream()
        .collect(Collectors.groupingBy(InfrastructureCollaborationOpportunityFileLink::getCollaborationOpportunity));

    // duplicate any project detail file data associated to the fromDetail
    final var duplicatedProjectDetailLookup = copyProjectDetailFileData(
        fromDetail,
        toDetail,
        getFilePurpose()
    );

    originalOpportunityToFileLinkLookup.forEach((originalOpportunity, originalOpportunityFileLinks) -> {

      final var duplicatedOpportunityFileLinks = entityDuplicationService.duplicateEntitiesAndSetNewParent(
          originalOpportunityFileLinks,
          duplicatedOpportunityLookup.get(originalOpportunity),
          InfrastructureCollaborationOpportunityFileLink.class
      );

      var opportunityFileLinksToUpdate = new ArrayList<InfrastructureCollaborationOpportunityFileLink>();

      // for each file link, lookup the new duplicated ProjectDetailFile and reparent
      duplicatedOpportunityFileLinks.forEach(duplicatedUpcomingTenderFileLink -> {
        final var duplicatedProjectDetailFile = duplicatedProjectDetailLookup.get(
            duplicatedUpcomingTenderFileLink.getOriginalEntity().getProjectDetailFile()
        );

        var opportunityFileLink = duplicatedUpcomingTenderFileLink.getDuplicateEntity();
        opportunityFileLink.setProjectDetailFile(duplicatedProjectDetailFile);
        opportunityFileLinksToUpdate.add(opportunityFileLink);

      });

      infrastructureCollaborationOpportunityFileLinkRepository.saveAll(opportunityFileLinksToUpdate);

    });
  }

  private List<InfrastructureCollaborationOpportunityFileLink> mapFileLinkEntityList(List<? extends FileLinkEntity> fileLinkEntities) {
    return fileLinkEntities
        .stream()
        .map(InfrastructureCollaborationOpportunityFileLink.class::cast)
        .collect(Collectors.toList());
  }

}
