package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.infrastructure;

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
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.submitted.ProjectUpdateEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.email.EmailLinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureUpdateSubmittedEmailPropertyServiceTest {

  private static final String SERVICE_LOGIN_URL = "service-url";

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private EmailLinkService emailLinkService;

  @Mock
  private ProjectInformationService projectInformationService;

  private InfrastructureUpdateSubmittedEmailPropertyService infrastructureUpdateSubmittedEmailPropertyService;

  @Before
  public void setup() {
    infrastructureUpdateSubmittedEmailPropertyService = new InfrastructureUpdateSubmittedEmailPropertyService(
        projectOperatorService,
        emailLinkService,
        projectInformationService
    );

    when(emailLinkService.generateProjectManagementUrl(any())).thenReturn(SERVICE_LOGIN_URL);
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    final var supportedProjectType = infrastructureUpdateSubmittedEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getUpdateSubmittedEmailProperties_assertEmailProperties() {

    final var projectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails(projectType);

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    final var projectTitle = "project title";
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectTitle);

    final var resultingEmailProperties = infrastructureUpdateSubmittedEmailPropertyService.getUpdateSubmittedEmailProperties(
        projectDetail
    );

    final var expectedEmailProperties = getCommonEmailProperties();
    expectedEmailProperties.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for %s: %s.",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT,
            projectOperator.getOrganisationGroup().getName(),
            projectType.getLowercaseDisplayName(),
            projectTitle
        )
    );
    expectedEmailProperties.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s for %s: %s",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBJECT_TEXT,
            projectType.getLowercaseDisplayName(),
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