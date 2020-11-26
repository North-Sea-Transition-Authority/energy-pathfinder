package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.model.form.useraction.ButtonType;
import uk.co.ogauthority.pathfinder.model.form.useraction.LinkButton;
import uk.co.ogauthority.pathfinder.service.team.ManageTeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementActionServiceTest {

  @Mock
  private ManageTeamService manageTeamService;

  @Mock
  private RegulatorActionService regulatorActionService;

  private ProjectManagementActionService projectManagementActionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementActionService = new ProjectManagementActionService(
        manageTeamService,
        regulatorActionService
    );
  }

  @Test
  public void getActions_whenRegulator() {
    var userAction1 = new UserActionWithDisplayOrder(
        new LinkButton("test", "test", true, ButtonType.PRIMARY),
        20
    );
    var userAction2 = new UserActionWithDisplayOrder(
        new LinkButton("test2", "test2", true, ButtonType.SECONDARY),
        10
    );

    var actions = new ArrayList<UserActionWithDisplayOrder>();
    actions.add(userAction1);
    actions.add(userAction2);

    when(manageTeamService.isPersonMemberOfRegulatorTeam(authenticatedUser)).thenReturn(true);
    when(regulatorActionService.getActions(projectDetail, authenticatedUser)).thenReturn(actions);

    assertThat(projectManagementActionService.getActions(projectDetail, authenticatedUser)).containsExactly(
        userAction2,
        userAction1
    );
  }

  @Test
  public void getActions_whenNotRegulator() {
    when(manageTeamService.isPersonMemberOfRegulatorTeam(authenticatedUser)).thenReturn(false);

    assertThat(projectManagementActionService.getActions(projectDetail, authenticatedUser)).isEmpty();
  }
}
