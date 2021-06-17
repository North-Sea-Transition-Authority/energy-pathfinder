package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.project.campaigninformation.CampaignInformationController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  private CampaignInformationService campaignInformationService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    campaignInformationService = new CampaignInformationService(projectSetupService);
  }

  @Test
  public void getCampaignInformationModelAndView_assertCorrectModelProperties() {
    var modelAndView = campaignInformationService.getCampaignInformationModelAndView();

    assertThat(modelAndView.getViewName()).isEqualTo(CampaignInformationService.FORM_TEMPLATE_PATH);
    assertThat(modelAndView.getModel()).containsExactly(
        entry("pageTitle", CampaignInformationController.PAGE_NAME)
    );
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(true);
    assertThat(campaignInformationService.canShowInTaskList(projectDetail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(projectDetail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(false);
    assertThat(campaignInformationService.canShowInTaskList(projectDetail)).isFalse();
  }

  @Test
  public void isComplete_isFalse() {
    assertThat(campaignInformationService.isComplete(projectDetail)).isFalse();
  }
}