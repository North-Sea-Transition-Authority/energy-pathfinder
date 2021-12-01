package uk.co.ogauthority.pathfinder.service.email.projectupdate.updatesubmitted.forwardworkplan;

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
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanUpdateSubmittedEmailPropertyServiceTest {

  private static final String SERVICE_LOGIN_URL = "service-url";

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private EmailLinkService emailLinkService;

  private ForwardWorkPlanUpdateSubmittedEmailPropertyService forwardWorkPlanUpdateSubmittedEmailPropertyService;

  @Before
  public void setup() {
    forwardWorkPlanUpdateSubmittedEmailPropertyService = new ForwardWorkPlanUpdateSubmittedEmailPropertyService(
        projectOperatorService,
        emailLinkService
    );

    when(emailLinkService.generateProjectManagementUrl(any())).thenReturn(SERVICE_LOGIN_URL);
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    final var supportedProjectType = forwardWorkPlanUpdateSubmittedEmailPropertyService.getSupportedProjectType();
    assertThat(supportedProjectType).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getUpdateSubmittedEmailProperties_assertEmailProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails(ProjectType.FORWARD_WORK_PLAN);

    final var projectOperator = ProjectOperatorTestUtil.getOperator();
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    final var resultingEmailProperties = forwardWorkPlanUpdateSubmittedEmailPropertyService.getUpdateSubmittedEmailProperties(
        projectDetail
    );

    final var expectedEmailProperties = getCommonEmailProperties();
    expectedEmailProperties.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_INTRO_TEXT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s.",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBMITTED_INTRO_TEXT,
            projectOperator.getOrganisationGroup().getName(),
            ProjectType.FORWARD_WORK_PLAN.getLowercaseDisplayName()
        )
    );
    expectedEmailProperties.put(
        ProjectUpdateEmailProperties.UPDATE_SUBMITTED_SUBJECT_MAIL_MERGE_FIELD_NAME,
        String.format(
            "%s by %s for their %s",
            ProjectUpdateEmailProperties.DEFAULT_UPDATE_SUBJECT_TEXT,
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