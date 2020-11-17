package uk.co.ogauthority.pathfinder.service.project.management.action;

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
import uk.co.ogauthority.pathfinder.model.form.useraction.UserAction;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementActionSectionServiceTest {

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Mock
  private ProjectManagementActionService projectManagementActionService;

  private ProjectManagementActionSectionService projectManagementActionSectionService;

  @Before
  public void setup() {
    projectManagementActionSectionService = new ProjectManagementActionSectionService(projectManagementActionService);
  }

  @Test
  public void getSection() {
    List<UserAction> actions = Collections.emptyList();

    when(projectManagementActionService.getUserActions(authenticatedUser)).thenReturn(actions);

    var section = projectManagementActionSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementActionSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementActionSectionService.DISPLAY_ORDER);
    assertThat(section.getTemplateModel()).containsExactly(
        entry("actions", actions)
    );
  }
}
