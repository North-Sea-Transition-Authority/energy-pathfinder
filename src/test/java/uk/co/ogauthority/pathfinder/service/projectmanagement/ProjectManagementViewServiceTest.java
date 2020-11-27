package uk.co.ogauthority.pathfinder.service.projectmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementView;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectVersionService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementViewServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectManagementService projectManagementService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private ProjectVersionService projectVersionService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private ProjectManagementViewService projectManagementViewService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementViewService = new ProjectManagementViewService(
        projectService,
        projectManagementService,
        projectInformationService,
        projectOperatorService,
        projectVersionService,
        templateRenderingService
    );
  }

  @Test
  public void getProjectManagementModelAndView() {
    var stubRender = "FAKE";
    var sectionName1 = "text";
    var sectionName2 = "text2";
    var sectionName3 = "text3";

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var projectOperator = ProjectOperatorTestUtil.getOperator();

    when(projectInformationService.getProjectInformationOrError(projectDetail)).thenReturn(
        projectInformation
    );
    when(projectOperatorService.getProjectOperatorByProjectDetailOrError(projectDetail)).thenReturn(
        projectOperator
    );
    when(projectManagementService.getSections(projectDetail, projectDetail, authenticatedUser)).thenReturn(List.of(
        new ProjectManagementSection(sectionName1, Map.of("test", "1"), 1, ProjectManagementPageSectionType.STATIC_CONTENT),
        new ProjectManagementSection(sectionName2, Map.of("test", "2"), 2, ProjectManagementPageSectionType.VERSION_CONTENT),
        new ProjectManagementSection(sectionName3, Map.of("test", "3"), 3, ProjectManagementPageSectionType.VERSION_CONTENT)
    ));

    when(templateRenderingService.render(any(), any(), anyBoolean())).thenReturn(stubRender);

    var modelAndView = projectManagementViewService.getProjectManagementModelAndView(projectDetail, null, authenticatedUser);

    var projectManagementView = (ProjectManagementView) modelAndView.getModel().get("projectManagementView");
    assertThat(projectManagementView.getTitle()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(projectManagementView.getOperator()).isEqualTo(projectOperator.getOrganisationGroup().getName());
    assertThat(projectManagementView.getStaticContentHtml()).isEqualTo(stubRender);
    assertThat(projectManagementView.getVersionContentHtml()).isEqualTo(stubRender + stubRender);
  }
}
