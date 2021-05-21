package uk.co.ogauthority.pathfinder.service.project.submission.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.model.entity.WebUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectOperator;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.SubmissionSummaryView;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanSubmissionSummaryServiceTest {

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ForwardWorkPlanSubmissionSummaryService forwardWorkPlanSubmissionSummaryService;

  private ProjectDetail projectDetail;

  private ProjectOperator projectOperator;

  private WebUserAccount submittingUserAccount;

  @Before
  public void setup() {
    forwardWorkPlanSubmissionSummaryService = new ForwardWorkPlanSubmissionSummaryService(
        projectOperatorService,
        webUserAccountService
    );

    projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setSubmittedByWua(1);
    projectDetail.setSubmittedInstant(Instant.now());

    projectOperator = ProjectOperatorTestUtil.getOperator();
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    submittingUserAccount = UserTestingUtil.getWebUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua())).thenReturn(submittingUserAccount);
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    assertThat(forwardWorkPlanSubmissionSummaryService.getSupportedProjectType()).isEqualTo(ProjectType.FORWARD_WORK_PLAN);
  }

  @Test
  public void getSubmissionSummaryView_assertSummaryViewProperties() {

    final var submissionSummaryView = forwardWorkPlanSubmissionSummaryService.getSubmissionSummaryView(projectDetail);

    assertCommonSummaryFields(
        submissionSummaryView,
        projectOperator.getOrganisationGroup().getName(),
        submittingUserAccount.getFullName(),
        projectDetail.getSubmittedInstant()
    );
  }

  @Test
  public void getNoUpdateSubmissionSummaryView_assertSummaryViewProperties() {

    final var submissionSummaryView = forwardWorkPlanSubmissionSummaryService.getNoUpdateSubmissionSummaryView(projectDetail);

    assertCommonSummaryFields(
        submissionSummaryView,
        projectOperator.getOrganisationGroup().getName(),
        submittingUserAccount.getFullName(),
        projectDetail.getSubmittedInstant()
    );
  }

  private void assertCommonSummaryFields(SubmissionSummaryView submissionSummaryView,
                                         String expectedOperatorName,
                                         String expectedUserName,
                                         Instant projectSubmissionInstant) {
    assertThat(submissionSummaryView.getProjectDisplayName()).isEqualTo(expectedOperatorName);
    assertThat(submissionSummaryView.getSubmittedBy()).isEqualTo(expectedUserName);
    assertThat(submissionSummaryView.getFormattedSubmittedTimestamp()).isEqualTo(DateUtil.formatInstant(projectSubmissionInstant));
  }

}