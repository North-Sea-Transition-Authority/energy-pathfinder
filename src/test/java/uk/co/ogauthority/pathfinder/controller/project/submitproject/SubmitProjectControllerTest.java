package uk.co.ogauthority.pathfinder.controller.project.submitproject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.project.submission.SubmitProjectController;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.SubmitProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SubmitProjectController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class SubmitProjectControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;


  @MockBean
  private ProjectSummaryViewService projectSummaryViewService;

  @MockBean
  private SubmitProjectService submitProjectService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES
  );

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Before
  public void setUp() throws Exception {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectSummaryViewService.getProjectSummaryView(detail)).thenReturn(new ProjectSummaryView("",
        Collections.emptyList()));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
  }

  @Test
  public void authenticatedUser_hasAccessToReviewAndSubmit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubmitProjectController.class).getProjectSummary(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessReviewAndSubmit() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubmitProjectController.class).getProjectSummary(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void submitProject_whenAuthenticatedAndProjectValid_thenAccess() throws Exception {

    when(submitProjectService.isProjectValid(any())).thenReturn(true);

    mockMvc.perform(post(ReverseRouter.route(
        on(SubmitProjectController.class).submitProject(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(submitProjectService, times(1)).submitProject(any(), any());
  }

  @Test
  public void submitProject_whenAuthenticatedAndProjectInvalid_thenSubmitNotCalled() throws Exception {

    when(submitProjectService.isProjectValid(any())).thenReturn(false);

    mockMvc.perform(post(ReverseRouter.route(
        on(SubmitProjectController.class).submitProject(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().isOk());

    verify(submitProjectService, times(0)).submitProject(any(), any());
  }

  @Test
  public void submitProject_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(
        on(SubmitProjectController.class).submitProject(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser))
        .with(csrf()))
        .andExpect(status().isForbidden());

    verify(submitProjectService, times(0)).submitProject(any(), any());
  }

  @Test
  public void submitProjectConfirmation_whenAuthenticatedAndQA_thenAccess() throws Exception {
    var projectId = 2;
    var projectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);

    when(projectService.getLatestDetailOrError(projectId)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(
        on(SubmitProjectController.class).submitProjectConfirmation(projectId, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void submitProjectConfirmation_whenAuthenticatedAndNonQA_thenNoAccess() throws Exception {
    detail.setStatus(ProjectStatus.DRAFT);

    mockMvc.perform(get(ReverseRouter.route(
        on(SubmitProjectController.class).submitProjectConfirmation(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void submitProjectConfirmation_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(SubmitProjectController.class).submitProjectConfirmation(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }
}
