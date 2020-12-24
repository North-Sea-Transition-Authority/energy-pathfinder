package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectupdate.OperatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class OperatorActionServiceTest {

  @Mock
  private ProjectActionService projectActionService;

  @Mock
  private ProjectUpdateContextService projectUpdateContextService;

  private OperatorActionService operatorActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    operatorActionService = new OperatorActionService(projectActionService, projectUpdateContextService);
  }

  @Test
  public void getActions_whenCannotBuildUpdateContext() {
    when(projectUpdateContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any())).thenReturn(false);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        operatorActionService.getProvideUpdateAction(project.getId(), false),
        operatorActionService.getProvideNoUpdateNotificationAction(project.getId(), false),
        projectActionService.getArchiveAction(project.getId(), OperatorActionService.ARCHIVE_ACTION_DISPLAY_ORDER, false) // FIXME
    );
  }

  @Test
  public void getActions_whenCanBuildUpdateContext() {
    when(projectUpdateContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any())).thenReturn(true);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        operatorActionService.getProvideUpdateAction(project.getId(), true),
        operatorActionService.getProvideNoUpdateNotificationAction(project.getId(), true),
        projectActionService.getArchiveAction(project.getId(), OperatorActionService.ARCHIVE_ACTION_DISPLAY_ORDER, true) // FIXME
    );
  }

  @Test
  public void getProvideUpdateAction_enabled() {
    var action = operatorActionService.getProvideUpdateAction(project.getId(), true);

    assertProvideUpdateActionFields(action, true);
  }

  @Test
  public void getProvideUpdateAction_disabled() {
    var action = operatorActionService.getProvideUpdateAction(project.getId(), false);

    assertProvideUpdateActionFields(action, false);
  }

  private void assertProvideUpdateActionFields(UserActionWithDisplayOrder action, boolean isEnabled) {
    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(OperatorActionService.PROVIDE_UPDATE_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(OperatorUpdateController.class).startPage(project.getId(), null))
    );
    assertThat(linkButton.getEnabled()).isEqualTo(isEnabled);
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.PRIMARY);

    assertThat(action.getDisplayOrder()).isEqualTo(OperatorActionService.PROVIDE_UPDATE_ACTION_DISPLAY_ORDER);
  }

  @Test
  public void getProvideNoUpdateNotificationAction_enabled() {
    var action = operatorActionService.getProvideNoUpdateNotificationAction(project.getId(), true);

    assertProvideNoUpdateNotificationActionFields(action, true);
  }

  @Test
  public void getProvideNoUpdateNotificationAction_disabled() {
    var action = operatorActionService.getProvideNoUpdateNotificationAction(project.getId(), false);

    assertProvideNoUpdateNotificationActionFields(action, false);
  }

  private void assertProvideNoUpdateNotificationActionFields(UserActionWithDisplayOrder action, boolean isEnabled) {
    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(OperatorActionService.PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(OperatorUpdateController.class).provideNoUpdate(
            project.getId(),
            null,
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isEqualTo(isEnabled);
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.SECONDARY);

    assertThat(action.getDisplayOrder()).isEqualTo(OperatorActionService.PROVIDE_NO_UPDATE_NOTIFICATION_ACTION_DISPLAY_ORDER);
  }
}
