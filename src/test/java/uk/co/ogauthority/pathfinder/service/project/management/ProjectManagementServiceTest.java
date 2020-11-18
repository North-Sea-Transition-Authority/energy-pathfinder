package uk.co.ogauthority.pathfinder.service.project.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.management.action.ProjectManagementActionSectionService;
import uk.co.ogauthority.pathfinder.service.project.management.details.ProjectManagementDetailSectionService;
import uk.co.ogauthority.pathfinder.service.project.management.summary.ProjectManagementSummarySectionService;
import uk.co.ogauthority.pathfinder.testutil.ProjectManagementSectionTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementServiceTest {

  @Mock
  private ProjectManagementActionSectionService projectManagementActionSectionService;

  @Mock
  private ProjectManagementDetailSectionService projectManagementDetailSectionService;

  @Mock
  private ProjectManagementSummarySectionService projectManagementSummarySectionService;

  private ProjectManagementService projectManagementService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementService = new ProjectManagementService(List.of(
        projectManagementActionSectionService,
        projectManagementDetailSectionService,
        projectManagementSummarySectionService
    ));

    when(projectManagementActionSectionService.getSection(projectDetail, authenticatedUser)).thenReturn(
        ProjectManagementSectionTestUtil.getProjectManagementSection()
    );
    when(projectManagementDetailSectionService.getSection(projectDetail, authenticatedUser)).thenReturn(
        ProjectManagementSectionTestUtil.getProjectManagementSection()
    );
    when(projectManagementSummarySectionService.getSection(projectDetail, authenticatedUser)).thenReturn(
        ProjectManagementSectionTestUtil.getProjectManagementSection()
    );
  }

  @Test
  public void getSections() {
    var sections = projectManagementService.getSections(projectDetail, authenticatedUser);

    assertThat(sections.size()).isEqualTo(3);

    assertThat(sections.get(0).getDisplayOrder()).isEqualTo(ProjectManagementSectionTestUtil.DISPLAY_ORDER);
    assertThat(sections.get(0).getTemplatePath()).isEqualTo(ProjectManagementSectionTestUtil.TEMPLATE_PATH);
    assertThat(sections.get(0).getTemplateModel()).isEqualTo(ProjectManagementSectionTestUtil.TEMPLATE_MODEL);

    assertThat(sections.get(1).getDisplayOrder()).isEqualTo(ProjectManagementSectionTestUtil.DISPLAY_ORDER);
    assertThat(sections.get(1).getTemplatePath()).isEqualTo(ProjectManagementSectionTestUtil.TEMPLATE_PATH);
    assertThat(sections.get(1).getTemplateModel()).isEqualTo(ProjectManagementSectionTestUtil.TEMPLATE_MODEL);

    assertThat(sections.get(2).getDisplayOrder()).isEqualTo(ProjectManagementSectionTestUtil.DISPLAY_ORDER);
    assertThat(sections.get(2).getTemplatePath()).isEqualTo(ProjectManagementSectionTestUtil.TEMPLATE_PATH);
    assertThat(sections.get(2).getTemplateModel()).isEqualTo(ProjectManagementSectionTestUtil.TEMPLATE_MODEL);
  }
}
