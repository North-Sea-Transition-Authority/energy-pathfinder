package uk.co.ogauthority.pathfinder.service.audit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.enums.audit.AuditEvent;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceTest {
  private static final String TEST_MESSAGE = "Test message";

  @Test
  public void verifyMessage() {
    var testMessage = AuditService.audit(
        AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST,
        TEST_MESSAGE
    );
    assertThat(testMessage).isEqualTo(String.format("%s [%s] - %s", AuditEvent.SUBSCRIBER_SIGN_UP_REQUEST.name(), AuditService.UNAUTHENTICATED_USER, TEST_MESSAGE));
  }
}
