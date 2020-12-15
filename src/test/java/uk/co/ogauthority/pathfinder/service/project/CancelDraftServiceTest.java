package uk.co.ogauthority.pathfinder.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.ProjectUpdate;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryRenderingService;
import uk.co.ogauthority.pathfinder.service.project.upcomingtender.UpcomingTenderService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class CancelDraftServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectUpdateService projectUpdateService;

  @Mock
  private RegulatorProjectUpdateService regulatorProjectUpdateService;

  @Mock
  private ProjectSummaryRenderingService projectSummaryRenderingService;

  @Mock
  private UpcomingTenderService upcomingTenderService;

  @Mock
  private PlatformsFpsosService platformsFpsosService;

  private CancelDraftService cancelDraftService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    cancelDraftService = new CancelDraftService(
        projectService,
        projectUpdateService,
        regulatorProjectUpdateService,
        projectSummaryRenderingService,
        List.of(upcomingTenderService, platformsFpsosService)
    );
  }

  @Test
  public void cancelDraft_whenFirstVersion() {
    projectDetail.setVersion(1);

    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.empty());

    cancelDraftService.cancelDraft(projectDetail);

    verify(upcomingTenderService, times(1)).removeSectionData(projectDetail);
    verify(platformsFpsosService, times(1)).removeSectionData(projectDetail);

    verify(projectService, times(1)).deleteProjectDetail(projectDetail);
    verify(projectService, times(1)).deleteProject(project);
  }

  @Test
  public void cancelDraft_whenOperatorRequestedUpdate() {
    projectDetail.setVersion(2);

    var projectUpdate = new ProjectUpdate();
    projectUpdate.setUpdateType(ProjectUpdateType.OPERATOR_INITIATED);

    var fromDetail = ProjectUtil.getProjectDetails();
    projectUpdate.setFromDetail(fromDetail);

    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.of(projectUpdate));

    cancelDraftService.cancelDraft(projectDetail);

    verify(upcomingTenderService, times(1)).removeSectionData(projectDetail);
    verify(platformsFpsosService, times(1)).removeSectionData(projectDetail);

    verify(regulatorProjectUpdateService, never()).deleteRegulatorRequestedUpdate(projectUpdate);
    verify(projectUpdateService, times(1)).deleteProjectUpdate(projectUpdate);
    verify(projectService, times(1)).updateProjectDetailIsCurrentVersion(fromDetail, true);

    verify(projectService, times(1)).deleteProjectDetail(projectDetail);
    verify(projectService, never()).deleteProject(project);
  }

  @Test
  public void cancelDraft_whenRegulatorRequestedUpdate() {
    projectDetail.setVersion(2);

    var projectUpdate = new ProjectUpdate();
    projectUpdate.setUpdateType(ProjectUpdateType.REGULATOR_REQUESTED);

    var fromDetail = ProjectUtil.getProjectDetails();
    projectUpdate.setFromDetail(fromDetail);

    when(projectUpdateService.getByToDetail(projectDetail)).thenReturn(Optional.of(projectUpdate));

    cancelDraftService.cancelDraft(projectDetail);

    verify(upcomingTenderService, times(1)).removeSectionData(projectDetail);
    verify(platformsFpsosService, times(1)).removeSectionData(projectDetail);

    verify(regulatorProjectUpdateService, times(1)).deleteRegulatorRequestedUpdate(projectUpdate);
    verify(projectUpdateService, times(1)).deleteProjectUpdate(projectUpdate);
    verify(projectService, times(1)).updateProjectDetailIsCurrentVersion(fromDetail, true);

    verify(projectService, times(1)).deleteProjectDetail(projectDetail);
    verify(projectService, never()).deleteProject(project);
  }

  @Test
  public void getCancelDraftModelAndView_whenFirstVersion() {
    projectDetail.setVersion(1);

    var projectSummaryHtml = "html";

    when(projectSummaryRenderingService.renderSummary(projectDetail, authenticatedUser)).thenReturn(projectSummaryHtml);

    var modelAndView = cancelDraftService.getCancelDraftModelAndView(projectDetail, authenticatedUser);
    assertCancelDraftModelAndView(modelAndView, false, projectSummaryHtml);
  }

  @Test
  public void getCancelDraftModelAndView_whenUpdate() {
    projectDetail.setVersion(2);

    var projectSummaryHtml = "html";

    when(projectSummaryRenderingService.renderSummary(projectDetail, authenticatedUser)).thenReturn(projectSummaryHtml);

    var modelAndView = cancelDraftService.getCancelDraftModelAndView(projectDetail, authenticatedUser);
    assertCancelDraftModelAndView(modelAndView, true, projectSummaryHtml);
  }

  private void assertCancelDraftModelAndView(ModelAndView modelAndView,
                                             boolean isUpdate,
                                             String projectSummaryHtml) {
    assertThat(modelAndView.getViewName()).isEqualTo(CancelDraftService.CANCEL_DRAFT_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("isUpdate", isUpdate),
        entry("projectSummaryHtml", projectSummaryHtml),
        entry("backToTaskListUrl", ReverseRouter.route(on(TaskListController.class)
            .viewTaskList(projectDetail.getProject().getId(), null)))
    );
  }
}
