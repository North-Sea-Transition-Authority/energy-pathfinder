package uk.co.ogauthority.pathfinder.service.project.upcomingtender;

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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.upcomingtender.UpcomingTenderFileLink;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.repository.project.upcomingtender.UpcomingTenderFileLinkRepository;
import uk.co.ogauthority.pathfinder.service.entityduplication.DuplicatedEntityPairing;
import uk.co.ogauthority.pathfinder.service.entityduplication.EntityDuplicationService;
import uk.co.ogauthority.pathfinder.service.file.FileUpdateMode;
import uk.co.ogauthority.pathfinder.service.file.ProjectDetailFileService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderFileLinkUtil;
import uk.co.ogauthority.pathfinder.testutil.UpcomingTenderUtil;
import uk.co.ogauthority.pathfinder.testutil.UploadedFileUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingTenderFileLinkServiceTest {

  @Mock
  private UpcomingTenderFileLinkRepository upcomingTenderFileLinkRepository;

  @Mock
  private ProjectDetailFileService projectDetailFileService;

  @Mock
  private EntityDuplicationService entityDuplicationService;

  private UpcomingTenderFileLinkService upcomingTenderFileLinkService;

  @Before
  public void setup() {
    upcomingTenderFileLinkService = new UpcomingTenderFileLinkService(
        upcomingTenderFileLinkRepository,
        projectDetailFileService,
        entityDuplicationService
    );
  }

  @Test
  public void getAllByUpcomingTender_whenLinks_thenReturnPopulatedList() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(ProjectUtil.getProjectDetails());

    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(
        upcomingTender,
        new ProjectDetailFile()
    );

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender)).thenReturn(List.of(upcomingTenderFileLink));

    var result = upcomingTenderFileLinkService.getAllByUpcomingTender(upcomingTender);

    assertThat(result).containsExactly(
        upcomingTenderFileLink
    );
  }

  @Test
  public void getAllByUpcomingTender_whenNoLinks_thenReturnEmptyList() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(ProjectUtil.getProjectDetails());

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender)).thenReturn(List.of());

    var result = upcomingTenderFileLinkService.getAllByUpcomingTender(upcomingTender);

    assertThat(result).isEmpty();
  }

  @Test
  public void updateUpcomingTenderFileLinks() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(new ProjectDetail());

    var form = UpcomingTenderUtil.getCompleteForm();
    var uploadedFile = UploadedFileUtil.createUploadFileWithDescriptionForm();
    form.setUploadedFileWithDescriptionForms(List.of(uploadedFile));

    var authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();
    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender)).thenReturn(List.of(upcomingTenderFileLink));

    var projectDetailFile = new ProjectDetailFile();
    projectDetailFile.setFileId(uploadedFile.getUploadedFileId());
    projectDetailFile.setDescription(uploadedFile.getUploadedFileDescription());

    when(projectDetailFileService.updateFiles(any(), any(), any(), any(), any())).thenReturn(List.of(projectDetailFile));

    upcomingTenderFileLinkService.updateUpcomingTenderFileLinks(upcomingTender, form, authenticatedUser);

    verify(upcomingTenderFileLinkRepository).deleteAll(List.of(upcomingTenderFileLink));
    verify(projectDetailFileService).updateFiles(
        form,
        upcomingTender.getProjectDetail(),
        UpcomingTenderFileLinkService.FILE_PURPOSE,
        FileUpdateMode.KEEP_UNLINKED_FILES,
        authenticatedUser
    );

    verify(upcomingTenderFileLinkRepository).saveAll(anyList());

  }

  @Test
  public void removeUpcomingTenderFileLink_whenFound_thenDelete() {

    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();
    var projectDetailFile = upcomingTenderFileLink.getProjectDetailFile();

    when(upcomingTenderFileLinkRepository.findByProjectDetailFile(projectDetailFile)).thenReturn(Optional.of(upcomingTenderFileLink));

    upcomingTenderFileLinkService.removeUpcomingTenderFileLink(projectDetailFile);

    verify(upcomingTenderFileLinkRepository).delete(upcomingTenderFileLink);
  }

  @Test(expected = PathfinderEntityNotFoundException.class)
  public void removeUpcomingTenderFileLink_whenNotFound_thenException() {

    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();
    var projectDetailFile = upcomingTenderFileLink.getProjectDetailFile();

    when(upcomingTenderFileLinkRepository.findByProjectDetailFile(projectDetailFile)).thenReturn(Optional.empty());

    upcomingTenderFileLinkService.removeUpcomingTenderFileLink(projectDetailFile);

    verify(upcomingTenderFileLinkRepository, times(0)).delete(upcomingTenderFileLink);
  }

  @Test
  public void removeUpcomingTenderFileLinks_whenListOfLinks_allRemoved() {

    var upcomingTenderFileLink1 = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();
    var upcomingTenderFileLink2 = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(ProjectUtil.getProjectDetails());

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender)).thenReturn(List.of(
        upcomingTenderFileLink1,
        upcomingTenderFileLink2)
    );

    upcomingTenderFileLinkService.removeUpcomingTenderFileLinks(upcomingTender);

    verify(upcomingTenderFileLinkRepository).deleteAll(List.of(upcomingTenderFileLink1, upcomingTenderFileLink2));
    verify(projectDetailFileService).removeProjectDetailFiles(List.of(
        upcomingTenderFileLink1.getProjectDetailFile(),
        upcomingTenderFileLink2.getProjectDetailFile()
    ));
  }

  @Test
  public void getFileUploadViewsLinkedToUpcomingTender_whenLinksForTender_thenReturnPopulatedList() {
    var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();
    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(ProjectUtil.getProjectDetails());

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender)).thenReturn(List.of(upcomingTenderFileLink));

    var uploadedFileView = UploadedFileUtil.createUploadedFileView();
    when(projectDetailFileService.getUploadedFileView(any(), any(), any(), any())).thenReturn(uploadedFileView);

    var fileViews = upcomingTenderFileLinkService.getFileUploadViewsLinkedToUpcomingTender(upcomingTender);

    assertThat(fileViews).containsExactly(uploadedFileView);
  }

  @Test
  public void getFileUploadViewsLinkedToUpcomingTender_whenNoLinksForTender_thenReturnEmptyList() {

    var upcomingTender = UpcomingTenderUtil.getUpcomingTender(ProjectUtil.getProjectDetails());

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender)).thenReturn(List.of());

    var fileViews = upcomingTenderFileLinkService.getFileUploadViewsLinkedToUpcomingTender(upcomingTender);

    assertThat(fileViews).isEmpty();
  }

  @Test
  public void removeUpcomingTenderFileLinks_whenListOfTendersWithLinks_thenAllRemoved() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    var projectDetailFile1 = new ProjectDetailFile();
    projectDetailFile1.setId(1);

    final var upcomingTender1 = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender1.setContactName("upcomingTender1");

    final var upcomingTenderFileLink1 = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(
        upcomingTender1,
        projectDetailFile1
    );

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender1)).thenReturn(
        List.of(upcomingTenderFileLink1)
    );

    var projectDetailFile2 = new ProjectDetailFile();
    projectDetailFile1.setId(2);

    final var upcomingTender2 = UpcomingTenderUtil.getUpcomingTender(projectDetail);
    upcomingTender2.setContactName("upcomingTender2");

    final var upcomingTenderFileLink2 = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(
        upcomingTender2,
        projectDetailFile2
    );

    when(upcomingTenderFileLinkRepository.findAllByUpcomingTender(upcomingTender2)).thenReturn(
        List.of(upcomingTenderFileLink2)
    );

    upcomingTenderFileLinkService.removeUpcomingTenderFileLinks(List.of(upcomingTender1, upcomingTender2));

    verify(upcomingTenderFileLinkRepository, times(1)).deleteAll(List.of(upcomingTenderFileLink1));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(upcomingTenderFileLink1.getProjectDetailFile()));
    verify(upcomingTenderFileLinkRepository, times(1)).deleteAll(List.of(upcomingTenderFileLink2));
    verify(projectDetailFileService, times(1)).removeProjectDetailFiles(List.of(upcomingTenderFileLink2.getProjectDetailFile()));
  }

  @Test
  public void findAllByProjectDetail_whenResults_thenReturnPopulatedList() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var upcomingTenderFileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink();

    when(upcomingTenderFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of(upcomingTenderFileLink));

    var results = upcomingTenderFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).containsExactly(upcomingTenderFileLink);
  }

  @Test
  public void findAllByProjectDetail_whenNoResults_thenReturnEmptyList() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(upcomingTenderFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(projectDetail))
        .thenReturn(List.of());

    var results = upcomingTenderFileLinkService.findAllByProjectDetail(projectDetail);

    assertThat(results).isEmpty();
  }

  @Test
  public void copyUpcomingTenderFileLinkData_ensureDuplicationServiceInteraction() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var toProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    final var fromUpcomingTender = UpcomingTenderUtil.getUpcomingTender(fromProjectDetail);
    final var toUpcomingTender = UpcomingTenderUtil.getUpcomingTender(fromProjectDetail);

    final var fileLink = UpcomingTenderFileLinkUtil.createUpcomingTenderFileLink(
        fromUpcomingTender,
        new ProjectDetailFile()
    );

    final var fileLinks = List.of(fileLink);

    when(upcomingTenderFileLinkRepository.findAllByProjectDetailFile_ProjectDetail(fromProjectDetail))
        .thenReturn(fileLinks);

    when(entityDuplicationService.duplicateEntitiesAndSetNewParent(
        any(),
        any(),
        eq(UpcomingTenderFileLink.class)
    )).thenReturn(Set.of(new DuplicatedEntityPairing<>(fileLink, fileLink)));

    final var upcomingTenderMap = Map.of(
        fromUpcomingTender,
        toUpcomingTender
    );

    upcomingTenderFileLinkService.copyUpcomingTenderFileLinkData(
        fromProjectDetail,
        toProjectDetail,
        upcomingTenderMap
    );

    verify(projectDetailFileService, times(1)).copyProjectDetailFileData(
        fromProjectDetail,
        toProjectDetail,
        ProjectDetailFilePurpose.UPCOMING_TENDER
    );

    verify(entityDuplicationService, times(1)).duplicateEntitiesAndSetNewParent(
        fileLinks,
        toUpcomingTender,
        UpcomingTenderFileLink.class
    );

    verify(upcomingTenderFileLinkRepository, times(1)).saveAll(any());

  }
}