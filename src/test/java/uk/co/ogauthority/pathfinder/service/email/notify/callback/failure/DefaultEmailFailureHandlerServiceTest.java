package uk.co.ogauthority.pathfinder.service.email.notify.callback.failure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailDeliveryFailedEmailProps;
import uk.co.ogauthority.pathfinder.service.email.EmailService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEmailFailureHandlerServiceTest {

  @Mock
  private EmailService emailService;

  private static final String REGULATOR_SHARED_EMAIL = "someone@regulator.co.uk";

  private DefaultEmailFailureHandlerService defaultEmailFailureHandlerService;

  @Before
  public void setup() {
    defaultEmailFailureHandlerService = new DefaultEmailFailureHandlerService(
        emailService,
        REGULATOR_SHARED_EMAIL
    );
  }

  @Test
  public void handleEmailFailure_whenFailureEmailAddressIsRegulatorInbox_thenVerifyFailureEmailNotSent() {

    final var emailCallbackWithRegulatorRecipient = EmailCallbackTestUtil.constructEmailCallback(
        REGULATOR_SHARED_EMAIL
    );

    defaultEmailFailureHandlerService.handleEmailFailure(emailCallbackWithRegulatorRecipient);

    verify(emailService, never()).sendEmail(any(), eq(REGULATOR_SHARED_EMAIL));
  }

  @Test
  public void handleEmailFailure_whenFailureEmailAddressIsNotRegulatorInbox_thenVerifyFailureEmailSent() {

    final var nonRegulatorFailureRecipient = "someone@not-regulator.co.uk";

    final var emailCallbackWithNonRegulatorRecipient = EmailCallbackTestUtil.constructEmailCallback(
        nonRegulatorFailureRecipient
    );

    defaultEmailFailureHandlerService.handleEmailFailure(emailCallbackWithNonRegulatorRecipient);

    final var failedEmailProperties = new EmailDeliveryFailedEmailProps(
        nonRegulatorFailureRecipient,
        emailCallbackWithNonRegulatorRecipient.getSubject(),
        emailCallbackWithNonRegulatorRecipient.getBody()
    );

    verify(emailService, times(1)).sendEmail(failedEmailProperties, REGULATOR_SHARED_EMAIL);
  }
}