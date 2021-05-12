package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectActionServiceTest {

  private static final int ARCHIVE_ACTION_DISPLAY_ORDER = 10;

  private ProjectActionService projectActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  @Before
  public void setup() {
    projectActionService = new ProjectActionService();
  }

  @Test
  public void getArchiveAction_enabled() {
    var action = projectActionService.getArchiveAction(projectDetail, ARCHIVE_ACTION_DISPLAY_ORDER);

    var linkButton = (LinkButton) action.getUserAction();

    final var expectedButtonPrompt = String.format("Archive %s", projectDetail.getProjectType().getLowercaseDisplayName());
    assertThat(linkButton.getPrompt()).isEqualTo(expectedButtonPrompt);

    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(ArchiveProjectController.class).getArchiveProject(
            project.getId(),
            null,
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isTrue();
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.SECONDARY);

    assertThat(action.getDisplayOrder()).isEqualTo(ARCHIVE_ACTION_DISPLAY_ORDER);
  }
}
