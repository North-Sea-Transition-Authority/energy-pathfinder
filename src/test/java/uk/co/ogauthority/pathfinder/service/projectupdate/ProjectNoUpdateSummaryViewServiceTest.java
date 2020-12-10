package uk.co.ogauthority.pathfinder.service.projectupdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectNoUpdateSummaryViewServiceTest {

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectNoUpdateSummaryViewService projectNoUpdateSummaryViewService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    projectNoUpdateSummaryViewService = new ProjectNoUpdateSummaryViewService(projectInformationService, webUserAccountService);
  }

  @Test
  public void getProjectNoUpdateSummaryView() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var webUserAccount = UserTestingUtil.getWebUserAccount();

    when(projectInformationService.getProjectInformationOrError(projectDetail)).thenReturn(projectInformation);
    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua())).thenReturn(webUserAccount);

    var projectNoUpdateSummaryView = projectNoUpdateSummaryViewService.getProjectNoUpdateSummaryView(projectDetail);
    assertThat(projectNoUpdateSummaryView.getProjectTitle()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(projectNoUpdateSummaryView.getFormattedSubmittedTimestamp()).isEqualTo(DateUtil.formatInstant(projectDetail.getSubmittedInstant()));
    assertThat(projectNoUpdateSummaryView.getSubmittedBy()).isEqualTo(webUserAccount.getFullName());
  }
}
