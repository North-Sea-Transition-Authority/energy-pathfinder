package uk.co.ogauthority.pathfinder.service.projectmanagement.action;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.form.useraction.UserActionWithDisplayOrder;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementActionSectionServiceTest {

  @Mock
  private ProjectManagementActionService projectManagementActionService;

  private ProjectManagementActionSectionService projectManagementActionSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementActionSectionService = new ProjectManagementActionSectionService(projectManagementActionService);
  }

  @Test
  public void getSection() {
    List<UserActionWithDisplayOrder> actions = Collections.emptyList();

    when(projectManagementActionService.getActions(projectDetail, authenticatedUser)).thenReturn(actions);

    var section = projectManagementActionSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementActionSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementActionSectionService.DISPLAY_ORDER);
    assertThat(section.getTemplateModel()).containsExactly(
        entry("actions", actions)
    );
  }
}
