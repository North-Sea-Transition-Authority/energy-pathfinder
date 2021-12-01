package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.noupdatenotification.NoUpdateNotificationEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureNoUpdateNotificationEmailPropertyServiceTest {

  private static final String SERVICE_LOGIN_URL = "service-url";

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private EmailLinkService emailLinkService;

  @Mock
  private ProjectInformationService projectInformationService;

  private InfrastructureNoUpdateNotificationEmailPropertyService infrastructureNoUpdateNotificationEmailPropertyService;

  @Before
  public void setup() {
    infrastructureNoUpdateNotificationEmailPropertyService = new InfrastructureNoUpdateNotificationEmailPropertyService(
        projectOperatorService,
        emailLinkService,
        projectInformationService
    );

    when(emailLinkService.generateProjectManagementUrl(any())).thenReturn(SERVICE_LOGIN_URL);
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    final var supportedProjectType = infrastructureNoUpdateNotificationEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getNoUpdateNotificationEmailProperties_assertEmailProperties() {

    final var noUpdateReason = "no update reason text";

    final var projectDetail = ProjectUtil.getProjectDetails(ProjectType.INFRASTRUCTURE);

    final var projectTitle = "project title";
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectTitle);

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    final var resultingEmailProperties = infrastructureNoUpdateNotificationEmailPropertyService.getNoUpdateNotificationEmailProperties(
        projectDetail,
        noUpdateReason
    );

    final var expectedEmailProperties = getCommonEmailProperties();
    expectedEmailProperties.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for %s: %s.",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT,
            projectOperator.getOrganisationGroup().getName(),
            ProjectType.INFRASTRUCTURE.getLowercaseDisplayName(),
            projectTitle
        )
    );
    expectedEmailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_REASON_MAIL_MERGE_FIELD_NAME, noUpdateReason);
    expectedEmailProperties.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s for %s: %s",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_SUBJECT_TEXT,
            ProjectType.INFRASTRUCTURE.getLowercaseDisplayName(),
            projectTitle
        )
    );

    assertThat(resultingEmailProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedEmailProperties);
  }

  private Map<String, Object> getCommonEmailProperties() {
    final var commonEmailProperties = new HashMap<String, Object>();
    commonEmailProperties.put(CommonEmailMergeField.SERVICE_LOGIN_URL, SERVICE_LOGIN_URL);
    return commonEmailProperties;
  }
}