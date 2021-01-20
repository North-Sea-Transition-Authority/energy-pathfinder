package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.project.TaskListController;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class OperatorActionServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectActionService projectActionService;

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private OperatorProjectUpdateContextService operatorProjectUpdateContextService;

  private OperatorActionService operatorActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    operatorActionService = new OperatorActionService(
        projectService,
        projectActionService,
        projectContextService,
        operatorProjectUpdateContextService
    );
  }

  @Test
  public void getActions_whenCannotUpdateAndCannotResumeUpdate() {
    when(operatorProjectUpdateContextService.canBuildContext(projectDetail, authenticatedUser, OperatorUpdateController.class)).thenReturn(false);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).isEmpty();
  }

  @Test
  public void getActions_whenCannotUpdateAndCanResumeUpdate() {
    var latestProjectDetail = ProjectUtil.getProjectDetails();

    when(operatorProjectUpdateContextService.canBuildContext(projectDetail, authenticatedUser, OperatorUpdateController.class)).thenReturn(false);
    when(projectService.getLatestDetailOrError(project.getId())).thenReturn(latestProjectDetail);
    when(projectContextService.canBuildContext(latestProjectDetail, authenticatedUser, TaskListController.class)).thenReturn(true);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        operatorActionService.getResumeUpdateAction(project.getId())
    );
  }

  @Test
  public void getActions_whenCanUpdate() {
    when(operatorProjectUpdateContextService.canBuildContext(projectDetail, authenticatedUser, OperatorUpdateController.class)).thenReturn(true);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        operatorActionService.getProvideUpdateAction(project.getId()),
        operatorActionService.getProvideNoUpdateNotificationAction(project.getId())
    );
  }

  @Test
  public void getActions_whenCannotArchive() {
    when(projectContextService.canBuildContext(projectDetail, authenticatedUser, ArchiveProjectController.class)).thenReturn(false);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).isEmpty();
  }

  @Test
  public void getActions_whenCanArchive() {
    when(projectContextService.canBuildContext(projectDetail, authenticatedUser, ArchiveProjectController.class)).thenReturn(true);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        projectActionService.getArchiveAction(project.getId(), OperatorActionService.ARCHIVE_ACTION_DISPLAY_ORDER)
    );
  }

  @Test
  public void getProvideUpdateAction() {
    var action = operatorActionService.getProvideUpdateAction(project.getId());

    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(OperatorActionService.PROVIDE_UPDATE_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(OperatorUpdateController.class).startPage(project.getId(), null))
    );
    assertThat(linkButton.getEnabled()).isTrue();
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.PRIMARY);

    assertThat(action.getDisplayOrder()).isEqualTo(OperatorActionService.PROVIDE_UPDATE_ACTION_DISPLAY_ORDER);
  }

  @Test
  public void getProvideNoUpdateNotificationAction() {
    var action = operatorActionService.getProvideNoUpdateNotificationAction(project.getId());

    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(OperatorActionService.PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(OperatorUpdateController.class).provideNoUpdate(
            project.getId(),
            null,
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isTrue();
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.SECONDARY);

    assertThat(action.getDisplayOrder()).isEqualTo(OperatorActionService.PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_DISPLAY_ORDER);
  }
}
