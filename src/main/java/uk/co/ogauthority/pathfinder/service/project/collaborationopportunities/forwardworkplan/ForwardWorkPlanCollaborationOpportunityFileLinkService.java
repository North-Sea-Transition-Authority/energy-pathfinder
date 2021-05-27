package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.config.file.FileDeleteResult;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.EntityWithLinkedFile;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;
import uk.co.ogauthority.pathfinder.service.file.FileLinkService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;

@Service
public class ForwardWorkPlanCollaborationOpportunityFileLinkService extends FileLinkService {

  public static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.WORK_PLAN_COLLABORATION_OPPORTUNITY;

  private final ForwardWorkPlanCollaborationOpportunityFileLinkRepository forwardWorkPlanCollaborationOpportunityFileLinkRepository;

  private final EntityDuplicationService entityDuplicationService;

  private final ProjectDetailFileService projectDetailFileService;

  @Autowired
  public ForwardWorkPlanCollaborationOpportunityFileLinkService(
      ForwardWorkPlanCollaborationOpportunityFileLinkRepository forwardWorkPlanCollaborationOpportunityFileLinkRepository,
      ProjectDetailFileService projectDetailFileService,
      EntityDuplicationService entityDuplicationService
  ) {
    super(projectDetailFileService);
    this.forwardWorkPlanCollaborationOpportunityFileLinkRepository = forwardWorkPlanCollaborationOpportunityFileLinkRepository;
    this.entityDuplicationService = entityDuplicationService;
    this.projectDetailFileService = projectDetailFileService;
  }

  @Transactional
  public void updateCollaborationOpportunityFileLinks(
      ForwardWorkPlanCollaborationOpportunity forwardWorkPlanCollaborationOpportunity,
      ForwardWorkPlanCollaborationOpportunityForm form,
      AuthenticatedUserAccount authenticatedUserAccount) {
    updateFileLinks(forwardWorkPlanCollaborationOpportunity, form, authenticatedUserAccount);
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
   * @param forwardWorkPlanCollaborationOpportunity the collaboration opportunity to to remove all the file links from
   */
  @Transactional
  public void removeCollaborationOpportunityFileLinks(
      ForwardWorkPlanCollaborationOpportunity forwardWorkPlanCollaborationOpportunity
  ) {
    deleteAllFileLinksAndProjectDetailFilesLinkedToEntity(forwardWorkPlanCollaborationOpportunity);
  }

  /**
   * Remove all collaboration opportunity file links associated to the collection of collaboration opportunity.
   * @param collaborationOpportunities the collaboration opportunities to to remove all the file links from
   */
  public void removeCollaborationOpportunityFileLinks(
      List<ForwardWorkPlanCollaborationOpportunity> collaborationOpportunities) {
    collaborationOpportunities.forEach(this::deleteAllFileLinksAndProjectDetailFilesLinkedToEntity);
  }

  /**
   * Retrieve a list of UploadedFileView objects for documents linked to a collaboration opportunity.
   * @param forwardWorkPlanCollaborationOpportunity the collaboration opportunity to get the file views for
   * @return a list of UploadedFileView linked to an collaboration opportunity
   */
  public List<UploadedFileView> getFileUploadViewsLinkedToOpportunity(
      ForwardWorkPlanCollaborationOpportunity forwardWorkPlanCollaborationOpportunity
  ) {
    return getFileUploadViewsLinkedEntity(forwardWorkPlanCollaborationOpportunity);
  }

  /**
   * Retrieve all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity.
   * @param forwardWorkPlanCollaborationOpportunity the collaboration opportunity to retrieve the file links for
   * @return all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity
   */
  public List<ForwardWorkPlanCollaborationOpportunityFileLink> getAllByCollaborationOpportunity(
      ForwardWorkPlanCollaborationOpportunity forwardWorkPlanCollaborationOpportunity
  ) {
    return findAllByFileLinkableEntity(forwardWorkPlanCollaborationOpportunity);
  }

  public List<ForwardWorkPlanCollaborationOpportunityFileLink> findAllByProjectDetail(ProjectDetail projectDetail) {
    return forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail);
  }

  /**
   * Remove an uploaded collaboration opportunity file.
   * @param fileId The file id to remove
   * @param projectDetail the project detail the file is linked to
   * @param webUserAccount the logged in user
   * @return a FileDeleteResult to indicate a success or failure of the removal
   */
  @Transactional
  public FileDeleteResult deleteCollaborationOpportunityFile(String fileId,
                                                             ProjectDetail projectDetail,
                                                             WebUserAccount webUserAccount) {
    var file = projectDetailFileService.getProjectDetailFileByProjectDetailAndFileId(
        projectDetail,
        fileId
    );

    if (file.getFileLinkStatus().equals(FileLinkStatus.FULL)) {
      // if fully linked we need to remove the collaboration opportunity file link
      removeCollaborationOpportunityFileLink(file);
    }

    return projectDetailFileService.processFileDeletion(file, webUserAccount);
  }

  public void copyCollaborationOpportunityFileLinkData(
      ProjectDetail fromDetail,
      ProjectDetail toDetail,
      Map<ForwardWorkPlanCollaborationOpportunity, ForwardWorkPlanCollaborationOpportunity> duplicatedOpportunityLookup
  ) {

    // get a map of all the file links associated to the fromDetail
    var originalOpportunityToFileLinkLookup = findAllByProjectDetail(fromDetail)
        .stream()
        .collect(Collectors.groupingBy(ForwardWorkPlanCollaborationOpportunityFileLink::getCollaborationOpportunity));

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
          ForwardWorkPlanCollaborationOpportunityFileLink.class
      );

      var opportunityFileLinksToUpdate = new ArrayList<ForwardWorkPlanCollaborationOpportunityFileLink>();

      // for each file link, lookup the new duplicated ProjectDetailFile and reparent
      duplicatedOpportunityFileLinks.forEach(duplicatedUpcomingTenderFileLink -> {
        final var duplicatedProjectDetailFile = duplicatedProjectDetailLookup.get(
            duplicatedUpcomingTenderFileLink.getOriginalEntity().getProjectDetailFile()
        );

        var opportunityFileLink = duplicatedUpcomingTenderFileLink.getDuplicateEntity();
        opportunityFileLink.setProjectDetailFile(duplicatedProjectDetailFile);
        opportunityFileLinksToUpdate.add(opportunityFileLink);

      });

      forwardWorkPlanCollaborationOpportunityFileLinkRepository.saveAll(opportunityFileLinksToUpdate);

    });
  }

  @Override
  public ProjectDetailFilePurpose getFilePurpose() {
    return FILE_PURPOSE;
  }

  @Override
  public List<ForwardWorkPlanCollaborationOpportunityFileLink> findAllByFileLinkableEntity(
      EntityWithLinkedFile entityWithLinkedFile
  ) {
    return forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(
        (ForwardWorkPlanCollaborationOpportunity) entityWithLinkedFile
    );
  }

  @Override
  @Transactional
  public void deleteAllFileLinksLinkedToEntity(EntityWithLinkedFile entityWithLinkedFile) {
    var existingFileLinks = findAllByFileLinkableEntity(entityWithLinkedFile);
    forwardWorkPlanCollaborationOpportunityFileLinkRepository.deleteAll(existingFileLinks);
  }

  @Override
  public FileLinkEntity createFileLinkEntity(EntityWithLinkedFile entityWithLinkedFile,
                                             ProjectDetailFile projectDetailFile) {
    var collaborationOpportunityFileLink = new ForwardWorkPlanCollaborationOpportunityFileLink();
    collaborationOpportunityFileLink.setCollaborationOpportunity((ForwardWorkPlanCollaborationOpportunity) entityWithLinkedFile);
    collaborationOpportunityFileLink.setProjectDetailFile(projectDetailFile);
    return collaborationOpportunityFileLink;
  }

  @Override
  @Transactional
  public void saveFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var collaborationOpportunityFileLinks = mapFileLinkEntityList(fileLinkEntities);
    forwardWorkPlanCollaborationOpportunityFileLinkRepository.saveAll(collaborationOpportunityFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var collaborationOpportunityFileLinks = mapFileLinkEntityList(fileLinkEntities);
    forwardWorkPlanCollaborationOpportunityFileLinkRepository.deleteAll(collaborationOpportunityFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinkByProjectDetailFile(ProjectDetailFile projectDetailFile) {
    var collaborationOpportunityFileLink =
        forwardWorkPlanCollaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)
            .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
                "CollaborationOpportunityFileLink for project detail file with file id %s could not be found",
                projectDetailFile.getFileId()
            )));
    forwardWorkPlanCollaborationOpportunityFileLinkRepository.delete(collaborationOpportunityFileLink);
  }

  private List<ForwardWorkPlanCollaborationOpportunityFileLink> mapFileLinkEntityList(List<? extends FileLinkEntity> fileLinkEntities) {
    return fileLinkEntities
        .stream()
        .map(ForwardWorkPlanCollaborationOpportunityFileLink.class::cast)
        .collect(Collectors.toList());
  }

}
