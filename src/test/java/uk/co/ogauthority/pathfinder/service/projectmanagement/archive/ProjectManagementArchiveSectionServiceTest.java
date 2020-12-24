package uk.co.ogauthority.pathfinder.service.projectmanagement.archive;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.view.projectarchive.ProjectArchiveDetailViewUtil;
import uk.co.ogauthority.pathfinder.service.projectarchive.ArchiveProjectService;
import uk.co.ogauthority.pathfinder.testutil.ProjectArchiveTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementArchiveSectionServiceTest {

  @Mock
  private ArchiveProjectService archiveProjectService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectManagementArchiveSectionService projectManagementArchiveSectionService;

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementArchiveSectionService = new ProjectManagementArchiveSectionService(
        archiveProjectService,
        webUserAccountService
    );
  }

  @Test
  public void getSection_whenArchived_thenReturnView() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectStatus.ARCHIVED);

    var projectArchiveDetail = ProjectArchiveTestUtil.createProjectArchiveDetail(projectDetail);

    when(archiveProjectService.getProjectArchiveDetailOrError(projectDetail)).thenReturn(projectArchiveDetail);
    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getCreatedByWua())).thenReturn(authenticatedUser);

    var section = projectManagementArchiveSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementArchiveSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementArchiveSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementArchiveSectionService.SECTION_TYPE);

    var projectArchiveDetailView = ProjectArchiveDetailViewUtil.from(
        projectArchiveDetail,
        projectDetail.getCreatedDatetime(),
        authenticatedUser
    );
    assertThat(section.getTemplateModel()).containsExactly(
        entry("projectArchiveDetailView", projectArchiveDetailView)
    );
  }

  @Test
  public void getSection_whenNotArchived_thenNoReturnView() {
    var projectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);

    verify(archiveProjectService, never()).getProjectArchiveDetailOrError(projectDetail);

    var section = projectManagementArchiveSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementArchiveSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementArchiveSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementArchiveSectionService.SECTION_TYPE);
    assertThat(section.getTemplateModel()).isEmpty();
  }
}
