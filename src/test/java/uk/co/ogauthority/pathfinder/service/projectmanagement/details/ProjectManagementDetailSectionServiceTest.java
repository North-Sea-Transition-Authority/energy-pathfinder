package uk.co.ogauthority.pathfinder.service.projectmanagement.details;

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
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.details.ProjectManagementDetailViewUtil;
import uk.co.ogauthority.pathfinder.service.project.location.ProjectLocationService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectLocationTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementDetailSectionServiceTest {

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectLocationService projectLocationService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectManagementDetailSectionService projectManagementDetailSectionService;

  @Before
  public void setup() {
    projectManagementDetailSectionService = new ProjectManagementDetailSectionService(
        projectInformationService,
        projectLocationService,
        webUserAccountService
    );
  }

  @Test
  public void getSection() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var projectLocation = ProjectLocationTestUtil.getProjectLocation_withField(projectDetail);
    var submitterAccount = UserTestingUtil.getAuthenticatedUserAccount();

    when(projectInformationService.getProjectInformationOrError(projectDetail)).thenReturn(projectInformation);
    when(projectLocationService.getOrError(projectDetail)).thenReturn(projectLocation);
    when(webUserAccountService.getWebUserAccountOrError(projectDetail.getSubmittedByWua())).thenReturn(submitterAccount);

    var section = projectManagementDetailSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementDetailSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementDetailSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementDetailSectionService.SECTION_TYPE);

    var projectManagementDetailView = ProjectManagementDetailViewUtil.from(
        projectDetail,
        projectInformation,
        projectLocation,
        submitterAccount
    );
    assertThat(section.getTemplateModel()).containsExactly(
        entry("projectManagementDetailView", projectManagementDetailView)
    );
  }
}
