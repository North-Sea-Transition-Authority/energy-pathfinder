package uk.co.ogauthority.pathfinder.service.project.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSubmissionSummaryViewServiceTest {

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectSubmissionSummaryViewService projectSubmissionSummaryViewService;

  @Before
  public void setup() {
    projectSubmissionSummaryViewService = new ProjectSubmissionSummaryViewService(projectInformationService, webUserAccountService);
  }

  @Test
  public void getProjectSubmissionSummaryView() {
    var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setSubmittedInstant(Instant.now());
    projectDetail.setSubmittedByWua(4);

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);

    when(projectInformationService.getProjectInformationOrError(projectDetail)).thenReturn(projectInformation);

    var webUserAccount = UserTestingUtil.getWebUserAccount();

    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua())).thenReturn(webUserAccount);

    var projectSubmissionSummaryView = projectSubmissionSummaryViewService.getProjectSubmissionSummaryView(projectDetail);

    assertThat(projectSubmissionSummaryView.getProjectTitleOrOperator()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(projectSubmissionSummaryView.getFormattedSubmittedTimestamp()).isEqualTo(DateUtil.formatInstant(projectDetail.getSubmittedInstant()));
    assertThat(projectSubmissionSummaryView.getSubmittedBy()).isEqualTo(webUserAccount.getFullName());
  }
}
