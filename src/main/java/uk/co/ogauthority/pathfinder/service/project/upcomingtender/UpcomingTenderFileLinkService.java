package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.EntityWithLinkedFile;
import uk.co.ogauthority.pathfinder.service.file.FileLinkEntity;
import uk.co.ogauthority.pathfinder.service.file.FileLinkService;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;

@Service
public class UpcomingTenderFileLinkService extends FileLinkService {

  public static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.UPCOMING_TENDER;

  private final UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository;

  private final EntityDuplicationService entityDuplicationService;

  @Autowired
  public UpcomingTenderFileLinkService(UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository,
                                       ProjectDetailFileService projectDetailFileService,
                                       EntityDuplicationService entityDuplicationService) {
    super(projectDetailFileService);
    this.upcomingTenderFileLinkRepository = upcomingTenderFileLinkRepository;
    this.entityDuplicationService = entityDuplicationService;
  }

  /**
   * Retrieve all the UpcomingTenderFileLink entities linked to the provided upcoming tender.
   * @param upcomingTender the upcoming tender to retrieve the file links for
   * @return all the UpcomingTenderFileLink entities linked to the provided upcoming tender
   */
  protected List<UpcomingTenderFileLink> getAllByUpcomingTender(UpcomingTender upcomingTender) {
    return upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender);
  }

  protected List<UpcomingTenderFileLink> findAllByProjectDetail(ProjectDetail projectDetail)  {
    return upcomingTenderFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail);
  }

  /**
   * Update the UpcomingTenderFileLinks for the provided upcomingTender.
   * @param upcomingTender The upcoming tender entity to link to
   * @param form The form object which contains the files we need to link to
   * @param authenticatedUserAccount The logged in user
   */
  @Transactional
  public void updateUpcomingTenderFileLinks(UpcomingTender upcomingTender,
                                            UpcomingTenderForm form,
                                            AuthenticatedUserAccount authenticatedUserAccount) {
    updateFileLinks(upcomingTender, form, authenticatedUserAccount);
  }

  /**
   * Remove an UpcomingTenderFileLink related to a ProjectDetailFile.
   * @param projectDetailFile the project detail file to identify the UpcomingTenderFileLink
   */
  @Transactional
  public void removeUpcomingTenderFileLink(ProjectDetailFile projectDetailFile) {
    deleteFileLinkByProjectDetailFile(projectDetailFile);
  }

  /**
   * Remove all upcoming tender file links associated to an upcoming tender.
   * @param upcomingTender the upcoming tender to to remove all the file links from
   */
  @Transactional
  public void removeUpcomingTenderFileLinks(UpcomingTender upcomingTender) {
    deleteAllFileLinksAndProjectDetailFilesLinkedToEntity(upcomingTender);
  }

  /**
   * Remove all upcoming tender file links associated to the collection of upcoming tenders.
   * @param upcomingTenderFileLinks the upcoming tenders to to remove all the file links from
   */
  public void removeUpcomingTenderFileLinks(List<UpcomingTender> upcomingTenderFileLinks) {
    upcomingTenderFileLinks.forEach(this::deleteAllFileLinksAndProjectDetailFilesLinkedToEntity);
  }

  /**
   * Retrieve a list of UploadedFileView objects for documents linked to an upcoming tender.
   * @param upcomingTender the upcoming tender to get the file views for
   * @return a list of UploadedFileView linked to an upcoming tender
   */
  public List<UploadedFileView> getFileUploadViewsLinkedToUpcomingTender(UpcomingTender upcomingTender) {
    return getFileUploadViewsLinkedEntity(upcomingTender);
  }

  @Override
  public ProjectDetailFilePurpose getFilePurpose() {
    return FILE_PURPOSE;
  }

  @Override
  public List<UpcomingTenderFileLink> findAllByFileLinkableEntity(EntityWithLinkedFile entityWithLinkedFile) {
    return upcomingTenderFileLinkRepository.findAllByUpcomingTender((UpcomingTender) entityWithLinkedFile);
  }

  @Override
  @Transactional
  public void deleteAllFileLinksLinkedToEntity(EntityWithLinkedFile entityWithLinkedFile) {
    var existingFileLinks = findAllByFileLinkableEntity(entityWithLinkedFile);
    upcomingTenderFileLinkRepository.deleteAll(existingFileLinks);
  }

  @Override
  public FileLinkEntity createFileLinkEntity(EntityWithLinkedFile entityWithLinkedFile,
                                             ProjectDetailFile projectDetailFile) {
    var upcomingTenderFileLink = new UpcomingTenderFileLink();
    upcomingTenderFileLink.setUpcomingTender((UpcomingTender) entityWithLinkedFile);
    upcomingTenderFileLink.setProjectDetailFile(projectDetailFile);
    return upcomingTenderFileLink;
  }

  @Override
  @Transactional
  public void saveFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var upcomingTenderFileLinks = mapFileLinkEntityList(fileLinkEntities);
    upcomingTenderFileLinkRepository.saveAll(upcomingTenderFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinks(List<? extends FileLinkEntity> fileLinkEntities) {
    var upcomingTenderFileLinks = mapFileLinkEntityList(fileLinkEntities);
    upcomingTenderFileLinkRepository.deleteAll(upcomingTenderFileLinks);
  }

  @Override
  @Transactional
  public void deleteFileLinkByProjectDetailFile(ProjectDetailFile projectDetailFile) {
    var upcomingTenderFileLink = upcomingTenderFileLinkRepository.findByProjectDetailFile(projectDetailFile)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "UpcomingTenderFileLink for project detail file with file id %s could not be found",
            projectDetailFile.getFileId()
        )));
    upcomingTenderFileLinkRepository.delete(upcomingTenderFileLink);
  }

  public void copyUpcomingTenderFileLinkData(ProjectDetail fromDetail,
                                             ProjectDetail toDetail,
                                             Map<UpcomingTender, UpcomingTender> duplicatedUpcomingTenderLookup) {

    // get a map of all the UpcomingTenderFileLink associated to the fromDetail
    var originalUpcomingTenderToFileLinkLookup = findAllByProjectDetail(fromDetail)
        .stream()
        .collect(Collectors.groupingBy(UpcomingTenderFileLink::getUpcomingTender));

    // duplicate any project detail file data associated to the fromDetail
    final var duplicatedProjectDetailLookup = copyProjectDetailFileData(
        fromDetail,
        toDetail,
        getFilePurpose()
    );

    originalUpcomingTenderToFileLinkLookup.forEach((originalUpcomingTender, originalUpcomingTenderFileLinks) -> {

      // for each UpcomingTender associated to the fromDetail, duplicate any UpcomingTenderFileLinks and reparent
      // to the duplicated UpcomingTender
      final var duplicatedUpcomingTenderFileLinks = entityDuplicationService.duplicateEntitiesAndSetNewParent(
          originalUpcomingTenderFileLinks,
          duplicatedUpcomingTenderLookup.get(originalUpcomingTender),
          UpcomingTenderFileLink.class
      );

      var upcomingTenderFileLinksToUpdate = new ArrayList<UpcomingTenderFileLink>();

      // for each UpcomingTenderFileLink, lookup the new duplicated ProjectDetailFile and reparent
      duplicatedUpcomingTenderFileLinks.forEach(duplicatedUpcomingTenderFileLink -> {
        final var duplicatedProjectDetailFile = duplicatedProjectDetailLookup.get(
            duplicatedUpcomingTenderFileLink.getOriginalEntity().getProjectDetailFile()
        );

        var upcomingTenderFileLink = duplicatedUpcomingTenderFileLink.getDuplicateEntity();
        upcomingTenderFileLink.setProjectDetailFile(duplicatedProjectDetailFile);
        upcomingTenderFileLinksToUpdate.add(upcomingTenderFileLink);

      });

      upcomingTenderFileLinkRepository.saveAll(upcomingTenderFileLinksToUpdate);

    });
  }

  private List<UpcomingTenderFileLink> mapFileLinkEntityList(List<? extends FileLinkEntity> fileLinkEntities) {
    return fileLinkEntities
        .stream()
        .map(fileLinkEntity -> (UpcomingTenderFileLink) fileLinkEntity)
        .collect(Collectors.toList());
  }

}
