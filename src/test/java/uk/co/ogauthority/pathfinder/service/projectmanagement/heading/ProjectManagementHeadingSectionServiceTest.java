package uk.co.ogauthority.pathfinder.service.projectmanagement.heading;

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
import uk.co.ogauthority.pathfinder.exception.ProjectManagementHeadingServiceImplementationException;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectType;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementHeadingSectionServiceTest {

  @Mock
  private TestProjectManagementHeadingSectionService testProjectManagementHeadingSectionService;

  private ProjectManagementHeadingSectionService projectManagementHeadingSectionService;

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementHeadingSectionService = new ProjectManagementHeadingSectionService(
        List.of(testProjectManagementHeadingSectionService)
    );
  }

  @Test
  public void getSection_whenSupportedProjectType_thenAssertProjectManagementSectionProperties() {

    final var projectDetail = ProjectUtil.getProjectDetails();

    when(testProjectManagementHeadingSectionService.getSupportedProjectType()).thenReturn(projectDetail.getProjectType());

    final var expectedHeadingText = "heading text";
    when(testProjectManagementHeadingSectionService.getHeadingText(projectDetail)).thenReturn(expectedHeadingText);

    final var expectedCaptionText = "caption text";
    when(testProjectManagementHeadingSectionService.getCaptionText(projectDetail)).thenReturn(expectedCaptionText);

    var section = projectManagementHeadingSectionService.getSection(projectDetail, authenticatedUser);

    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementHeadingSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementHeadingSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementHeadingSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).containsExactly(
        entry(ProjectManagementHeadingSectionService.HEADING_TEXT_MODEL_ATTR_NAME, expectedHeadingText),
        entry(ProjectManagementHeadingSectionService.CAPTION_TEXT_MODEL_ATTR_NAME, expectedCaptionText)
    );
  }

  @Test(expected = ProjectManagementHeadingServiceImplementationException.class)
  public void getSection_whenNoSupportedProjectType_thenException() {

    final var projectDetail = ProjectUtil.getProjectDetails();
    projectDetail.setProjectType(null);

    when(testProjectManagementHeadingSectionService.getSupportedProjectType()).thenReturn(ProjectType.INFRASTRUCTURE);

    projectManagementHeadingSectionService.getSection(projectDetail, authenticatedUser);

  }
}
