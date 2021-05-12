package uk.co.ogauthority.pathfinder.service.projectmanagement.details.forwardworkplan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.forwardworkplan.ForwardWorkPlanManagementDetailViewUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ForwardWorkPlanManagementDetailSectionServiceTest {

  @Mock
  private WebUserAccountService webUserAccountService;

  private ForwardWorkPlanManagementDetailSectionService forwardWorkPlanManagementDetailSectionService;

  @Before
  public void setup() {
    forwardWorkPlanManagementDetailSectionService = new ForwardWorkPlanManagementDetailSectionService(
        webUserAccountService
    );
  }

  @Test
  public void getSupportedProjectType_assertForwardWorkPlan() {
    assertThat(forwardWorkPlanManagementDetailSectionService.getSupportedProjectType()).isEqualTo(
        ProjectType.FORWARD_WORK_PLAN
    );
  }

  @Test
  public void getTemplatePath_assertExpectedPath() {
    assertThat(forwardWorkPlanManagementDetailSectionService.getTemplatePath()).isEqualTo(
        ForwardWorkPlanManagementDetailSectionService.TEMPLATE_PATH
    );
  }

  @Test
  public void getManagementDetailView_assertExpectedView() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    final var submitterUserAccount = UserTestingUtil.getAuthenticatedUserAccount();

    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua())).thenReturn(submitterUserAccount);

    final var expectedDetailView = ForwardWorkPlanManagementDetailViewUtil.from(
        projectDetail,
        submitterUserAccount
    );

    final var resultingDetailView = forwardWorkPlanManagementDetailSectionService.getManagementDetailView(
        projectDetail
    );

    assertThat(resultingDetailView).isEqualTo(expectedDetailView);
  }

}