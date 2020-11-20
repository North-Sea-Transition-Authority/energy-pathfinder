package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementActionServiceTest {

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  private ProjectManagementActionService projectManagementActionService;

  @Before
  public void setup() {
    projectManagementActionService = new ProjectManagementActionService();
  }

  @Test
  public void getUserActions() {
    assertThat(projectManagementActionService.getUserActions(authenticatedUser)).isEmpty();
  }
}
