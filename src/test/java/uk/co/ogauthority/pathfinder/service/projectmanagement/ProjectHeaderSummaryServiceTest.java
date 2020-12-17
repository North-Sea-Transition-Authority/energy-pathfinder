package uk.co.ogauthority.pathfinder.service.projectmanagement;

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
import uk.co.ogauthority.pathfinder.service.projectmanagement.details.ProjectManagementDetailSectionService;
import uk.co.ogauthority.pathfinder.service.projectmanagement.heading.ProjectManagementHeadingSectionService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectHeaderSummaryServiceTest {

  @Mock
  private ProjectManagementHeadingSectionService projectManagementHeadingSectionService;

  @Mock
  private ProjectManagementDetailSectionService projectManagementDetailSectionService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private ProjectHeaderSummaryService projectHeaderSummaryService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectHeaderSummaryService = new ProjectHeaderSummaryService(
        projectManagementHeadingSectionService,
        projectManagementDetailSectionService,
        templateRenderingService
    );
  }

  @Test
  public void getProjectHeaderHtml() {
    var stubRender = "FAKE";

    when(projectManagementHeadingSectionService.getSection(projectDetail, authenticatedUser)).thenReturn(
        new ProjectManagementSection("text", Map.of("test", "1"), 1, ProjectManagementPageSectionType.STATIC_CONTENT)
    );
    when(projectManagementDetailSectionService.getSection(projectDetail, authenticatedUser)).thenReturn(
        new ProjectManagementSection("text2", Map.of("test", "2"), 2, ProjectManagementPageSectionType.STATIC_CONTENT)
    );

    when(templateRenderingService.render(any(), any(), anyBoolean())).thenReturn(stubRender);

    var html = projectHeaderSummaryService.getProjectHeaderHtml(projectDetail, authenticatedUser);

    assertThat(html).isEqualTo(stubRender + stubRender);
  }
}
