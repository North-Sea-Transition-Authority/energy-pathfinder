package uk.co.ogauthority.pathfinder.controller.communication;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.communication.Communication;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationContext;
import uk.co.ogauthority.pathfinder.service.communication.CommunicationModelService;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(CommunicationSummaryController.class)
public class CommunicationSummaryControllerTest extends AbstractControllerTest {

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.COMMUNICATION_PRIVILEGES
  );

  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.WORK_AREA_PRIVILEGES
  );

  private final Integer communicationId = 1;

  @MockBean
  protected CommunicationModelService communicationModelService;

  @Before
  public void setup() {
    Communication communication = new Communication();
    communication.setId(1);

    //TODO Remove this ones controller tests are updated as part of PAT-441
    when(communicationJourneyService.checkJourneyStage(any(), any()))
        .thenReturn(new CommunicationContext(communication));
  }

  @Test
  public void getCommunicationsSummary_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getCommunicationsSummary_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationsSummary(
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getCommunicationSummary_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationSummary(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getCommunicationSummary_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(CommunicationSummaryController.class).getCommunicationSummary(
        communicationId,
        null,
        null
    )))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getCommunicationSummary_whenDraftCommunication_thenNoAccess() {
    //TODO complete with PAT-441
  }
}
