package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.repository.project.ProjectDetailsRepository;
import uk.co.ogauthority.pathfinder.repository.projectupdate.ProjectUpdateRepository;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectUpdateServiceTest {

  @Mock
  private ProjectUpdateRepository projectUpdateRepository;

  @Mock
  private ProjectDetailsRepository projectDetailsRepository;

  @Mock
  private ProjectService projectService;

  @Mock
  private TestProjectFormSectionService testProjectFormSectionServiceA;

  @Mock
  private TestProjectFormSectionService testProjectFormSectionServiceB;

  private ProjectUpdateService projectUpdateService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectUpdateService = new ProjectUpdateService(
        projectUpdateRepository,
        projectDetailsRepository,
        projectService,
        List.of(testProjectFormSectionServiceA, testProjectFormSectionServiceB)
    );

    when(projectUpdateRepository.save(any(ProjectUpdate.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
  }

  @Test
  public void createNewProjectVersion_whenAllShownInTaskListAndNoneAlwaysCopy_verifyServiceInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(testProjectFormSectionServiceA.canShowInTaskList(fromProjectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceA.alwaysCopySectionData(fromProjectDetail)).thenReturn(false);

    when(testProjectFormSectionServiceB.canShowInTaskList(fromProjectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.alwaysCopySectionData(fromProjectDetail)).thenReturn(false);

    projectUpdateService.createNewProjectVersion(fromProjectDetail, ProjectStatus.DRAFT, user);

    verify(projectService, times(1)).createNewProjectDetailVersion(fromProjectDetail, ProjectStatus.DRAFT, user);
    verify(testProjectFormSectionServiceA, times(1)).copySectionData(eq(fromProjectDetail), any());
    verify(testProjectFormSectionServiceB, times(1)).copySectionData(eq(fromProjectDetail), any());
  }

  @Test
  public void createNewProjectVersion_whenNotAllShownInTaskListAndNoneAlwaysCopy_verifyServiceInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(testProjectFormSectionServiceA.canShowInTaskList(fromProjectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceA.alwaysCopySectionData(fromProjectDetail)).thenReturn(false);

    when(testProjectFormSectionServiceB.canShowInTaskList(fromProjectDetail)).thenReturn(false);
    when(testProjectFormSectionServiceB.alwaysCopySectionData(fromProjectDetail)).thenReturn(false);

    projectUpdateService.createNewProjectVersion(fromProjectDetail, ProjectStatus.DRAFT, user);

    verify(projectService, times(1)).createNewProjectDetailVersion(fromProjectDetail, ProjectStatus.DRAFT, user);
    verify(testProjectFormSectionServiceA, times(1)).copySectionData(eq(fromProjectDetail), any());
    verify(testProjectFormSectionServiceB, never()).copySectionData(eq(fromProjectDetail), any());
  }

  @Test
  public void createNewProjectVersion_whenNoneShownInTaskListAndAllAlwaysCopy_verifyServiceInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(testProjectFormSectionServiceA.alwaysCopySectionData(fromProjectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.alwaysCopySectionData(fromProjectDetail)).thenReturn(true);

    projectUpdateService.createNewProjectVersion(fromProjectDetail, ProjectStatus.DRAFT, user);

    verify(projectService, times(1)).createNewProjectDetailVersion(fromProjectDetail, ProjectStatus.DRAFT, user);
    verify(testProjectFormSectionServiceA, times(1)).copySectionData(eq(fromProjectDetail), any());
    verify(testProjectFormSectionServiceB, times(1)).copySectionData(eq(fromProjectDetail), any());
  }

  @Test
  public void createNewProjectVersion_whenSomeShownInTaskListAndSomeAlwaysCopy_verifyServiceInteractions() {

    final var fromProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
    final var user = UserTestingUtil.getAuthenticatedUserAccount();

    when(testProjectFormSectionServiceA.alwaysCopySectionData(fromProjectDetail)).thenReturn(true);

    when(testProjectFormSectionServiceB.canShowInTaskList(fromProjectDetail)).thenReturn(true);
    when(testProjectFormSectionServiceB.alwaysCopySectionData(fromProjectDetail)).thenReturn(false);

    projectUpdateService.createNewProjectVersion(fromProjectDetail, ProjectStatus.DRAFT, user);

    verify(projectService, times(1)).createNewProjectDetailVersion(fromProjectDetail, ProjectStatus.DRAFT, user);
    verify(testProjectFormSectionServiceA, times(1)).copySectionData(eq(fromProjectDetail), any());
    verify(testProjectFormSectionServiceB, times(1)).copySectionData(eq(fromProjectDetail), any());
  }

  @Test
  public void startUpdate() {
    when(projectService.createNewProjectDetailVersion(projectDetail, ProjectStatus.DRAFT, authenticatedUser)).thenReturn(
        ProjectUtil.getProjectDetails()
    );

    var projectUpdate = projectUpdateService.startUpdate(projectDetail, authenticatedUser, ProjectUpdateType.OPERATOR_INITIATED);
    assertThat(projectUpdate.getFromDetail()).isEqualTo(projectDetail);
    assertThat(projectUpdate.getToDetail()).isNotNull();
    assertThat(projectUpdate.getUpdateType()).isEqualTo(ProjectUpdateType.OPERATOR_INITIATED);
    verify(projectUpdateRepository, times(1)).save(projectUpdate);
  }

  @Test
  public void deleteProjectUpdate() {
    var projectUpdate = new ProjectUpdate();

    projectUpdateService.deleteProjectUpdate(projectUpdate);

    verify(projectUpdateRepository, times(1)).delete(projectUpdate);
  }

  @Test
  public void isUpdateInProgress_whenUpdateNotInProgress() {
    when(projectDetailsRepository.isProjectUpdateInProgress(project.getId())).thenReturn(false);

    assertThat(projectUpdateService.isUpdateInProgress(project)).isFalse();
  }

  @Test
  public void isUpdateInProgress_whenUpdateInProgress() {
    when(projectDetailsRepository.isProjectUpdateInProgress(project.getId())).thenReturn(true);

    assertThat(projectUpdateService.isUpdateInProgress(project)).isTrue();
  }

  @Test
  public void getByToDetail_whenExists() {
    var update = new ProjectUpdate();
    when(projectUpdateRepository.findByToDetail(projectDetail)).thenReturn(Optional.of(update));
    var returnedUpdate = projectUpdateService.getByToDetail(projectDetail);
    assertThat(returnedUpdate).contains(update);
  }

  @Test
  public void getByToDetail_notFound() {
    when(projectUpdateRepository.findByToDetail(projectDetail)).thenReturn(Optional.empty());
    assertThat(projectUpdateService.getByToDetail(projectDetail)).isEmpty();
  }
}
