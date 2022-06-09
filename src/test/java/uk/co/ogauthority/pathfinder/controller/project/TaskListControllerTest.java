package uk.co.ogauthority.pathfinder.controller.project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.tasks.TaskListService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = TaskListController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class TaskListControllerTest extends ProjectContextAbstractControllerTest {

  @MockBean
  private TaskListService taskListService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private final ProjectDetail details = ProjectUtil.getProjectDetails();

  @Before
  public void setUp() throws Exception {
    when(taskListService.getTaskListModelAndView(eq(details), any(), eq(authenticatedUser)))
        .thenReturn(new ModelAndView("test/blankTemplate.ftl"));
  }

  @Test
  public void authenticatedUser_hasAccessToTaskList() throws Exception {
    when(projectService.getLatestDetailOrError(any())).thenReturn(details);
    when(projectOperatorService.isUserInProjectTeam(details, authenticatedUser)).thenReturn(true);

    mockMvc.perform(get(ReverseRouter.route(on(TaskListController.class).viewTaskList(1, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(taskListService, times(1))
        .getTaskListModelAndView(eq(details), any(), eq(authenticatedUser));
  }

  @Test
  public void unAuthenticatedUser_cannotAccessTaskList() throws Exception {
    when(projectService.getLatestDetailOrError(any())).thenReturn(details);
    mockMvc.perform(get(ReverseRouter.route(on(TaskListController.class).viewTaskList(1, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());

    verify(taskListService, times(0))
        .getTaskListModelAndView(eq(details), any(), eq(authenticatedUser));
  }
}
