package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.testutil.CollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CollaborationOpportunityFileLinkServiceTest {

  @Mock
  private CollaborationOpportunityFileLinkRepository collaborationOpportunityFileLinkRepository;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  private CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService;

  @Before
  public void setup() {
    collaborationOpportunityFileLinkService = new CollaborationOpportunityFileLinkService(
        collaborationOpportunityFileLinkRepository,
        projectDetailFileService
    );
  }

  @Test
  public void getAllByCollaborationOpportunity_whenLinks_thenReturnPopulatedList() {

    var collaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    var collaborationOpportunityFileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity,
        new ProjectDetailFile()
    );

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var result = collaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity);

    assertThat(result).containsExactly(
        collaborationOpportunityFileLink
    );
  }

  @Test
  public void getAllByCollaborationOpportunity_whenNoLinks_thenReturnEmptyList() {

    var collaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of());

    var result = collaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity);

    assertThat(result).isEmpty();
  }

  @Test
  public void updateCollaborationOpportunityFileLinks() {

    var collaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    var form = CollaborationOpportunityTestUtil.getCompleteForm();
    var uploadedFile = UploadedFileUtil.createUploadFileWithDescriptionForm();
    form.setUploadedFileWithDescriptionForms(List.of(uploadedFile));

    var authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    var collaborationOpportunityFileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var projectDetailFile = new ProjectDetailFile();
    projectDetailFile.setFileId(uploadedFile.getUploadedFileId());
    projectDetailFile.setDescription(uploadedFile.getUploadedFileDescription());

    when(projectDetailFileService.updateFiles(any(), any(), any(), any(), any())).thenReturn(List.of(projectDetailFile));

    collaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(collaborationOpportunity, form, authenticatedUser);

    verify(collaborationOpportunityFileLinkRepository).deleteAll(List.of(collaborationOpportunityFileLink));
    verify(projectDetailFileService).updateFiles(
        form,
        collaborationOpportunity.getProjectDetail(),
        CollaborationOpportunityFileLinkService.FILE_PURPOSE,
        FileUpdateMode.KEEP_UNLINKED_FILES,
        authenticatedUser
    );

    verify(collaborationOpportunityFileLinkRepository).saveAll(anyList());

  }

  @Test
  public void removeCollaborationOpportunityFileLink_whenFound_thenDelete() {

    var collaborationOpportunityFileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

    when(collaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile))
        .thenReturn(Optional.of(collaborationOpportunityFileLink));

    collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(projectDetailFile);

    verify(collaborationOpportunityFileLinkRepository).delete(collaborationOpportunityFileLink);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void removeCollaborationOpportunityFileLink_whenNotFound_thenException() {

    var collaborationOpportunityFileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

    when(collaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)).thenReturn(Optional.empty());

    collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(projectDetailFile);

    verify(collaborationOpportunityFileLinkRepository, times(0)).delete(collaborationOpportunityFileLink);
  }

  @Test
  public void removeCollaborationOpportunityFileLinks_whenListOfLinks_allRemoved() {

    var collaborationOpportunityFileLink1 = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var collaborationOpportunityFileLink2 = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    var collaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of(
          collaborationOpportunityFileLink1,
          collaborationOpportunityFileLink2
        )
    );

    collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(collaborationOpportunity);

    verify(collaborationOpportunityFileLinkRepository).deleteAll(List.of(collaborationOpportunityFileLink1, collaborationOpportunityFileLink2));
    verify(projectDetailFileService).removeProjectDetailFiles(List.of(
        collaborationOpportunityFileLink1.getProjectDetailFile(),
        collaborationOpportunityFileLink2.getProjectDetailFile()
    ));
  }

  @Test
  public void getFileUploadViewsLinkedToOpportunity_whenLinksForOpportunity_thenReturnPopulatedList() {
    var collaborationOpportunityFileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var collaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var uploadedFileView = UploadedFileUtil.createUploadedFileView();
    when(projectDetailFileService.getUploadedFileView(any(), any(), any(), any())).thenReturn(uploadedFileView);

    var fileViews = collaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(collaborationOpportunity);

    assertThat(fileViews).containsExactly(uploadedFileView);
  }

  @Test
  public void getFileUploadViewsLinkedToOpportunity_whenNoLinksForOpportunity_thenReturnEmptyList() {

    var collaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of());

    var fileViews = collaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(collaborationOpportunity);

    assertThat(fileViews).isEmpty();
  }
}