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
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class OperatorActionServiceTest {

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private ProjectUpdateContextService projectUpdateContextService;

  private OperatorActionService operatorActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    operatorActionService = new OperatorActionService(projectContextService, projectUpdateContextService);
  }

  @Test
  public void getActions_whenCannotBuildUpdateContext() {
    when(projectUpdateContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any(), any())).thenReturn(false);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        operatorActionService.getProvideUpdateAction(projectDetail, false)
    );
  }

  @Test
  public void getActions_whenCanBuildAssessmentContext() {
    when(projectUpdateContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any(), any())).thenReturn(true);

    var actions = operatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        operatorActionService.getProvideUpdateAction(projectDetail, true)
    );
  }

  @Test
  public void getProvideUpdateAction_enabled() {
    var action = operatorActionService.getProvideUpdateAction(projectDetail, true);

    assertProvideUpdateActionFields(action, true);
  }

  @Test
  public void getProvideUpdateAction_disabled() {
    var action = operatorActionService.getProvideUpdateAction(projectDetail, false);

    assertProvideUpdateActionFields(action, false);
  }

  private void assertProvideUpdateActionFields(UserActionWithDisplayOrder action, boolean isEnabled) {
    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(OperatorActionService.PROVIDE_UPDATE_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(OperatorUpdateController.class).startPage(
            projectDetail.getProject().getId(),
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isEqualTo(isEnabled);
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.PRIMARY);

    assertThat(action.getDisplayOrder()).isEqualTo(OperatorActionService.PROVIDE_UPDATE_ACTION_DISPLAY_ORDER);
  }
}
