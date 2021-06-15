package uk.co.ogauthority.pathfinder.service.project.campaigninformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.tasks.ProjectTask;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class CampaignInformationServiceTest {

  @Mock
  private ProjectSetupService projectSetupService;

  private CampaignInformationService campaignInformationService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();

  @Before
  public void setup() {
    campaignInformationService = new CampaignInformationService(
      projectSetupService
    );
  }

  @Test
  public void canShowInTaskList_true() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(true);
    assertThat(campaignInformationService.canShowInTaskList(detail)).isTrue();
  }

  @Test
  public void canShowInTaskList_false() {
    when(projectSetupService.taskValidAndSelectedForProjectDetail(detail, ProjectTask.CAMPAIGN_INFORMATION)).thenReturn(false);
    assertThat(campaignInformationService.canShowInTaskList(detail)).isFalse();
  }
}