package uk.co.ogauthority.pathfinder.service.projectmanagement.notification;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementNotificationSectionServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectManagementNotificationSectionService projectManagementNotificationSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementNotificationSectionService = new ProjectManagementNotificationSectionService(
        projectService,
        webUserAccountService
    );
  }

  @Test
  public void getSection_whenUpdateInProgress() {
    var latestProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

    when(projectService.getLatestDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);

    var updateCreatedByUser = UserTestingUtil.getAuthenticatedUserAccount();

    when(webUserAccountService.getWebUserAccountOrError(latestProjectDetail.getCreatedByWua())).thenReturn(updateCreatedByUser);

    var section = projectManagementNotificationSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementNotificationSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementNotificationSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementNotificationSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).containsExactly(
        entry("isUpdateInProgress", true),
        entry("updateCreatedByUserName", updateCreatedByUser.getFullName()),
        entry("updateCreatedByUserEmailAddress", updateCreatedByUser.getEmailAddress())
    );
  }

  @Test
  public void getSection_whenNoUpdateInProgress() {
    var latestProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

    when(projectService.getLatestDetailOrError(projectDetail.getProject().getId())).thenReturn(latestProjectDetail);

    var section = projectManagementNotificationSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementNotificationSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementNotificationSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementNotificationSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).containsExactly(
        entry("isUpdateInProgress", false)
    );
  }
}
