package uk.co.ogauthority.pathfinder.controller.project;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

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
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.CancelDraftProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = CancelDraftProjectVersionController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class})
)
public class CancelDraftProjectVersionControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer DRAFT_PROJECT_ID = 1;
  private static final Integer PUBLISHED_PROJECT_ID = 2;

  @MockBean
  private CancelDraftProjectVersionService cancelDraftProjectVersionService;

  private final ProjectDetail draftProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);
  private final ProjectDetail publishedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestDetailOrError(DRAFT_PROJECT_ID)).thenReturn(draftProjectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(draftProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(draftProjectDetail, unauthenticatedUser)).thenReturn(false);

    when(projectService.getLatestDetailOrError(PUBLISHED_PROJECT_ID)).thenReturn(publishedProjectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(publishedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(publishedProjectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getCancelDraft_whenAuthenticatedAndDraft_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CancelDraftProjectVersionController.class).getCancelDraft(DRAFT_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getCancelDraft_whenUnauthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CancelDraftProjectVersionController.class).getCancelDraft(DRAFT_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getCancelDraft_whenAuthenticatedAndPublished_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(CancelDraftProjectVersionController.class).getCancelDraft(PUBLISHED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void cancelDraft_whenAuthenticatedAndDraft_thenCancel() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(CancelDraftProjectVersionController.class)
            .cancelDraft(DRAFT_PROJECT_ID, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(cancelDraftProjectVersionService, times(1)).cancelDraft(draftProjectDetail);
  }

  @Test
  public void cancelDraft_whenUnauthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(CancelDraftProjectVersionController.class)
            .cancelDraft(DRAFT_PROJECT_ID, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(cancelDraftProjectVersionService, never()).cancelDraft(draftProjectDetail);
  }

  @Test
  public void cancelDraft_whenAuthenticatedAndPublished_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(CancelDraftProjectVersionController.class)
            .cancelDraft(PUBLISHED_PROJECT_ID, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(cancelDraftProjectVersionService, never()).cancelDraft(publishedProjectDetail);
  }
}
