package uk.co.ogauthority.pathfinder.controller.project.platformsfpsos;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
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
import uk.co.ogauthority.pathfinder.model.form.project.platformsfpsos.PlatformFpsoForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.platformsfpsos.PlatformsFpsosService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PlatformsFpsosController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class PlatformsFpsosControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private PlatformsFpsosService platformsFpsosService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Before
  public void setUp() {
    when(projectService.getLatestDetail(PROJECT_ID)).thenReturn(Optional.of(detail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
  }


  @Test
  public void authenticatedUser_hasAccessToAddPlatformFpso() throws Exception {
    when(platformsFpsosService.getForm()).thenReturn(new PlatformFpsoForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).addPlatformFpso(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessAddPlatformFpso() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).addPlatformFpso(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void authenticatedUser_hasAccessToPlatformsFpsosSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).viewPlatformFpso(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessPlatformsFpsosSummary() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(PlatformsFpsosController.class).viewPlatformFpso(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }
}
