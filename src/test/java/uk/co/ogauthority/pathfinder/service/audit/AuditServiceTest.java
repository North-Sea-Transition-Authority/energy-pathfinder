package uk.co.ogauthority.pathfinder.service.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserToken;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;
import uk.co.ogauthority.pathfinder.util.SecurityUtil;

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
  public void audit_whenNoAuthenticatedUser_thenVerifyMessage() {

    var auditEvent = AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST;

    AuditService.audit(
        auditEvent,
        TEST_MESSAGE
    );

    ArgumentCaptor<LoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
    verify(mockAppender, times(1)).doAppend(loggingEventCaptor.capture());
    LoggingEvent loggingEvent = loggingEventCaptor.getValue();

    assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    assertThat(loggingEvent.getMessage()).isEqualTo(String.format(AuditService.AUDIT_MESSAGE_FORMAT, auditEvent.name(), AuditService.UNAUTHENTICATED_USER, TEST_MESSAGE));
  }

  @Test
  public void audit_whenAuthenticatedUser_thenVerifyMessage() {

    var userToken = AuthenticatedUserToken.create("1",  new AuthenticatedUserAccount(new WebUserAccount(1), List.of()));
    SecurityContextHolder.getContext().setAuthentication(userToken);

    var auditEvent = AuditEvent.PROJECT_INFORMATION_UPDATED;

    AuditService.audit(
        auditEvent,
        TEST_MESSAGE
    );

    ArgumentCaptor<LoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
    verify(mockAppender, times(1)).doAppend(loggingEventCaptor.capture());
    LoggingEvent loggingEvent = loggingEventCaptor.getValue();

    assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);

    var webUserAccountId = SecurityUtil.getAuthenticatedUserFromSecurityContext()
        .map(AuthenticatedUserAccount::getWuaId)
        .orElseThrow(() -> new IllegalArgumentException("Could not find authenticated user from security context"));

    assertThat(loggingEvent.getMessage()).isEqualTo(
        String.format(
            AuditService.AUDIT_MESSAGE_FORMAT,
            auditEvent.name(),
            webUserAccountId,
            TEST_MESSAGE
        )
    );

    SecurityContextHolder.clearContext();
  }
}
