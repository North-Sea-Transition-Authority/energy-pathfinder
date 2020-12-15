package uk.co.ogauthority.pathfinder.service.project.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.projectmanagement.summary.ProjectManagementSummarySectionService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSummaryRenderingServiceTest {

  @Mock
  private ProjectManagementSummarySectionService projectManagementSummarySectionService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private ProjectSummaryRenderingService projectSummaryRenderingService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectSummaryRenderingService = new ProjectSummaryRenderingService(
        projectManagementSummarySectionService,
        templateRenderingService
    );
  }

  @Test
  public void renderSummary() {
    var projectSummarySection = new ProjectManagementSection("test", Map.of("test", "1"), 1, ProjectManagementPageSectionType.STATIC_CONTENT);
    var stubRender = "FAKE";

    when(projectManagementSummarySectionService.getSection(projectDetail, authenticatedUser)).thenReturn(projectSummarySection);
    when(templateRenderingService.render(any(), any(), anyBoolean())).thenReturn(stubRender);

    assertThat(projectSummaryRenderingService.renderSummary(projectDetail, authenticatedUser)).isEqualTo(stubRender);
  }
}
