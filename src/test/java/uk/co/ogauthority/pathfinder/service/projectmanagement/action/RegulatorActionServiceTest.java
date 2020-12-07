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
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorActionServiceTest {

  @Mock
  private ProjectAssessmentContextService projectAssessmentContextService;

  private RegulatorActionService regulatorActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    regulatorActionService = new RegulatorActionService(projectAssessmentContextService);
  }

  @Test
  public void getActions_whenCannotBuildAssessmentContext() {
    when(projectAssessmentContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any())).thenReturn(false);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getProvideAssessmentAction(project.getId(), false)
    );
  }

  @Test
  public void getActions_whenCanBuildAssessmentContext() {
    when(projectAssessmentContextService.canBuildContext(eq(projectDetail), eq(authenticatedUser), any())).thenReturn(true);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getProvideAssessmentAction(project.getId(), true)
    );
  }

  @Test
  public void getProvideAssessmentAction_enabled() {
    var action = regulatorActionService.getProvideAssessmentAction(project.getId(), true);

    assertProvideAssessmentActionFields(action, true);
  }

  @Test
  public void getProvideAssessmentAction_disabled() {
    var action = regulatorActionService.getProvideAssessmentAction(project.getId(), false);

    assertProvideAssessmentActionFields(action, false);
  }

  private void assertProvideAssessmentActionFields(UserActionWithDisplayOrder action, boolean isEnabled) {
    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(RegulatorActionService.PROVIDE_ASSESSMENT_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(ProjectAssessmentController.class).getProjectAssessment(
            project.getId(),
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isEqualTo(isEnabled);
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.PRIMARY);

    assertThat(action.getDisplayOrder()).isEqualTo(RegulatorActionService.PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER);
  }
}
