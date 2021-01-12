package uk.co.ogauthority.pathfinder.controller.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.email.NotifyCallbackService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = NotifyCallbackController.class)
public class NotifyCallbackControllerTest extends AbstractControllerTest {

  @MockBean
  private NotifyCallbackService notifyCallbackServiceMock;

  @Autowired
  private ObjectMapper objectMapper;

  private NotifyCallback notifyCallback;

  @Before
  public void setup() {
    notifyCallback = new NotifyCallback(
        "be0a4c7d-1657-4b83-8771-2a40e7408d67",
        345235,
        NotifyCallback.NotifyCallbackStatus.DELIVERED,
        "test@test.email.co.uk",
        NotifyCallback.NotifyNotificationType.EMAIL,
        Instant.now(),
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  public void notifyCallback_invalidToken() throws Exception {

    when(notifyCallbackServiceMock.isTokenValid(anyString())).thenReturn(false);

    mockMvc.perform(post(ReverseRouter.route(on(NotifyCallbackController.class).notifyCallback(null, null)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notifyCallback))
        .header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isForbidden());
  }

  @Test
  public void notifyCallback_validToken() throws Exception {

    when(notifyCallbackServiceMock.isTokenValid(anyString())).thenReturn(true);

    mockMvc.perform(post(ReverseRouter.route(on(NotifyCallbackController.class).notifyCallback(null, null)))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(notifyCallback))
        .header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk());

    verify(notifyCallbackServiceMock, times(1)).handleCallback(any());

  }
}