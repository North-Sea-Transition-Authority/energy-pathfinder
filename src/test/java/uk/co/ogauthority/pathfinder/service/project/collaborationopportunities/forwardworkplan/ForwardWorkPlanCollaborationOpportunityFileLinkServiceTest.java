package uk.co.ogauthority.pathfinder.service.project.collaborationopportunities.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.model.entity.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.collaborationopportunities.forwardworkplan.ForwardWorkPlanCollaborationOpportunityFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.DuplicatedEntityPairing;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.testutil.ForwardWorkPlanCollaborationOpportunityTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanCollaborationOpportunityFileLinkServiceTest {

  @Mock
  private ForwardWorkPlanCollaborationOpportunityFileLinkRepository forwardWorkPlanCollaborationOpportunityFileLinkRepository;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private ForwardWorkPlanCollaborationOpportunityFileLinkService forwardWorkPlanCollaborationOpportunityFileLinkService;

  @Before
  public void setup() {
    forwardWorkPlanCollaborationOpportunityFileLinkService = new ForwardWorkPlanCollaborationOpportunityFileLinkService(
        forwardWorkPlanCollaborationOpportunityFileLinkRepository,
        projectDetailFileService,
        entityDuplicationService
    );
  }

  @Test
  public void getAllByCollaborationOpportunity_whenLinks_thenReturnPopulatedList() {

    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        ProjectUtil.getProjectDetails());

    var collaborationOpportunityFileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity,
        new ProjectDetailFile()
    );

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var result = forwardWorkPlanCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity);

    assertThat(result).containsExactly(
        collaborationOpportunityFileLink
    );
  }

  @Test
  public void getAllByCollaborationOpportunity_whenNoLinks_thenReturnEmptyList() {

    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of());

    var result = forwardWorkPlanCollaborationOpportunityFileLinkService.getAllByCollaborationOpportunity(collaborationOpportunity);

    assertThat(result).isEmpty();
  }

  @Test
  public void updateCollaborationOpportunityFileLinks() {

    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    var form = ForwardWorkPlanCollaborationOpportunityTestUtil.getCompleteForm();
    var uploadedFile = UploadedFileUtil.createUploadFileWithDescriptionForm();
    form.setUploadedFileWithDescriptionForms(List.of(uploadedFile));

    var authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    var collaborationOpportunityFileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var projectDetailFile = new ProjectDetailFile();
    projectDetailFile.setUploadedFile(UploadedFileUtil.createUploadedFile(uploadedFile.getUploadedFileId()));
    projectDetailFile.setDescription(uploadedFile.getUploadedFileDescription());

    when(projectDetailFileService.updateFiles(any(), any(), any(), any(), any())).thenReturn(List.of(projectDetailFile));

    forwardWorkPlanCollaborationOpportunityFileLinkService.updateCollaborationOpportunityFileLinks(collaborationOpportunity, form, authenticatedUser);

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository).deleteAll(List.of(collaborationOpportunityFileLink));
    verify(projectDetailFileService).updateFiles(
        form,
        collaborationOpportunity.getProjectDetail(),
        ForwardWorkPlanCollaborationOpportunityFileLinkService.FILE_PURPOSE,
        FileUpdateMode.KEEP_UNLINKED_FILES,
        authenticatedUser
    );

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository).saveAll(anyList());

  }

  @Test
  public void removeCollaborationOpportunityFileLink_whenFound_thenDelete() {

    var collaborationOpportunityFileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile))
        .thenReturn(Optional.of(collaborationOpportunityFileLink));

    forwardWorkPlanCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(projectDetailFile);

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository).delete(collaborationOpportunityFileLink);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void removeCollaborationOpportunityFileLink_whenNotFound_thenException() {

    var collaborationOpportunityFileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var projectDetailFile = collaborationOpportunityFileLink.getProjectDetailFile();

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findByProjectDetailFile(projectDetailFile)).thenReturn(Optional.empty());

    forwardWorkPlanCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLink(projectDetailFile);

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository, times(0)).delete(collaborationOpportunityFileLink);
  }

  @Test
  public void removeCollaborationOpportunityFileLinks_whenListOfLinks_allRemoved() {

    var collaborationOpportunityFileLink1 = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var collaborationOpportunityFileLink2 = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of(
        collaborationOpportunityFileLink1,
        collaborationOpportunityFileLink2
        )
    );

    forwardWorkPlanCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(collaborationOpportunity);

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository).deleteAll(List.of(collaborationOpportunityFileLink1, collaborationOpportunityFileLink2));
    verify(projectDetailFileService).removeProjectDetailFiles(List.of(
        collaborationOpportunityFileLink1.getProjectDetailFile(),
        collaborationOpportunityFileLink2.getProjectDetailFile()
    ));
  }

  @Test
  public void removeCollaborationOpportunityFileLinks_whenListOfOpportunitiesWithLinks_thenAllRemoved() {

    var projectDetailFile1 = new ProjectDetailFile();
    projectDetailFile1.setId(1);
    final var collaborationOpportunity1 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(
        ProjectUtil.getProjectDetails()
    );

    final var collaborationOpportunityFileLink1 = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity1,
        projectDetailFile1
    );

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity1)).thenReturn(
        List.of(collaborationOpportunityFileLink1)
    );

    var projectDetailFile2 = new ProjectDetailFile();
    projectDetailFile1.setId(2);
    final var collaborationOpportunity2 = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity_manualEntry(
        ProjectUtil.getProjectDetails()
    );

    final var collaborationOpportunityFileLink2 = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        collaborationOpportunity2,
        projectDetailFile2
    );

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity2)).thenReturn(
        List.of(collaborationOpportunityFileLink2)
    );

    forwardWorkPlanCollaborationOpportunityFileLinkService.removeCollaborationOpportunityFileLinks(
        List.of(collaborationOpportunity1, collaborationOpportunity2)
    );

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository, times(1)).deleteAll(List.of(collaborationOpportunityFileLink1));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(collaborationOpportunityFileLink1.getProjectDetailFile()));
    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository, times(1)).deleteAll(List.of(collaborationOpportunityFileLink2));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(collaborationOpportunityFileLink2.getProjectDetailFile()));
  }

  @Test
  public void getFileUploadViewsLinkedToOpportunity_whenLinksForOpportunity_thenReturnPopulatedList() {
    var collaborationOpportunityFileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();
    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity))
        .thenReturn(List.of(collaborationOpportunityFileLink));

    var uploadedFileView = UploadedFileUtil.createUploadedFileView();
    when(projectDetailFileService.getUploadedFileView(any(), any(), any(), any())).thenReturn(uploadedFileView);

    var fileViews = forwardWorkPlanCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(collaborationOpportunity);

    assertThat(fileViews).containsExactly(uploadedFileView);
  }

  @Test
  public void getFileUploadViewsLinkedToOpportunity_whenNoLinksForOpportunity_thenReturnEmptyList() {

    var collaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(ProjectUtil.getProjectDetails());

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByCollaborationOpportunity(collaborationOpportunity)).thenReturn(List.of());

    var fileViews = forwardWorkPlanCollaborationOpportunityFileLinkService.getFileUploadViewsLinkedToOpportunity(collaborationOpportunity);

    assertThat(fileViews).isEmpty();
  }

  @Test
  public void findAllByProjectDetail_whenResults_thenReturnPopulatedLists() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var fileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink();

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of(fileLink));

    final var results = forwardWorkPlanCollaborationOpportunityFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).containsExactly(fileLink);
  }

  @Test
  public void findAllByProjectDetail_whenNoResults_thenReturnEmptyLists() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of());

    final var results = forwardWorkPlanCollaborationOpportunityFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).isEmpty();

  }

  @Test
  public void copyCollaborationOpportunityFileLinkData_ensureDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromCollaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail);
    final var toCollaborationOpportunity = ForwardWorkPlanCollaborationOpportunityTestUtil.getCollaborationOpportunity(fromProjectDetail);

    final var fileLink = ForwardWorkPlanCollaborationOpportunityTestUtil.createCollaborationOpportunityFileLink(
        fromCollaborationOpportunity,
        new ProjectDetailFile()
    );

    final var fileLinks = List.of(fileLink);

    when(forwardWorkPlanCollaborationOpportunityFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(fromProjectDetail))
        .thenReturn(fileLinks);

    when(entityDuplicationService.duplicateEntitiesAndSetNewParent(
        any(),
        any(),
        eq(ForwardWorkPlanCollaborationOpportunityFileLink.class)
    )).thenReturn(Set.of(new DuplicatedEntityPairing<>(fileLink, fileLink)));

    final var opportunityMap = Map.of(
        fromCollaborationOpportunity,
        toCollaborationOpportunity
    );

    forwardWorkPlanCollaborationOpportunityFileLinkService.copyCollaborationOpportunityFileLinkData(
        fromProjectDetail,
        toProjectDetail,
        opportunityMap
    );

    verify(projectDetailFileService, times(1)).copyProjectDetailFileData(
        fromProjectDetail,
        toProjectDetail,
        ProjectDetailFilePurpose.WORK_PLAN_COLLABORATION_OPPORTUNITY
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        fileLinks,
        toCollaborationOpportunity,
        ForwardWorkPlanCollaborationOpportunityFileLink.class
    );

    verify(forwardWorkPlanCollaborationOpportunityFileLinkRepository, times(1)).saveAll(any());
  }

}