package uk.co.ogauthority.pathfinder.service.projectmanagement.heading;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementHeadingSectionServiceTest {

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  private ProjectManagementHeadingSectionService projectManagementHeadingSectionService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementHeadingSectionService = new ProjectManagementHeadingSectionService(
        projectInformationService,
        projectOperatorService
    );
  }

  @Test
  public void getSection() {
    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var projectOperator = ProjectOperatorTestUtil.getOperator();

    when(projectInformationService.getProjectInformationOrError(projectDetail)).thenReturn(projectInformation);
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(projectOperator);

    var section = projectManagementHeadingSectionService.getSection(projectDetail, authenticatedUser);
    assertThat(section.getTemplatePath()).isEqualTo(ProjectManagementHeadingSectionService.TEMPLATE_PATH);
    assertThat(section.getDisplayOrder()).isEqualTo(ProjectManagementHeadingSectionService.DISPLAY_ORDER);
    assertThat(section.getSectionType()).isEqualTo(ProjectManagementHeadingSectionService.SECTION_TYPE);

    assertThat(section.getTemplateModel()).containsOnly(
        entry("projectTitle", projectInformation.getProjectTitle()),
        entry("projectOperator", projectOperator.getOrganisationGroup().getName())
    );
  }
}
