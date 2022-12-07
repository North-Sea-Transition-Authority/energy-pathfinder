package uk.co.ogauthority.pathfinder.service.project.overview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.util.ControllerUtils;

@RunWith(MockitoJUnitRunner.class)
public class OverviewServiceTest {

  @Mock
  private ProjectSummaryViewService projectSummaryViewService;

  private OverviewService overviewService;

  @Before
  public void setup() {
    overviewService = new OverviewService(projectSummaryViewService);
  }

  @Test
  public void getModelAndView_assertObjects() {
    var projectId = 1;
    var projectDetail = ProjectUtil.getProjectDetails();
    var projectSummaryView = new ProjectSummaryView("html", Collections.emptyList());
    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    var modelAndView = overviewService.getModelAndView(projectId, projectDetail);

    assertThat(modelAndView.getModel()).containsExactly(
        entry("projectSummaryView", projectSummaryView),
        entry("taskListUrl", ControllerUtils.getBackToTaskListUrl(projectId))
    );
  }
}
