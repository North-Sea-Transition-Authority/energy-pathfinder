package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

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
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunity;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.form.project.collaborationopportunities.CollaborationOpportunityForm;
import uk.co.ogauthority.pathfinder.model.view.file.UploadedFileView;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;

@Service
public class CollaborationOpportunityFileLinkService {

  public static final ProjectDetailFilePurpose FILE_PURPOSE = ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY;

  private final CollaborationOpportunityFileLinkRepository collaborationOpportunityFileLinkRepository;
  private final ProjectDetailFileService projectDetailFileService;

  @Autowired
  public CollaborationOpportunityFileLinkService(
      CollaborationOpportunityFileLinkRepository collaborationOpportunityFileLinkRepository,
      ProjectDetailFileService projectDetailFileService
  ) {
    this.collaborationOpportunityFileLinkRepository = collaborationOpportunityFileLinkRepository;
    this.projectDetailFileService = projectDetailFileService;
  }

  @Transactional
  public void updateCollaborationOpportunityFileLinks(CollaborationOpportunity collaborationOpportunity,
                                                      CollaborationOpportunityForm form,
                                                      AuthenticatedUserAccount authenticatedUserAccount) {
    // clear any existing collaboration opportunity file links
    var existingLinks = collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(
        collaborationOpportunity
    );
    collaborationOpportunityFileLinkRepository.deleteAll(existingLinks);

    // update the project detail files with files from from
    var persistedProjectDetailFileMap = projectDetailFileService.updateFiles(
        form,
        collaborationOpportunity.getProjectDetail(),
        FILE_PURPOSE,
        FileUpdateMode.KEEP_UNLINKED_FILES,
        authenticatedUserAccount
    )
        .stream()
        .collect(Collectors.toMap(ProjectDetailFile::getFileId, file -> file));

    var collaborationOpportunityFileLinks = new ArrayList<CollaborationOpportunityFileLink>();

    // for each file in the form create a link to this collaboration opportunity
    form.getUploadedFileWithDescriptionForms()
        .forEach(uploadFileWithDescriptionForm -> {

          var projectDetailFile = persistedProjectDetailFileMap.get(
              uploadFileWithDescriptionForm.getUploadedFileId()
          );

          var collaborationOpportunityFileLink = new CollaborationOpportunityFileLink();
          collaborationOpportunityFileLink.setCollaborationOpportunity(collaborationOpportunity);
          collaborationOpportunityFileLink.setProjectDetailFile(projectDetailFile);

          collaborationOpportunityFileLinks.add(collaborationOpportunityFileLink);

        });

    // bulk save all the collaboration opportunity links
    collaborationOpportunityFileLinkRepository.saveAll(collaborationOpportunityFileLinks);

  }

  /**
   * Remove an CollaborationOpportunityFileLink related to a ProjectDetailFile.
   * @param projectDetailFile the project detail file to identify the CollaborationOpportunityFileLink
   */
  @Transactional
  protected void removeCollaborationOpportunityFileLink(ProjectDetailFile projectDetailFile) {
    var opportunityFileLink =
        collaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)
        .orElseThrow(() -> new PathfinderEntityNotFoundException(String.format(
            "CollaborationOpportunityFileLink for project detail file with file id %s could not be found",
            projectDetailFile.getFileId()
        )));
    collaborationOpportunityFileLinkRepository.delete(opportunityFileLink);
  }

  /**
   * Remove all collaboration opportunity file links associated to a collaboration opportunity.
   * @param collaborationOpportunity the collaboration opportunity to to remove all the file links from
   */
  @Transactional
  protected void removeCollaborationOpportunityFileLinks(CollaborationOpportunity collaborationOpportunity) {

    // remove the collaboration opportunity file links
    var collaborationOpportunityFileLinks =
        getAllByCollaborationOpportunity(collaborationOpportunity);
    collaborationOpportunityFileLinkRepository.deleteAll(collaborationOpportunityFileLinks);

    // remove the project detail files no longer required
    var projectDetailFiles = collaborationOpportunityFileLinks
        .stream()
        .map(CollaborationOpportunityFileLink::getProjectDetailFile)
        .collect(Collectors.toList());

    projectDetailFileService.removeProjectDetailFiles(projectDetailFiles);
  }

  /**
   * Retrieve all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity.
   * @param collaborationOpportunity the collaboration opportunity to retrieve the file links for
   * @return all the CollaborationOpportunityFileLink entities linked to the provided collaboration opportunity
   */
  protected List<CollaborationOpportunityFileLink> getAllByCollaborationOpportunity(
      CollaborationOpportunity collaborationOpportunity) {
    return collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity);
  }

  /**
   * Retrieve a list of UploadedFileView objects for documents linked to a collaboration opportunity.
   * @param collaborationOpportunity the collaboration opportunity to get the file views for
   * @return a list of UploadedFileView linked to an collaboration opportunity
   */
  public List<UploadedFileView> getFileUploadViewsLinkedToOpportunity(CollaborationOpportunity collaborationOpportunity) {
    return getAllByCollaborationOpportunity(collaborationOpportunity)
        .stream()
        .map(collaborationOpportunityFileLink ->
            projectDetailFileService.getUploadedFileView(
                collaborationOpportunity.getProjectDetail(),
                collaborationOpportunityFileLink.getProjectDetailFile().getFileId(),
                CollaborationOpportunityFileLinkService.FILE_PURPOSE,
                FileLinkStatus.FULL
            )
        )
        .collect(Collectors.toList());
  }

}
