package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.infrastructure;

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
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.infrastructure.InfrastructureCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.DuplicatedEntityPairing;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.testutil.InfrastructureCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureCollaborationOpportunityFileLinkServiceTest {

  @Mock
  private InfrastructureCollaborationOpportunityFileLinkRepository infrastructureCollaborationOpportunityFileLinkRepository;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private InfrastructureCollaborationOpportunityFileLinkService infrastructureCollaborationOpportunityFileLinkService;

  @Before
  public void setup() {
    infrastructureCollaborationOpportunityFileLinkService = new InfrastructureCollaborationOpportunityFileLinkService(
        infrastructureCollaborationOpportunityFileLinkRepository,
        projectDetailFileService,
        entityDuplicationService
    );
  }

  @Test
  public void getAllByCollaborationOpportunity_whenLinks_thenReturnPopulatedList() {

    var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    var collaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity,
        new ProjectDetailFile()
    );

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var result = infrastructureCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity);

    assertThat(result).containsExactly(
        collaborationOpportunityFileLink
    );
  }

  @Test
  public void getAllByCollaborationOpportunity_whenNoLinks_thenReturnEmptyList() {

    var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of());

    var result = infrastructureCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity);

    assertThat(result).isEmpty();
  }

  @Test
  public void updateCollaborationOpportunityFileLinks() {

    var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    var form = InfrastructureCollaborationOpportunityTestUtil.getCompleteForm();
    var uploadedFile = UploadedFileUtil.createUploadFileWithDescriptionForm();
    form.setUploadedFileWithDescriptionForms(List.of(uploadedFile));

    var authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    var collaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var projectDetailFile = new ProjectDetailFile();
    projectDetailFile.setFileId(uploadedFile.getUploadedFileId());
    projectDetailFile.setDescription(uploadedFile.getUploadedFileDescription());

    when(projectDetailFileService.updateFiles(any(), any(), any(), any(), any())).thenReturn(List.of(projectDetailFile));

    infrastructureCollaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(collaborationOpportunity, form, authenticatedUser);

    verify(infrastructureCollaborationOpportunityFileLinkRepository).deleteAll(List.of(collaborationOpportunityFileLink));
    verify(projectDetailFileService).updateFiles(
        form,
        collaborationOpportunity.getProjectDetail(),
        InfrastructureCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        FileUpdateMode.KEEP_UNLINKED_FILES,
        authenticatedUser
    );

    verify(infrastructureCollaborationOpportunityFileLinkRepository).saveAll(anyList());

  }

  @Test
  public void removeCollaborationOpportunityFileLink_whenFound_thenDelete() {

    var collaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

    when(infrastructureCollaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile))
        .thenReturn(Optional.of(collaborationOpportunityFileLink));

    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(projectDetailFile);

    verify(infrastructureCollaborationOpportunityFileLinkRepository).delete(collaborationOpportunityFileLink);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void removeCollaborationOpportunityFileLink_whenNotFound_thenException() {

    var collaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

    when(infrastructureCollaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)).thenReturn(Optional.empty());

    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(projectDetailFile);

    verify(infrastructureCollaborationOpportunityFileLinkRepository, times(0)).delete(collaborationOpportunityFileLink);
  }

  @Test
  public void removeCollaborationOpportunityFileLinks_whenListOfLinks_allRemoved() {

    var collaborationOpportunityFileLink1 = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var collaborationOpportunityFileLink2 = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of(
          collaborationOpportunityFileLink1,
          collaborationOpportunityFileLink2
        )
    );

    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(collaborationOpportunity);

    verify(infrastructureCollaborationOpportunityFileLinkRepository).deleteAll(List.of(collaborationOpportunityFileLink1, collaborationOpportunityFileLink2));
    verify(projectDetailFileService).removeProjectDetailFiles(List.of(
        collaborationOpportunityFileLink1.getProjectDetailFile(),
        collaborationOpportunityFileLink2.getProjectDetailFile()
    ));
  }

  @Test
  public void removeCollaborationOpportunityFileLinks_whenListOfOpportunitiesWithLinks_thenAllRemoved() {

    var projectDetailFile1 = new ProjectDetailFile();
    projectDetailFile1.setId(1);
    final var collaborationOpportunity1 = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        ProjectUtil.getProjectDetails()
    );

    final var collaborationOpportunityFileLink1 = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity1,
        projectDetailFile1
    );

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity1)).thenReturn(
        List.of(collaborationOpportunityFileLink1)
    );

    var projectDetailFile2 = new ProjectDetailFile();
    projectDetailFile1.setId(2);
    final var collaborationOpportunity2 = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(
        ProjectUtil.getProjectDetails()
    );

    final var collaborationOpportunityFileLink2 = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity2,
        projectDetailFile2
    );

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity2)).thenReturn(
        List.of(collaborationOpportunityFileLink2)
    );

    infrastructureCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(
        List.of(collaborationOpportunity1, collaborationOpportunity2)
    );

    verify(infrastructureCollaborationOpportunityFileLinkRepository, times(1)).deleteAll(List.of(collaborationOpportunityFileLink1));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(collaborationOpportunityFileLink1.getProjectDetailFile()));
    verify(infrastructureCollaborationOpportunityFileLinkRepository, times(1)).deleteAll(List.of(collaborationOpportunityFileLink2));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(collaborationOpportunityFileLink2.getProjectDetailFile()));
  }

  @Test
  public void getFileUploadViewsLinkedToOpportunity_whenLinksForOpportunity_thenReturnPopulatedList() {
    var collaborationOpportunityFileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var uploadedFileView = UploadedFileUtil.createUploadedFileView();
    when(projectDetailFileService.getUploadedFileView(any(), any(), any(), any())).thenReturn(uploadedFileView);

    var fileViews = infrastructureCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(collaborationOpportunity);

    assertThat(fileViews).containsExactly(uploadedFileView);
  }

  @Test
  public void getFileUploadViewsLinkedToOpportunity_whenNoLinksForOpportunity_thenReturnEmptyList() {

    var collaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of());

    var fileViews = infrastructureCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(collaborationOpportunity);

    assertThat(fileViews).isEmpty();
  }

  @Test
  public void findAllByProjectDetail_whenResults_thenReturnPopulatedLists() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var fileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of(fileLink));

    final var results = infrastructureCollaborationOpportunityFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).containsExactly(fileLink);
  }

  @Test
  public void findAllByProjectDetail_whenNoResults_thenReturnEmptyLists() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of());

    final var results = infrastructureCollaborationOpportunityFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).isEmpty();

  }

  @Test
  public void copyCollaborationOpportunityFileLinkData_ensureDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromCollaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail);
    final var toCollaborationOpportunity = InfrastructureCollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail);

    final var fileLink = InfrastructureCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        fromCollaborationOpportunity,
        new ProjectDetailFile()
    );

    final var fileLinks = List.of(fileLink);

    when(infrastructureCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(fromProjectDetail))
        .thenReturn(fileLinks);

    when(entityDuplicationService.duplicateEntitiesAndSetNewParent(
        any(),
        any(),
        eq(InfrastructureCollaborationOpportunityFileLink.class)
    )).thenReturn(Set.of(new DuplicatedEntityPairing<>(fileLink, fileLink)));

    final var opportunityMap = Map.of(
        fromCollaborationOpportunity,
        toCollaborationOpportunity
    );

    infrastructureCollaborationOpportunityFileLinkService.copyCollaborationOpportunityFileLinkData(
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
        InfrastructureCollaborationOpportunityFileLink.class
    );

    verify(infrastructureCollaborationOpportunityFileLinkRepository, times(1)).saveAll(any());
  }
}