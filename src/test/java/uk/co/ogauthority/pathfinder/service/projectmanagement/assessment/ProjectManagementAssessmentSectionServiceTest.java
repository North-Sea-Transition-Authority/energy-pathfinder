package uk.co.ogauthority.pathfinder.service.projectmanagement.assessment;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.energyportal.service.webuser.WebUserAccountService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.projectassessment.ProjectAssessmentViewUtil;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.team.ManageTeamService;
import uk.co.ogauthority.pathfinder.testutil.ProjectAssessmentTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementAssessmentSectionServiceTest {

  @Mock
  private ProjectAssessmentService projectAssessmentService;

  @Mock
  private ManageTeamService manageTeamService;

  @Mock
  private WebUserAccountService webUserAccountService;

  private ProjectManagementAssessmentSectionService projectManagementAssessmentSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementAssessmentSectionService = new ProjectManagementAssessmentSectionService(
        projectAssessmentService,
        manageTeamService,
        webUserAccountService
    );
  }

  @Test
  public void getSection_whenRegulatorAndAssessed_thenReturnView() {
    var projectAssessment = ProjectAssessmentTestUtil.createProjectAssessment();

    when(manageTeamService.isPersonMemberOfRegulatorTeam(authenticatedUser)).thenReturn(true);
    when(projectAssessmentService.getProjectAssessment(projectDetail)).thenReturn(Optional.of(projectAssessment));
    when(webUserAccountService.getWebUserAccountOrError(projectAssessment.getAssessorWuaId())).thenReturn(authenticatedUser);

    var section = projectManagementAssessmentSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementAssessmentSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementAssessmentSectionService.DISPLAY_ORDER);

    var projectAssessmentView = ProjectAssessmentViewUtil.from(projectAssessment, authenticatedUser);
    assertThat(section.getTemplateModel()).containsExactly(
        entry("projectAssessmentView", projectAssessmentView)
    );
  }

  @Test
  public void getSection_whenNotRegulator_thenNoView() {
    var section = projectManagementAssessmentSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementAssessmentSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementAssessmentSectionService.DISPLAY_ORDER);

    assertThat(section.getTemplateModel()).isEmpty();
  }

  @Test
  public void getSection_whenNotAssessed_thenNoView() {
    when(manageTeamService.isPersonMemberOfRegulatorTeam(authenticatedUser)).thenReturn(true);
    when(projectAssessmentService.getProjectAssessment(projectDetail)).thenReturn(Optional.empty());

    var section = projectManagementAssessmentSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementAssessmentSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementAssessmentSectionService.DISPLAY_ORDER);

    assertThat(section.getTemplateModel()).isEmpty();
  }
}
