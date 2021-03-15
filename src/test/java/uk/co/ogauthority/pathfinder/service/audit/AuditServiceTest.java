package uk.co.ogauthority.pathfinder.service.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceTest {
  private static final String TEST_MESSAGE = "Test message";

  @Mock
  private Appender<ch.qos.logback.classic.spi.ILoggingEvent> mockAppender;

  @Before
  public void setup() {
    Logger logger = (Logger) LoggerFactory.getLogger(AuditService.class);
    logger.addAppender(mockAppender);
  }

  @Test
  public void verifyMessage() {
    AuditService.audit(
        AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST,
        TEST_MESSAGE
    );

    ArgumentCaptor<LoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
    verify(mockAppender, times(1)).doAppend(loggingEventCaptor.capture());
    LoggingEvent loggingEvent = loggingEventCaptor.getValue();

    assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    assertThat(loggingEvent.getMessage()).isEqualTo(String.format("%s [%s] - %s", AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST.name(), AuditService.UNAUTHENTICATED_USER, TEST_MESSAGE));
  }
}
