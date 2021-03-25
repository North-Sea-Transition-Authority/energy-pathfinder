package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFile;
import uk.co.ogauthority.pathfinder.model.entity.file.ProjectDetailFilePurpose;
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.CollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.CollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.DuplicatedEntityPairing;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
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

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private CollaborationOpportunityFileLinkService collaborationOpportunityFileLinkService;

  @Before
  public void setup() {
    collaborationOpportunityFileLinkService = new CollaborationOpportunityFileLinkService(
        collaborationOpportunityFileLinkRepository,
        projectDetailFileService,
        entityDuplicationService
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
  public void removeCollaborationOpportunityFileLinks_whenListOfOpportunitiesWithLinks_thenAllRemoved() {

    var projectDetailFile1 = new ProjectDetailFile();
    projectDetailFile1.setId(1);
    final var collaborationOpportunity1 = CollaborationOpportunityTestUtil.getCollaborationOpportunity(
        ProjectUtil.getProjectDetails()
    );

    final var collaborationOpportunityFileLink1 = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity1,
        projectDetailFile1
    );

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity1)).thenReturn(
        List.of(collaborationOpportunityFileLink1)
    );

    var projectDetailFile2 = new ProjectDetailFile();
    projectDetailFile1.setId(2);
    final var collaborationOpportunity2 = CollaborationOpportunityTestUtil.getCollaborationOpportunity(
        ProjectUtil.getProjectDetails()
    );

    final var collaborationOpportunityFileLink2 = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity2,
        projectDetailFile2
    );

    when(collaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity2)).thenReturn(
        List.of(collaborationOpportunityFileLink2)
    );

    collaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(
        List.of(collaborationOpportunity1, collaborationOpportunity2)
    );

    verify(collaborationOpportunityFileLinkRepository, times(1)).deleteAll(List.of(collaborationOpportunityFileLink1));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(collaborationOpportunityFileLink1.getProjectDetailFile()));
    verify(collaborationOpportunityFileLinkRepository, times(1)).deleteAll(List.of(collaborationOpportunityFileLink2));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(collaborationOpportunityFileLink2.getProjectDetailFile()));
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

  @Test
  public void findAllByProjectDetail_whenResults_thenReturnPopulatedLists() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var fileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    when(collaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of(fileLink));

    final var results = collaborationOpportunityFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).containsExactly(fileLink);
  }

  @Test
  public void findAllByProjectDetail_whenNoResults_thenReturnEmptyLists() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(collaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of());

    final var results = collaborationOpportunityFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).isEmpty();

  }

  @Test
  public void copyCollaborationOpportunityFileLinkData_ensureDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromCollaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail);
    final var toCollaborationOpportunity = CollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail);

    final var fileLink = CollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        fromCollaborationOpportunity,
        new ProjectDetailFile()
    );

    final var fileLinks = List.of(fileLink);

    when(collaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(fromProjectDetail))
        .thenReturn(fileLinks);

    when(entityDuplicationService.duplicateEntitiesAndSetNewParent(
        any(),
        any(),
        eq(CollaborationOpportunityFileLink.class)
    )).thenReturn(Set.of(new DuplicatedEntityPairing<>(fileLink, fileLink)));

    final var opportunityMap = Map.of(
        fromCollaborationOpportunity,
        toCollaborationOpportunity
    );

    collaborationOpportunityFileLinkService.copyCollaborationOpportunityFileLinkData(
        fromProjectDetail,
        toProjectDetail,
        opportunityMap
    );

    verify(projectDetailFileService, times(1)).copyProjectDetailFileData(
        fromProjectDetail,
        toProjectDetail,
        ProjectDetailFilePurpose.COLLABORATION_OPPORTUNITY
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        fileLinks,
        toCollaborationOpportunity,
        CollaborationOpportunityFileLink.class
    );

    verify(collaborationOpportunityFileLinkRepository, times(1)).saveAll(any());
  }
}