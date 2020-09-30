package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.FileLinkStatus;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTender;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;
import uk.co.ogauthority.pathfinder.model.form.project.upcomingtender.UpcomingTenderForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;

@Service
public class UpcomingTenderFileLinkService {

  public static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.UPCOMING_TENDER;

  private final UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository;
  private final ProjectDetailFileService projectDetailFileService;

  @Autowired
  public UpcomingTenderFileLinkService(UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository,
                                       ProjectDetailFileService projectDetailFileService) {
    this.upcomingTenderFileLinkRepository = upcomingTenderFileLinkRepository;
    this.projectDetailFileService = projectDetailFileService;
  }

  /**
   * Retrieve all the UpcomingTenderFileLink entities linked to the provided upcoming tender.
   * @param upcomingTender the upcoming tender to retrieve the file links for
   * @return all the UpcomingTenderFileLink entities linked to the provided upcoming tender
   */
  protected List<UpcomingTenderFileLink> getAllByUpcomingTender(UpcomingTender upcomingTender) {
    return upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender);
  }

  /**
   * Update the UpcomingTenderFileLinks for the provided upcomingTender.
   * @param upcomingTender The upcoming tender entity to link to
   * @param form The form object which contains the files we need to link to
   * @param authenticatedUserAccount The logged in user
   */
  @Transactional
  protected void updateUpcomingTenderFileLinks(UpcomingTender upcomingTender,
                                               UpcomingTenderForm form,
                                               AuthenticatedUserAccount authenticatedUserAccount) {

    // clear any existing upcoming tender file links
    var existingLinks = upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender);
    upcomingTenderFileLinkRepository.deleteAll(existingLinks);

    // update the project detail files with files from from
    var persistedProjectDetailFileMap = projectDetailFileService.updateFiles(
        form,
        upcomingTender.getProjectDetail(),
        FILE_PURPOSE,
        FileUpdateMode.KEEP_UNLINKED_FILES,
        authenticatedUserAccount
    )
        .stream()
        .collect(Collectors.toMap(ProjectDetailFile::getFileId, file -> file));

    var upcomingTenderFileLinks = new ArrayList<UpcomingTenderFileLink>();

    // for each file in the form create a link to this upcoming tender
    form.getUploadedFileWithDescriptionForms()
        .forEach(uploadFileWithDescriptionForm -> {

          var projectDetailFile = persistedProjectDetailFileMap.get(uploadFileWithDescriptionForm.getUploadedFileId());

          var upcomingTenderFileLink = new UpcomingTenderFileLink();
          upcomingTenderFileLink.setUpcomingTender(upcomingTender);
          upcomingTenderFileLink.setProjectDetailFile(projectDetailFile);

          upcomingTenderFileLinks.add(upcomingTenderFileLink);

        });

    // bulk save all the upcoming tender links
    upcomingTenderFileLinkRepository.saveAll(upcomingTenderFileLinks);
  }

  /**
   * Remove an UpcomingTenderFileLink related to a ProjectDetailFile.
   * @param projectDetailFile the project detail file to identify the UpcomingTenderFileLink
   */
  @Transactional
  protected void removeUpcomingTenderFileLink(ProjectDetailFile projectDetailFile) {
    var upcomingTenderFileLink = upcomingTenderFileLinkRepository.findByProjectDetailFile(projectDetailFile)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "UpcomingTenderFileLink for project detail file with file id %s could not be found",
            projectDetailFile.getFileId()
        )));
    upcomingTenderFileLinkRepository.delete(upcomingTenderFileLink);
  }

  /**
   * Remove all upcoming tender file links associated to an upcoming tender.
   * @param upcomingTender the upcoming tender to to remove all the file links from
   */
  @Transactional
  protected void removeUpcomingTenderFileLinks(UpcomingTender upcomingTender) {

    // remove the upcoming tender file links
    var upcomingTenderFileLinks = upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender);
    upcomingTenderFileLinkRepository.deleteAll(upcomingTenderFileLinks);

    // remove the project detail files no longer required
    var projectDetailFiles = upcomingTenderFileLinks
        .stream()
        .map(UpcomingTenderFileLink::getProjectDetailFile)
        .collect(Collectors.toList());

    projectDetailFileService.removeProjectDetailFiles(projectDetailFiles);
  }

  /**
   * Retrieve a list of UploadedFileView objects for documents linked to an upcoming tender.
   * @param upcomingTender the upcoming tender to get the file views for
   * @return a list of UploadedFileView linked to an upcoming tender
   */
  public List<UploadedFileView> getFileUploadViewsLinkedToUpcomingTender(UpcomingTender upcomingTender) {
    return getAllByUpcomingTender(upcomingTender)
        .stream()
        .map(upcomingTenderFileLink ->
          projectDetailFileService.getUploadedFileView(
              upcomingTender.getProjectDetail(),
              upcomingTenderFileLink.getProjectDetailFile().getFileId(),
              UpcomingTenderFileLinkService.FILE_PURPOSE,
              FileLinkStatus.FULL
          )
        )
        .collect(Collectors.toList());
  }
}
