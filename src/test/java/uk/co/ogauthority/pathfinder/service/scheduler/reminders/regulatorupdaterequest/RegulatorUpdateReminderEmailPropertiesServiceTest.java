package uk.co.ogauthority.pathfinder.service.scheduler.reminders.regulatorupdaterequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.entity.projectupdate.RegulatorUpdateRequest;
import uk.co.ogauthority.pathfinder.service.LinkService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorUpdateReminderEmailPropertiesServiceTest {

  @Mock
  private LinkService linkService;

  private RegulatorUpdateReminderEmailPropertiesService regulatorUpdateReminderEmailPropertiesService;

  @Before
  public void setup() {
    regulatorUpdateReminderEmailPropertiesService = new RegulatorUpdateReminderEmailPropertiesService(
        linkService
    );
  }

  @Test
  public void getFormattedDeadlineDate_verifyFormat() {

    var localDateToTest = LocalDate.of(2021, 1, 1);

    var resultingFormattedDate = regulatorUpdateReminderEmailPropertiesService.getFormattedDeadlineDate(localDateToTest);

    assertThat(resultingFormattedDate).isEqualTo("01 January 2021");
  }

  @Test
  public void getProjectManagementUrl_verifyInteractions() {

    var projectDetail = ProjectUtil.getProjectDetails();

    var regulatorUpdateRequest = new RegulatorUpdateRequest();
    regulatorUpdateRequest.setProjectDetail(projectDetail);

    var expectedProjectUrl = "project/url";

    when(linkService.generateProjectManagementUrl(projectDetail.getProject())).thenReturn(expectedProjectUrl);

    var resultingProjectUrl = regulatorUpdateReminderEmailPropertiesService.getProjectManagementUrl(regulatorUpdateRequest);

    assertThat(resultingProjectUrl).isEqualTo(expectedProjectUrl);
  }

}