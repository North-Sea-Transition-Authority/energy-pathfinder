package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted;

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
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class UpdateSubmittedEmailPropertyServiceTest {

  @Mock
  private TestUpdateSubmittedEmailPropertyService testUpdateSubmittedEmailPropertyService;

  @Mock
  private EmailLinkService emailLinkService;

  private UpdateSubmittedEmailPropertyService updateSubmittedEmailPropertyService;

  @Before
  public void setup() {
    updateSubmittedEmailPropertyService = new UpdateSubmittedEmailPropertyService(
        List.of(testUpdateSubmittedEmailPropertyService),
        emailLinkService
    );
  }

  @Test
  public void getUpdateSubmittedEmailProperties_whenSupportedProjectTypeImplementation_assertImplementationSpecificEmailProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails(supportedProjectType);

    when(testUpdateSubmittedEmailPropertyService.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var serviceUrl = "service-url";
    final var customIntroText = "custom into text";
    final var customSubjectText = "custom subject text";

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceUrl);
    expectedEmailProperties.put(ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
    expectedEmailProperties.put(ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME, customSubjectText);

    when(testUpdateSubmittedEmailPropertyService.getUpdateSubmittedEmailProperties(projectDetail))
        .thenReturn(new TestUpdateSubmittedEmailProperties(serviceUrl, customIntroText, customSubjectText));

    final var resultingEmailProperties = updateSubmittedEmailPropertyService.getUpdateSubmittedEmailProperties(
        projectDetail
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  @Test
  public void getUpdateSubmittedEmailProperties_whenNoSupportedProjectTypeImplementation_assertGenericEmailProperties() {

    final var supportedProjectType = ProjectType.INFRASTRUCTURE;
    final var unsupportedProjectType = ProjectType.FORWARD_WORK_PLAN;

    final var projectDetail = ProjectUtil.getProjectDetails(unsupportedProjectType);

    when(testUpdateSubmittedEmailPropertyService.getSupportedProjectType()).thenReturn(supportedProjectType);

    final var serviceUrl = "service-url";
    when(emailLinkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(serviceUrl);

    final var expectedEmailProperties = new HashMap<String, Object>();
    expectedEmailProperties.put(CommonEmailMergeField.SERVICE_LOGIN_URL, serviceUrl);
    expectedEmailProperties.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format("%s.", ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT)
    );
    expectedEmailProperties.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format("%s", ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBJECT_TEXT)
    );

    final var resultingEmailProperties = updateSubmittedEmailPropertyService.getUpdateSubmittedEmailProperties(
        projectDetail
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  static class TestUpdateSubmittedEmailProperties extends ProjectUpdateEmailProperties {

    private final String customIntroText;

    private final String customSubjectText;

    public TestUpdateSubmittedEmailProperties(String serviceLoginUrl,
                                              String customIntroText,
                                              String customSubjectText) {
      super(serviceLoginUrl);
      this.customIntroText = customIntroText;
      this.customSubjectText = customSubjectText;
    }

    @Override
    public Map<String, Object> getEmailPersonalisation() {
      final var emailProperties = super.getEmailPersonalisation();
      emailProperties.put(ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME, customIntroText);
      emailProperties.put(ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME, customSubjectText);
      return emailProperties;
    }
  }

}