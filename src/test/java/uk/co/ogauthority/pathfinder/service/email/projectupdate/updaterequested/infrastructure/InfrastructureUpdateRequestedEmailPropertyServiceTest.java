package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.config.ServiceProperties;
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.infrastructure.InfrastructureUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureUpdateRequestedEmailPropertyServiceTest {

  @Mock
  private LinkService linkService;

  @Mock
  private ServiceProperties serviceProperties;

  @Mock
  private ProjectInformationService projectInformationService;

  private InfrastructureUpdateRequestedEmailPropertyService infrastructureUpdateRequestedEmailPropertyService;

  @Before
  public void setup() {
    infrastructureUpdateRequestedEmailPropertyService = new InfrastructureUpdateRequestedEmailPropertyService(
        linkService,
        serviceProperties,
        projectInformationService
    );
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    final var supportedProjectType = infrastructureUpdateRequestedEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getUpdateRequestedEmailProperties_whenDeadlineDateIsEmptyString_assertEmailProperties() {

    final var emptyStringDeadlineDate = "";

    final var customerMnemonic = "whenDeadlineDateIsEmptyString customer mnemonic";
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var projectUrl = "whenDeadlineDateIsEmptyString project url";
    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);

    final var projectTitle = "whenDeadlineDateIsEmptyString project title";
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectTitle);

    final var updateReason = "whenDeadlineDateIsEmptyString update reason";

    final var expectedProperties = getCommonEmailPersonalisationProperties(
        updateReason,
        projectUrl,
        customerMnemonic,
        projectTitle
    );

    expectedProperties.put("DEADLINE_TEXT", "");

    final var resultingProperties = infrastructureUpdateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        emptyStringDeadlineDate
    );

    assertThat(resultingProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedProperties);
  }

  @Test
  public void getUpdateRequestedEmailProperties_whenDeadlineDateNotEmpty_assertEmailProperties() {

    final var populatedDeadlineDate = "deadline date";

    final var customerMnemonic = "whenDeadlineDateNotEmpty customer mnemonic";
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var projectUrl = "whenDeadlineDateNotEmpty project url";
    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);

    final var projectTitle = "whenDeadlineDateNotEmpty project title";
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectTitle);

    final var updateReason = "whenDeadlineDateNotEmpty update reason";

    final var expectedProperties = getCommonEmailPersonalisationProperties(
        updateReason,
        projectUrl,
        customerMnemonic,
        projectTitle
    );

    expectedProperties.put("DEADLINE_TEXT", String.format("An update is due by %s.", populatedDeadlineDate));

    final var resultingProperties = infrastructureUpdateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        populatedDeadlineDate
    );

    assertThat(resultingProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedProperties);
  }

  private Map<String, Object> getCommonEmailPersonalisationProperties(String updateReason,
                                                                      String projectUrl,
                                                                      String customerMnemonic,
                                                                      String projectTitle) {

    final var expectedProperties = new HashMap<String, Object>();
    expectedProperties.put("UPDATE_REASON", updateReason);
    expectedProperties.put("PROJECT_URL", projectUrl);

    final var projectTypeDisplayNameLowerCase = ProjectType.INFRASTRUCTURE.getLowercaseDisplayName();

    expectedProperties.put(
        InfrastructureUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s on %s: %s",
            InfrastructureUpdateRequestedEmailProperties.DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT,
            projectTypeDisplayNameLowerCase,
            projectTitle
        )
    );

    expectedProperties.put(
        InfrastructureUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s on your %s, %s.",
            String.format("The %s have requested an update", customerMnemonic),
            projectTypeDisplayNameLowerCase,
            projectTitle
        )
    );

    return expectedProperties;
  }

}