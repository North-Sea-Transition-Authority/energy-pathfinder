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
import uk.co.ogauthority.pathfinder.controller.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorActionServiceTest {

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private ProjectAssessmentContextService projectAssessmentContextService;

  private RegulatorActionService regulatorActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    regulatorActionService = new RegulatorActionService(projectContextService, projectAssessmentContextService);
  }

  @Test
  public void getActions_whenCannotBuildAssessmentContext() {
    when(projectAssessmentContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any(), any())).thenReturn(false);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getProvideAssessmentAction(projectDetail, false)
    );
  }

  @Test
  public void getActions_whenCanBuildAssessmentContext() {
    when(projectAssessmentContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any(), any())).thenReturn(true);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getProvideAssessmentAction(projectDetail, true)
    );
  }

  @Test
  public void getProvideAssessmentAction_enabled() {
    var action = regulatorActionService.getProvideAssessmentAction(projectDetail, true);

    assertProvideAssessmentActionFields(action, true);
  }

  @Test
  public void getProvideAssessmentAction_disabled() {
    var action = regulatorActionService.getProvideAssessmentAction(projectDetail, false);

    assertProvideAssessmentActionFields(action, false);
  }

  private void assertProvideAssessmentActionFields(UserActionWithDisplayOrder action, boolean isEnabled) {
    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(RegulatorActionService.PROVIDE_ASSESSMENT_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(ProjectAssessmentController.class).getProjectAssessment(
            projectDetail.getProject().getId(),
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isEqualTo(isEnabled);
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.PRIMARY);

    assertThat(action.getDisplayOrder()).isEqualTo(RegulatorActionService.PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER);
  }
}
