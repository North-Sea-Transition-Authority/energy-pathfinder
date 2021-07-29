package uk.co.ogauthority.pathfinder.service.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.EmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorEmailServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private EmailLinkService emailLinkService;

  @Mock
  private ProjectInformationService projectInformationService;


  private RegulatorEmailService regulatorEmailService;

  private static final String REGULATOR_SHARED_EMAIL = "a@b.com";
  private static final ProjectDetail PROJECT_DETAIL = ProjectUtil.getProjectDetails();
  private static final String PROJECT_NAME = "Regulator email service project";
  private static final String SERVICE_LOGIN_URL = "service-url";


  @Before
  public void setUp() throws Exception {
    regulatorEmailService = new RegulatorEmailService(
        emailService,
        emailLinkService,
        projectInformationService,
        REGULATOR_SHARED_EMAIL
    );
    when(projectInformationService.getProjectTitle(PROJECT_DETAIL)).thenReturn(PROJECT_NAME);
    when(emailLinkService.generateProjectManagementUrl(PROJECT_DETAIL.getProject())).thenReturn(SERVICE_LOGIN_URL);

  }

  @Test
  public void sendUpdateSubmitConfirmationEmail() {

    regulatorEmailService.sendUpdateSubmitConfirmationEmail(PROJECT_DETAIL);


    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(REGULATOR_SHARED_EMAIL));
    ProjectUpdateEmailProperties emailProperties = (ProjectUpdateEmailProperties) emailCaptor.getValue();

    var expectedEmailProperties = getCommonEmailProperties();

    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  @Test
  public void sendNoUpdateNotificationEmail() {
    var noUpdateReason = "no update reason text";
    regulatorEmailService.sendNoUpdateNotificationEmail(PROJECT_DETAIL, noUpdateReason);


    ArgumentCaptor<EmailProperties> emailCaptor = ArgumentCaptor.forClass(EmailProperties.class);
    verify(emailService, times(1)).sendEmail(emailCaptor.capture(), eq(REGULATOR_SHARED_EMAIL));

    NoUpdateNotificationEmailProperties emailProperties = (NoUpdateNotificationEmailProperties) emailCaptor.getValue();

    var expectedEmailProperties = getCommonEmailProperties();
    expectedEmailProperties.put("NO_UPDATE_REASON", noUpdateReason);

    assertThat(emailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  @Test
  public void getRegulatorSharedMailboxAddress() {
    assertThat(regulatorEmailService.getRegulatorSharedMailboxAddress()).isEqualTo(REGULATOR_SHARED_EMAIL);
  }

  private Map<String, Object> getCommonEmailProperties() {
    final var commonEmailProperties = new HashMap<String, Object>();
    commonEmailProperties.put(CommonEmailMergeField.SERVICE_LOGIN_URL, SERVICE_LOGIN_URL);
    commonEmailProperties.put("PROJECT_NAME", PROJECT_NAME);
    return commonEmailProperties;
  }
}
