package uk.co.ogauthority.pathfinder.service.email.projectupdate.updaterequested.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.model.email.emailproperties.project.update.requested.forwardworkplan.ForwardWorkPlanUpdateRequestedEmailProperties;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpdateRequestedEmailPropertyServiceTest {

  @Mock
  private LinkService linkService;

  @Mock
  private ServiceProperties serviceProperties;

  private ForwardWorkPlanUpdateRequestedEmailPropertyService forwardWorkPlanUpdateRequestedEmailPropertyService;

  @Before
  public void setup() {
    forwardWorkPlanUpdateRequestedEmailPropertyService = new ForwardWorkPlanUpdateRequestedEmailPropertyService(
        linkService,
        serviceProperties
    );
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    final var supportedProjectType = forwardWorkPlanUpdateRequestedEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getUpdateRequestedEmailProperties_whenDeadlineDateIsEmptyString_assertEmailProperties() {

    final var emptyStringDeadlineDate = "";

    final var customerMnemonic = "whenDeadlineDateIsEmptyString customer mnemonic";
    when(serviceProperties.getCustomerMnemonic()).thenReturn(customerMnemonic);

    final var projectDetail = ProjectUtil.getProjectDetails();

    final var projectUrl = "whenDeadlineDateIsEmptyString project url";
    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(projectUrl);

    final var updateReason = "whenDeadlineDateIsEmptyString update reason";

    final var expectedProperties = getCommonEmailPersonalisationProperties(
        updateReason,
        projectUrl,
        customerMnemonic
    );

    expectedProperties.put("DEADLINE_TEXT", "");

    final var resultingProperties = forwardWorkPlanUpdateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
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

    final var updateReason = "whenDeadlineDateNotEmpty update reason";

    final var expectedProperties = getCommonEmailPersonalisationProperties(
        updateReason,
        projectUrl,
        customerMnemonic
    );

    expectedProperties.put("DEADLINE_TEXT", String.format("An update is due by %s.", populatedDeadlineDate));

    final var resultingProperties = forwardWorkPlanUpdateRequestedEmailPropertyService.getUpdateRequestedEmailProperties(
        projectDetail,
        updateReason,
        populatedDeadlineDate
    );

    assertThat(resultingProperties.getEmailPersonalisation()).containsExactlyInAnyOrderEntriesOf(expectedProperties);
  }

  private Map<String, Object> getCommonEmailPersonalisationProperties(String updateReason,
                                                                      String projectUrl,
                                                                      String customerMnemonic) {

    final var expectedProperties = new HashMap<String, Object>();
    expectedProperties.put("UPDATE_REASON", updateReason);
    expectedProperties.put("PROJECT_URL", projectUrl);

    final var projectTypeDisplayNameLowerCase = ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName();

    expectedProperties.put(
        ForwardWorkPlanUpdateRequestedEmailProperties.UPDATE_REQUESTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s to your organisation's %s",
            ForwardWorkPlanUpdateRequestedEmailProperties.DEFAULT_UPDATE_REQUESTED_SUBJECT_TEXT,
            projectTypeDisplayNameLowerCase
        )
    );

    expectedProperties.put(
        ForwardWorkPlanUpdateRequestedEmailProperties.UPDATE_REQUESTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s to your organisation's %s.",
            String.format("The %s have requested an update", customerMnemonic),
            projectTypeDisplayNameLowerCase
        )
    );

    return expectedProperties;
  }

}