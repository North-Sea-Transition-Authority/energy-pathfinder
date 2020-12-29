package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.projectarchive.ArchiveProjectController;
import uk.co.ogauthority.pathfinder.controller.projectassessment.ProjectAssessmentController;
import uk.co.ogauthority.pathfinder.controller.projecttransfer.ProjectTransferController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class RegulatorActionServiceTest {

  @Mock
  private ProjectActionService projectActionService;

  @Mock
  private ProjectContextService projectContextService;

  @Mock
  private ProjectAssessmentContextService projectAssessmentContextService;

  @Mock
  private ProjectUpdateContextService projectUpdateContextService;

  private RegulatorActionService regulatorActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    regulatorActionService = new RegulatorActionService(
        projectActionService,
        projectContextService,
        projectAssessmentContextService,
        projectUpdateContextService
    );
  }

  @Test
  public void getActions_whenCannotAssess() {
    when(projectAssessmentContextService.canBuildContext(projectDetail, authenticatedUser, ProjectAssessmentController.class)).thenReturn(false);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).isEmpty();
  }

  @Test
  public void getActions_whenCanAssess() {
    when(projectAssessmentContextService.canBuildContext(projectDetail, authenticatedUser, ProjectAssessmentController.class)).thenReturn(true);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getProvideAssessmentAction(project.getId())
    );
  }

  @Test
  public void getActions_whenCannotRequestUpdate() {
    when(projectUpdateContextService.canBuildContext(projectDetail, authenticatedUser, RegulatorUpdateController.class)).thenReturn(false);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).isEmpty();
  }

  @Test
  public void getActions_whenCanRequestUpdate() {
    when(projectUpdateContextService.canBuildContext(projectDetail, authenticatedUser, RegulatorUpdateController.class)).thenReturn(true);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getRequestUpdateAction(project.getId())
    );
  }

  @Test
  public void getActions_whenCannotTransferProject() {
    when(projectContextService.canBuildContext(projectDetail, authenticatedUser, ProjectTransferController.class)).thenReturn(false);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).isEmpty();
  }

  @Test
  public void getActions_whenCanTransferProject() {
    when(projectContextService.canBuildContext(projectDetail, authenticatedUser, ProjectTransferController.class)).thenReturn(true);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        regulatorActionService.getTransferProjectAction(project.getId())
    );
  }

  @Test
  public void getActions_whenCannotArchive() {
    when(projectContextService.canBuildContext(projectDetail, authenticatedUser, ArchiveProjectController.class)).thenReturn(false);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).isEmpty();
  }

  @Test
  public void getActions_whenCanArchive() {
    when(projectContextService.canBuildContext(projectDetail, authenticatedUser, ArchiveProjectController.class)).thenReturn(true);

    var actions = regulatorActionService.getActions(projectDetail, authenticatedUser);

    assertThat(actions).containsExactly(
        projectActionService.getArchiveAction(project.getId(), RegulatorActionService.ARCHIVE_ACTION_DISPLAY_ORDER)
    );
  }

  @Test
  public void getProvideAssessmentAction() {
    var action = regulatorActionService.getProvideAssessmentAction(project.getId());

    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(RegulatorActionService.PROVIDE_ASSESSMENT_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(ProjectAssessmentController.class).getProjectAssessment(
            project.getId(),
            null,
            null
        ))
    );
    assertThat(linkButton.getEnabled()).isTrue();
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.PRIMARY);

    assertThat(action.getDisplayOrder()).isEqualTo(RegulatorActionService.PROVIDE_ASSESSMENT_ACTION_DISPLAY_ORDER);
  }

  @Test
  public void getRequestUpdateAction() {
    var action = regulatorActionService.getRequestUpdateAction(project.getId());

    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(RegulatorActionService.REQUEST_UPDATE_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(RegulatorUpdateController.class).getRequestUpdate(project.getId(), null, null))
    );
    assertThat(linkButton.getEnabled()).isTrue();
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.SECONDARY);

    assertThat(action.getDisplayOrder()).isEqualTo(RegulatorActionService.REQUEST_UPDATE_ACTION_DISPLAY_ORDER);
  }

  @Test
  public void getTransferProjectUpdateAction() {
    var action = regulatorActionService.getTransferProjectAction(project.getId());

    var linkButton = (LinkButton) action.getUserAction();
    assertThat(linkButton.getPrompt()).isEqualTo(RegulatorActionService.TRANSFER_PROJECT_ACTION_PROMPT);
    assertThat(linkButton.getUrl()).isEqualTo(
        ReverseRouter.route(on(ProjectTransferController.class).getTransferProject(project.getId(), null, null))
    );
    assertThat(linkButton.getEnabled()).isTrue();
    assertThat(linkButton.getButtonType()).isEqualTo(ButtonType.SECONDARY);

    assertThat(action.getDisplayOrder()).isEqualTo(RegulatorActionService.TRANSFER_PROJECT_ACTION_DISPLAY_ORDER);
  }
}
