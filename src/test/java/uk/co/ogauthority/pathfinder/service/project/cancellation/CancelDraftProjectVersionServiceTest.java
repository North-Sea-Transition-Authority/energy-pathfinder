package uk.co.ogauthority.pathfinder.service.project.cancellation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.exception.CancelDraftProjectException;
import uk.co.ogauthority.pathfinder.exception.CancelProjectVersionImplementationException;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.TestProjectFormSectionService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryRenderingService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CancelDraftProjectVersionServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private ProjectSummaryRenderingService projectSummaryRenderingService;

  @Mock
  private TestProjectFormSectionService testProjectFormSectionServiceA;

  @Mock
  private TestProjectFormSectionService testProjectFormSectionServiceB;

  @Mock
  private TestCancelProjectVersionService testCancelProjectVersionService;

  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    cancelDraftProjectVersionService = new CancelDraftProjectVersionService(
        projectService,
        projectUpdateService,
        projectSummaryRenderingService,
        List.of(testProjectFormSectionServiceA, testProjectFormSectionServiceB),
        List.of(testCancelProjectVersionService)
    );

    when(testCancelProjectVersionService.getSupportedProjectType()).thenReturn(projectDetail.getProjectType());
    when(testCancelProjectVersionService.isCancellable(projectDetail)).thenReturn(true);
  }

  @Test
  public void cancelDraft_whenFirstVersion_andSupportedServiceAndCancellable_verifyInteractions() {

    projectDetail.setVersion(1);
    projectDetail.setProjectType(ProjectType.INFRASTRUCTURE);

    when(testProjectFormSectionServiceA.getSupportedProjectTypes()).thenReturn(Set.of(ProjectType.INFRASTRUCTURE));
    when(testProjectFormSectionServiceB.getSupportedProjectTypes()).thenReturn(Set.of(ProjectType.FORWARD_WORK_PLAN));

    cancelDraftProjectVersionService.cancelDraft(projectDetail);

    verify(testProjectFormSectionServiceA, times(1)).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionData(projectDetail);

    verify(projectUpdateService, never()).getByToDetail(projectDetail);

    verify(projectService, times(1)).deleteProjectDetail(projectDetail);
    verify(projectService, times(1)).deleteProject(project);
  }

  @Test
  public void cancelDraft_whenUpdate_andSupportedServiceAndCancellable_verifyInteractions() {
    projectDetail.setVersion(2);
    projectDetail.setProjectType(ProjectType.INFRASTRUCTURE);

    when(testProjectFormSectionServiceA.getSupportedProjectTypes()).thenReturn(Set.of(ProjectType.INFRASTRUCTURE));
    when(testProjectFormSectionServiceB.getSupportedProjectTypes()).thenReturn(Set.of(ProjectType.FORWARD_WORK_PLAN));

    var projectUpdate = new ProjectUpdate();
    projectUpdate.setUpdateType(ProjectUpdateType.OPERATOR_INITIATED);

    var fromDetail = ProjectUtil.getProjectDetails();
    projectUpdate.setFromDetail(fromDetail);

    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.of(projectUpdate));

    cancelDraftProjectVersionService.cancelDraft(projectDetail);

    verify(testProjectFormSectionServiceA, times(1)).removeSectionData(projectDetail);
    verify(testProjectFormSectionServiceB, never()).removeSectionData(projectDetail);

    verify(projectUpdateService, times(1)).deleteProjectUpdate(projectUpdate);
    verify(projectService, times(1)).updateProjectDetailIsCurrentVersion(fromDetail, true);

    verify(projectService, times(1)).deleteProjectDetail(projectDetail);
    verify(projectService, never()).deleteProject(project);
  }

  @Test(expected = CancelDraftProjectException.class)
  public void cancelDraft_whenIsNotCancellable_andSupportedProjectVersionImplementation_thenException() {
    when(testCancelProjectVersionService.isCancellable(projectDetail)).thenReturn(false);
    cancelDraftProjectVersionService.cancelDraft(projectDetail);
  }

  @Test(expected = CancelProjectVersionImplementationException.class)
  public void cancelDraft_whenNoSupportedCancelProjectVersionImplementation_thenException() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    projectDetail.setProjectType(unsupportedProjectType);

    when(testCancelProjectVersionService.getSupportedProjectType()).thenReturn(supportedProjectType);

    cancelDraftProjectVersionService.cancelDraft(projectDetail);
  }

  @Test
  public void cancelDraftIfExists_whenExists() {
    var projectId = project.getId();
    var draftProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    when(projectService.getLatestDetailOrError(projectId)).thenReturn(draftProjectDetail);
    when(testCancelProjectVersionService.getSupportedProjectType()).thenReturn(draftProjectDetail.getProjectType());
    when(testCancelProjectVersionService.isCancellable(draftProjectDetail)).thenReturn(true);

    var cancelDraftProjectVersionServiceSpy = spy(cancelDraftProjectVersionService);
    cancelDraftProjectVersionServiceSpy.cancelDraftIfExists(projectId);
    verify(cancelDraftProjectVersionServiceSpy, times(1)).cancelDraft(draftProjectDetail);
  }

  @Test
  public void cancelDraftIfExists_whenNotExists() {
    var projectId = project.getId();
    var publishedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

    when(projectService.getLatestDetailOrError(projectId)).thenReturn(publishedProjectDetail);

    var cancelDraftProjectVersionServiceSpy = spy(cancelDraftProjectVersionService);
    cancelDraftProjectVersionServiceSpy.cancelDraftIfExists(projectId);
    verify(cancelDraftProjectVersionServiceSpy, never()).cancelDraft(publishedProjectDetail);
  }

  @Test
  public void getCancelDraftModelAndView_whenFirstVersion() {
    projectDetail.setVersion(1);

    var projectSummaryHtml = "html";

    when(projectSummaryRenderingService.renderSummary(projectDetail, authenticatedUser)).thenReturn(projectSummaryHtml);

    var modelAndView = cancelDraftProjectVersionService.getCancelDraftModelAndView(projectDetail, authenticatedUser);
    assertCancelDraftModelAndView(modelAndView, false, projectSummaryHtml);
  }

  @Test
  public void getCancelDraftModelAndView_whenUpdate() {
    projectDetail.setVersion(2);

    var projectSummaryHtml = "html";

    when(projectSummaryRenderingService.renderSummary(projectDetail, authenticatedUser)).thenReturn(projectSummaryHtml);

    var modelAndView = cancelDraftProjectVersionService.getCancelDraftModelAndView(projectDetail, authenticatedUser);
    assertCancelDraftModelAndView(modelAndView, true, projectSummaryHtml);
  }

  @Test
  public void isCancellable_whenCancellable_thenTrue() {
    when(testCancelProjectVersionService.getSupportedProjectType()).thenReturn(projectDetail.getProjectType());
    when(testCancelProjectVersionService.isCancellable(projectDetail)).thenReturn(true);

    final var isCancellable = cancelDraftProjectVersionService.isCancellable(projectDetail);

    assertThat(isCancellable).isTrue();
  }

  @Test
  public void isCancellable_whenNotCancellable_thenFalse() {
    when(testCancelProjectVersionService.getSupportedProjectType()).thenReturn(projectDetail.getProjectType());
    when(testCancelProjectVersionService.isCancellable(projectDetail)).thenReturn(false);

    final var isCancellable = cancelDraftProjectVersionService.isCancellable(projectDetail);

    assertThat(isCancellable).isFalse();
  }

  @Test(expected = CancelProjectVersionImplementationException.class)
  public void isCancellable_whenNoSupportedCancelProjectVersionImplementation_thenException() {

    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;
    final var supportedProjectType = ProjectType.INFRASTRUCTURE;

    projectDetail.setProjectType(unsupportedProjectType);

    when(testCancelProjectVersionService.getSupportedProjectType()).thenReturn(supportedProjectType);
    cancelDraftProjectVersionService.isCancellable(projectDetail);
  }


  private void assertCancelDraftModelAndView(ModelAndView modelAndView,
                                             boolean isUpdate,
                                             String projectSummaryHtml) {
    assertThat(modelAndView.getViewName()).isEqualTo(CancelDraftProjectVersionService.CANCEL_DRAFT_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("isUpdate", isUpdate),
        entry("projectSummaryHtml", projectSummaryHtml),
        entry("backToTaskListUrl", ReverseRouter.route(on(TaskListController.class)
            .viewTaskList(projectDetail.getProject().getId(), null)))
    );
  }
}
