package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class NoUpdateNotificationEmailPropertyServiceTest {

  @Mock
  private TestNoUpdateNotificationEmailPropertyService testNoUpdateNotificationEmailPropertyService;

  @Mock
  private LinkService linkService;

  private NoUpdateNotificationEmailPropertyService noUpdateNotificationEmailPropertyService;

  @Before
  public void setup() {
    noUpdateNotificationEmailPropertyService = new NoUpdateNotificationEmailPropertyService(
        List.of(testNoUpdateNotificationEmailPropertyService),
        linkService
    );
  }

  @Test
  public void getNoUpdateNotificationEmailProperties_whenSupportedProjectTypeImplementation_assertImplementationSpecificEmailProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails(supportedProjectType);
    final var noUpdateReason = "no update reason";

    when(testNoUpdateNotificationEmailPropertyService.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var serviceUrl = "service-url";
    final var customIntroText = "custom into text";
    final var customSubjectText = "custom subject text";

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceUrl);
    expectedEmailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_REASON_MAIL_MERGE_FIELD_NAME, noUpdateReason);
    expectedEmailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
    expectedEmailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME, customSubjectText);

    when(testNoUpdateNotificationEmailPropertyService.getNoUpdateNotificationEmailProperties(projectDetail, noUpdateReason))
        .thenReturn(new TestNoUpdateEmailProperties(serviceUrl, noUpdateReason, customIntroText, customSubjectText));

    final var resultingEmailProperties = noUpdateNotificationEmailPropertyService.getNoUpdateNotificationEmailProperties(
        projectDetail,
        noUpdateReason
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  @Test
  public void getNoUpdateNotificationEmailProperties_whenNoSupportedProjectTypeImplementation_assertGenericEmailProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetail = ProjectUtil.getProjectDetails(unsupportedProjectType);

    final var noUpdateReason = "no update reason";

    when(testNoUpdateNotificationEmailPropertyService.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var serviceUrl = "service-url";
    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(serviceUrl);

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceUrl);
    expectedEmailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_REASON_MAIL_MERGE_FIELD_NAME, noUpdateReason);
    expectedEmailProperties.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT)
    );
    expectedEmailProperties.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_SUBJECT_TEXT)
    );

    final var resultingEmailProperties = noUpdateNotificationEmailPropertyService.getNoUpdateNotificationEmailProperties(
        projectDetail,
        noUpdateReason
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  static class TestNoUpdateEmailProperties extends NoUpdateNotificationEmailProperties {

    private final String customIntroText;

    private final String customSubjectText;

    public TestNoUpdateEmailProperties(String serviceLoginUrl,
                                       String noUpdateReason,
                                       String customIntroText,
                                       String customSubjectText) {
      super(serviceLoginUrl, noUpdateReason);
      this.customIntroText = customIntroText;
      this.customSubjectText = customSubjectText;
    }

    @Override
    public Map<String, Object> getEmailPersonalisation() {
      final var emailProperties = super.getEmailPersonalisation();
      emailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
      emailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME, customSubjectText);
      return emailProperties;
    }
  }

}