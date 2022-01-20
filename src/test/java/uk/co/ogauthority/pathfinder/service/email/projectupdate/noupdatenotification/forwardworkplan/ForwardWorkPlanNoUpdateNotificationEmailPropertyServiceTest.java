package uk.co.ogauthority.pathfinder.service.email.projectupdate.noupdatenotification.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.service.email.notify.CommonEmailMergeField;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanNoUpdateNotificationEmailPropertyServiceTest {

  private static final String SERVICE_LOGIN_URL = "service-url";

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private LinkService linkService;

  private ForwardWorkPlanNoUpdateNotificationEmailPropertyService forwardWorkPlanNoUpdateNotificationEmailPropertyService;

  @Before
  public void setup() {
    forwardWorkPlanNoUpdateNotificationEmailPropertyService = new ForwardWorkPlanNoUpdateNotificationEmailPropertyService(
        projectOperatorService,
        linkService
    );

    when(linkService.generateProjectManagementUrl(any())).thenReturn(SERVICE_LOGIN_URL);
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    final var supportedProjectType = forwardWorkPlanNoUpdateNotificationEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getNoUpdateNotificationEmailProperties_assertEmailProperties() {

    final var noUpdateReason = "no update reason text";

    final var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    final var resultingEmailProperties = forwardWorkPlanNoUpdateNotificationEmailPropertyService.getNoUpdateNotificationEmailProperties(
        projectDetail,
        noUpdateReason
    );

    final var expectedEmailProperties = getCommonEmailProperties();
    expectedEmailProperties.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_PROJECT_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s.",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_PROJECT_INTRO_TEXT,
            projectOperator.getOrganisationGroup().getName(),
            ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()
        )
    );
    expectedEmailProperties.put(NoUpdateNotificationEmailProperties.NO_UPDATE_REASON_MAIL_MERGE_FIELD_NAME, noUpdateReason);
    expectedEmailProperties.put(
        NoUpdateNotificationEmailProperties.NO_UPDATE_NOTIFICATION_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s",
            NoUpdateNotificationEmailProperties.DEFAULT_NO_UPDATE_SUBJECT_TEXT,
            projectOperator.getOrganisationGroup().getName(),
            ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()
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