package uk.co.ogauthority.pathfinder.service.project.submission.infrastructure;

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
import uk.co.ogauthority.pathfinder.model.entity.project.projectinformation.ProjectInformation;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.submission.SubmissionSummaryView;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureProjectSubmissionSummaryServiceTest {

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private InfrastructureProjectSubmissionSummaryService infrastructureProjectSubmissionSummaryService;

  private ProjectDetail projectDetail;

  private ProjectInformation projectInformation;

  private WebUserAccount submittingUserAccount;

  @Before
  public void setup() {
    infrastructureProjectSubmissionSummaryService = new InfrastructureProjectSubmissionSummaryService(
        projectInformationService,
        webUserAccountService
    );

    projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setSubmittedByWua(1);
    projectDetail.setSubmittedInstant(Instant.now());

    projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    when(projectInformationService.getProjectTitle(projectDetail)).thenReturn(projectInformation.getProjectTitle());

    submittingUserAccount = UserTestingUtil.getWebUserAccount();
    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua())).thenReturn(submittingUserAccount);
  }

  @Test
  public void getSupportedProjectType_assertInfrastructure() {
    assertThat(infrastructureProjectSubmissionSummaryService.getSupportedProjectType()).isEqualTo(ProjectType.INFRASTRUCTURE);
  }

  @Test
  public void getSubmissionSummaryView_assertSummaryViewProperties() {

    final var submissionSummaryView = infrastructureProjectSubmissionSummaryService.getSubmissionSummaryView(projectDetail);

    assertCommonSummaryFields(
        submissionSummaryView,
        projectInformation.getProjectTitle(),
        submittingUserAccount.getFullName(),
        projectDetail.getSubmittedInstant()
    );
  }

  @Test
  public void getNoUpdateSubmissionSummaryView_assertSummaryViewProperties() {

    final var submissionSummaryView = infrastructureProjectSubmissionSummaryService.getNoUpdateSubmissionSummaryView(projectDetail);

    assertCommonSummaryFields(
        submissionSummaryView,
        projectInformation.getProjectTitle(),
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