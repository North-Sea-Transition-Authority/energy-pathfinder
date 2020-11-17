package uk.co.ogauthority.pathfinder.service.project.management.summary;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.summary.ProjectSummaryView;
import uk.co.ogauthority.pathfinder.service.project.summary.ProjectSummaryViewService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementSummarySectionServiceTest {

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Mock
  private ProjectSummaryViewService projectSummaryViewService;

  private ProjectManagementSummarySectionService projectManagementSummarySectionService;

  @Before
  public void setup() {
    projectManagementSummarySectionService = new ProjectManagementSummarySectionService(
        projectSummaryViewService
    );
  }

  @Test
  public void getSection() {
    var projectSummaryView = new ProjectSummaryView("", Collections.emptyList());

    when(projectSummaryViewService.getProjectSummaryView(projectDetail)).thenReturn(projectSummaryView);

    var section = projectManagementSummarySectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementSummarySectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementSummarySectionService.DISPLAY_ORDER);
    assertThat(section.getTemplateModel()).containsExactly(
        entry("projectSummaryView", projectSummaryView)
    );
  }
}
