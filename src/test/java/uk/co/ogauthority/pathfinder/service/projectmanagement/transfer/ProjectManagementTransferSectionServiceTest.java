package uk.co.ogauthority.pathfinder.service.projectmanagement.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projecttransfer.ProjectTransferViewUtil;
import uk.co.ogauthority.pathfinder.service.projecttransfer.ProjectTransferService;
import uk.co.ogauthority.pathfinder.testutil.ProjectTransferTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementTransferSectionServiceTest {

  @Mock
  private ProjectTransferService projectTransferService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectManagementTransferSectionService projectManagementTransferSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementTransferSectionService = new ProjectManagementTransferSectionService(
        projectTransferService,
        webUserAccountService
    );
  }

  @Test
  public void getSection_whenTransfer_thenReturnView() {

    final var projectTransfer = ProjectTransferTestUtil.createProjectTransfer();
    final var expectedProjectTransferView = ProjectTransferViewUtil.from(projectTransfer, authenticatedUser);

    when(projectTransferService.getProjectTransfer(projectDetail)).thenReturn(Optional.of(projectTransfer));
    when(webUserAccountService.getWebUserAccountOrError(projectTransfer.getTransferredByWuaId())).thenReturn(authenticatedUser);

    final var resultingSection = projectManagementTransferSectionService.getSection(
        projectDetail,
        authenticatedUser
    );

    assertCommonSectionProperties(resultingSection);

    assertThat(resultingSection.getTemplateModel()).containsExactlyInAnyOrderEntriesOf(
        Map.of(
            "projectTransferView", expectedProjectTransferView,
            "isPublishedAsOperator", projectTransfer.isPublishedAsOperator()
        )
    );
  }

  @Test
  public void getSection_whenNoTransfer_thenNoView() {
    when(projectTransferService.getProjectTransfer(projectDetail)).thenReturn(Optional.empty());

    final var resultingSection = projectManagementTransferSectionService.getSection(
        projectDetail,
        authenticatedUser
    );

    assertCommonSectionProperties(resultingSection);
    assertThat(resultingSection.getTemplateModel()).isEmpty();
  }

  private void assertCommonSectionProperties(ProjectManagementSection projectManagementSection) {
    assertThat(projectManagementSection.getTemplatePath()).isEqualTo(ProjectManagementTransferSectionService.TEMPLATE_PATH);
    assertThat(projectManagementSection.getDisplayOrder()).isEqualTo(ProjectManagementTransferSectionService.DISPLAY_ORDER);
    assertThat(projectManagementSection.getSectionType()).isEqualTo(ProjectManagementTransferSectionService.SECTION_TYPE);
  }
}
