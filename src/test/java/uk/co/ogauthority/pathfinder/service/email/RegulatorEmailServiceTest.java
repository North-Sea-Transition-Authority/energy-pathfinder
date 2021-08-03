package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.NoUpdateNotificationEmailPropertyService;
import uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.UpdateSubmittedEmailPropertyService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private NoUpdateNotificationEmailPropertyService noUpdateNotificationEmailPropertyService;

  @Mock
  private UpdateSubmittedEmailPropertyService updateSubmittedEmailPropertyService;

  private RegulatorEmailService regulatorEmailService;

  private static final String REGULATOR_SHARED_EMAIL = "a@b.com";

  @Before
  public void setUp() throws Exception {
    regulatorEmailService = new RegulatorEmailService(
        emailService,
        REGULATOR_SHARED_EMAIL,
        noUpdateNotificationEmailPropertyService,
        updateSubmittedEmailPropertyService
    );
  }

  @Test
  public void sendUpdateSubmitConfirmationEmail() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    regulatorEmailService.sendUpdateSubmitConfirmationEmail(projectDetail);

    verify(updateSubmittedEmailPropertyService, times(1)).getUpdateSubmittedEmailProperties(projectDetail);

    verify(emailService, times(1)).sendEmail(any(), eq(REGULATOR_SHARED_EMAIL));
  }

  @Test
  public void sendNoUpdateNotificationEmail_verifyInteractions() {

    final var noUpdateReason = "no update reason text";

    final var projectDetail = ProjectUtil.getProjectDetails();

    regulatorEmailService.sendNoUpdateNotificationEmail(projectDetail, noUpdateReason);

    verify(noUpdateNotificationEmailPropertyService, times(1)).getNoUpdateNotificationEmailProperties(
        projectDetail,
        noUpdateReason
    );

    verify(emailService, times(1)).sendEmail(any(), eq(REGULATOR_SHARED_EMAIL));
  }

  @Test
  public void getRegulatorSharedMailboxAddress() {
    assertThat(regulatorEmailService.getRegulatorSharedMailboxAddress()).isEqualTo(REGULATOR_SHARED_EMAIL);
  }
}
