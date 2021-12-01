package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.NotifyCallback;
import uk.co.ogauthority.pathfinder.model.enums.email.NotifyTemplate;
import uk.co.ogauthority.pathfinder.service.email.notify.callback.EmailCallback;

@RunWith(MockitoJUnitRunner.class)
public class PathfinderEmailFailureServiceTest {

  @Mock
  private TestEmailFailureHandlerService testEmailFailureHandlerService;

  @Mock
  private DefaultEmailFailureHandlerService defaultEmailFailureHandlerService;

  private PathfinderEmailFailureService pathfinderEmailFailureService;

  @Before
  public void setup() {
    pathfinderEmailFailureService = new PathfinderEmailFailureService(
        List.of(testEmailFailureHandlerService),
        defaultEmailFailureHandlerService
    );
  }

  @Test
  public void processNotifyEmailDeliveryFailure_whenNoSupportedFailureHandler_thenVerifyDefaultHandlerInteraction() {

    final var supportedNotifyTemplate = NotifyTemplate.CUSTOM_COMMUNICATION;
    final var unsupportedNotifyTemplate = NotifyTemplate.ADDED_TO_TEAM;

    when(testEmailFailureHandlerService.getSupportedTemplates()).thenReturn(Set.of(supportedNotifyTemplate));

    final var emailCallback = createEmailCallbackFailure(unsupportedNotifyTemplate);

    pathfinderEmailFailureService.processNotifyEmailDeliveryFailure(emailCallback);

    verify(testEmailFailureHandlerService, never()).handleEmailFailure(emailCallback);

    verify(defaultEmailFailureHandlerService, times(1)).handleEmailFailure(emailCallback);
  }

  @Test
  public void processNotifyEmailDeliveryFailure_whenSupportedFailureHandler_thenVerifyCustomHandlerInteraction() {

    final var supportedNotifyTemplate = NotifyTemplate.CUSTOM_COMMUNICATION;

    when(testEmailFailureHandlerService.getSupportedTemplates()).thenReturn(Set.of(supportedNotifyTemplate));

    final var emailCallback = createEmailCallbackFailure(supportedNotifyTemplate);

    pathfinderEmailFailureService.processNotifyEmailDeliveryFailure(emailCallback);

    verify(testEmailFailureHandlerService, times(1)).handleEmailFailure(emailCallback);

    verify(defaultEmailFailureHandlerService, never()).handleEmailFailure(emailCallback);
  }

  private EmailCallback createEmailCallbackFailure(NotifyTemplate notifyTemplate) {
    return EmailCallbackTestUtil.constructEmailCallback(
        notifyTemplate,
        NotifyCallback.NotifyCallbackStatus.PERMANENT_FAILURE
    );
  }

}