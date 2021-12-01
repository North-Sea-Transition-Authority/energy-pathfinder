package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.exception.ProjectTypeDetailServiceImplementationException;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementDetailSectionServiceTest {

  @Mock
  private TestProjectManagementDetailSummariserService testProjectManagementDetailSummariserService;

  private static final AuthenticatedUserAccount AUTHENTICATED_USER_ACCOUNT = UserTestingUtil.getAuthenticatedUserAccount();

  private ProjectManagementDetailSectionService projectManagementDetailSectionService;

  @Before
  public void setup() {
    projectManagementDetailSectionService = new ProjectManagementDetailSectionService(
        List.of(testProjectManagementDetailSummariserService)
    );
  }

  @Test
  public void getSection_whenImplementationForProjectType_assertSectionProperties() {

    final var projectType = ProjectType.INFRASTRUCTURE;

    final var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(projectType);

    when(testProjectManagementDetailSummariserService.getSupportedProjectType()).thenReturn(projectType);

    final var templatePath = "template path";
    when(testProjectManagementDetailSummariserService.getTemplatePath()).thenReturn(templatePath);

    final var projectManagementDetailView = new TestProjectManagementDetailView();
    when(testProjectManagementDetailSummariserService.getManagementDetailView(projectDetail)).thenReturn(projectManagementDetailView);

    var section = projectManagementDetailSectionService.getSection(
        projectDetail,
        AUTHENTICATED_USER_ACCOUNT
    );

    assertThat(section.getTemplatePath()).isEqualTo(templatePath);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementDetailSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementDetailSectionService.SECTION_TYPE);
    assertThat(section.getTemplateModel()).containsExactly(
        entry("projectManagementDetailView", projectManagementDetailView)
    );
  }

  @Test(expected = ProjectTypeDetailServiceImplementationException.class)
  public void getSection_whenNoImplementationForProjectType_thenException() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);

    final var projectType = ProjectType.INFRASTRUCTURE;
    when(testProjectManagementDetailSummariserService.getSupportedProjectType()).thenReturn(projectType);

    projectManagementDetailSectionService.getSection(projectDetail, AUTHENTICATED_USER_ACCOUNT);
  }
}
