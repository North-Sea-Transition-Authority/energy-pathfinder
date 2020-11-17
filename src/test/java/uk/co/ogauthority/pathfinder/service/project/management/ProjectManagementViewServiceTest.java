package uk.co.ogauthority.pathfinder.service.project.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.view.management.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.service.project.ProjectOperatorService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectInformationUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectOperatorTestUtil;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementViewServiceTest {

  @Mock
  private ProjectManagementService projectManagementService;

  @Mock
  private ProjectInformationService projectInformationService;

  @Mock
  private ProjectOperatorService projectOperatorService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private ProjectManagementViewService projectManagementViewService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementViewService = new ProjectManagementViewService(
        projectManagementService,
        projectInformationService,
        projectOperatorService,
        templateRenderingService
    );
  }

  @Test
  public void getProjectManagementView() {
    var stubRender = "FAKE";
    var sectionName1 = "text";
    var sectionName2 = "text2";

    var projectInformation = ProjectInformationUtil.getProjectInformation_withCompleteDetails(projectDetail);
    var projectOperator = ProjectOperatorTestUtil.getOperator();

    when(projectInformationService.getProjectInformationOrError(projectDetail)).thenReturn(
        projectInformation
    );
    when(projectOperatorService.getProjectOperatorByProjectDetail(projectDetail)).thenReturn(
        Optional.of(projectOperator)
    );
    when(projectManagementService.getSections(projectDetail, authenticatedUser)).thenReturn(List.of(
        new ProjectManagementSection(sectionName1, Map.of("test", "1"), 1),
        new ProjectManagementSection(sectionName2, Map.of("test", "2"), 2)
    ));

    when(templateRenderingService.render(any(), any(), anyBoolean())).thenReturn(stubRender);

    var projectManagementView = projectManagementViewService.getProjectManagementView(
        projectDetail,
        authenticatedUser
    );

    assertThat(projectManagementView.getTitle()).isEqualTo(projectInformation.getProjectTitle());
    assertThat(projectManagementView.getOperator()).isEqualTo(projectOperator.getOrganisationGroup().getName());
    assertThat(projectManagementView.getSectionsHtml()).isEqualTo(stubRender + stubRender);
  }
}
