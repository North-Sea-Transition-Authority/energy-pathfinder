package uk.co.ogauthority.pathfinder.controller.team;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.UserPrivilege;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.enums.team.ViewableTeamType;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.team.ManageTeamService;

@RunWith(SpringRunner.class)
@WebMvcTest(ManageTeamController.class)
public class ManageTeamControllerTest extends AbstractControllerTest {

  @MockitoBean
  private ManageTeamService manageTeamService;

  private final AuthenticatedUserAccount user = new AuthenticatedUserAccount(
      new WebUserAccount(1), List.of(UserPrivilege.PATHFINDER_TEAM_VIEWER)
  );

  @Test
  public void renderTeamTypes_allTeamTypesAvailable() throws Exception {

    when(manageTeamService.getViewableTeamTypesAndUrlsForUser(user)).thenReturn(Map.of(
        ViewableTeamType.REGULATOR_TEAM, "regUrl",
        ViewableTeamType.ORGANISATION_TEAMS, "orgUrl"
    ));

    mockMvc.perform(get(ReverseRouter.route(on(ManageTeamController.class).renderTeamTypes(null)))
        .with(authenticatedUserAndSession(user)))
        .andExpect(status().isOk());

  }

  @Test
  public void renderTeamTypes_oneTeamTypeAvailable() throws Exception {

    when(manageTeamService.getViewableTeamTypesAndUrlsForUser(user)).thenReturn(Map.of(
        ViewableTeamType.REGULATOR_TEAM, "regUrl"
    ));

    mockMvc.perform(get(ReverseRouter.route(on(ManageTeamController.class).renderTeamTypes(null)))
        .with(authenticatedUserAndSession(user)))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:regUrl"));

  }

  @Test
  public void renderTeamTypes_noTeamTypesAvailable() throws Exception {

    when(manageTeamService.getViewableTeamTypesAndUrlsForUser(user)).thenReturn(Map.of());

    mockMvc.perform(get(ReverseRouter.route(on(ManageTeamController.class).renderTeamTypes(null)))
        .with(authenticatedUserAndSession(user)))
        .andExpect(status().isForbidden());

  }

}
