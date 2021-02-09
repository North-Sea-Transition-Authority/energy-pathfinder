package uk.co.ogauthority.pathfinder.service.projectmanagement;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.WorkAreaController;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.model.dto.project.ProjectVersionDto;
import uk.co.ogauthority.pathfinder.model.entity.project.Project;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.projectmanagement.ProjectManagementPageSectionType;
import uk.co.ogauthority.pathfinder.model.view.projectmanagement.ProjectManagementSection;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.ProjectService;
import uk.co.ogauthority.pathfinder.service.project.ProjectVersionService;
import uk.co.ogauthority.pathfinder.service.rendering.TemplateRenderingService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;
import uk.co.ogauthority.pathfinder.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagementViewServiceTest {

  @Mock
  private ProjectService projectService;

  @Mock
  private ProjectManagementService projectManagementService;

  @Mock
  private ProjectVersionService projectVersionService;

  @Mock
  private TemplateRenderingService templateRenderingService;

  private ProjectManagementViewService projectManagementViewService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();
  private final Project project = projectDetail.getProject();
  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    projectManagementViewService = new ProjectManagementViewService(
        projectService,
        projectManagementService,
        projectVersionService,
        templateRenderingService
    );
  }

  @Test
  public void getProjectManagementModelAndView_noSelectedVersion() {
    var modelAndView = projectManagementViewService.getProjectManagementModelAndView(projectDetail, null, authenticatedUser);

    verify(projectService, never()).getDetailOrError(eq(project.getId()), any());

    assertThat(modelAndView.getViewName()).isEqualTo(ProjectManagementViewService.TEMPLATE_PATH);

    var model = modelAndView.getModel();
    assertThat(model).containsOnlyKeys(
        "projectManagementView",
        "backLinkUrl",
        "viewableVersions",
        "form",
        "viewVersionUrl"
    );
    assertThat(model).containsEntry("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)));
    assertThat(model).containsEntry("viewVersionUrl", ReverseRouter.route(on(ManageProjectController.class)
        .updateProjectVersion(project.getId(), null, null, null)));
  }

  @Test
  public void getProjectManagementModelAndView_latestSubmittedSelectedVersion() {
    var modelAndView = projectManagementViewService.getProjectManagementModelAndView(projectDetail, projectDetail.getVersion(), authenticatedUser);

    verify(projectService, never()).getDetailOrError(eq(project.getId()), any());

    assertThat(modelAndView.getViewName()).isEqualTo(ProjectManagementViewService.TEMPLATE_PATH);

    var model = modelAndView.getModel();
    assertThat(model).containsOnlyKeys(
        "projectManagementView",
        "backLinkUrl",
        "viewableVersions",
        "form",
        "viewVersionUrl"
    );
    assertThat(model).containsEntry("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)));
    assertThat(model).containsEntry("viewVersionUrl", ReverseRouter.route(on(ManageProjectController.class)
        .updateProjectVersion(project.getId(), null, null, null)));
  }

  @Test
  public void getProjectManagementModelAndView_otherSelectedVersion() {
    var selectedVersion = projectDetail.getVersion() - 1;

    when(projectService.getDetailOrError(project.getId(), selectedVersion)).thenReturn(projectDetail);

    var modelAndView = projectManagementViewService.getProjectManagementModelAndView(projectDetail, selectedVersion, authenticatedUser);

    verify(projectService, times(1)).getDetailOrError(project.getId(), selectedVersion);

    assertThat(modelAndView.getViewName()).isEqualTo(ProjectManagementViewService.TEMPLATE_PATH);

    var model = modelAndView.getModel();
    assertThat(model).containsOnlyKeys(
        "projectManagementView",
        "backLinkUrl",
        "viewableVersions",
        "form",
        "viewVersionUrl"
    );
    assertThat(model).containsEntry("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null, null)));
    assertThat(model).containsEntry("viewVersionUrl", ReverseRouter.route(on(ManageProjectController.class)
        .updateProjectVersion(project.getId(), null, null, null)));
  }

  @Test
  public void getProjectManagementView() {
    var stubRender = "FAKE";
    var sectionName1 = "text";
    var sectionName2 = "text2";
    var sectionName3 = "text3";

    when(projectManagementService.getSections(projectDetail, projectDetail, authenticatedUser)).thenReturn(List.of(
        new ProjectManagementSection(sectionName1, Map.of("test", "1"), 1, ProjectManagementPageSectionType.STATIC_CONTENT),
        new ProjectManagementSection(sectionName2, Map.of("test", "2"), 2, ProjectManagementPageSectionType.VERSION_CONTENT),
        new ProjectManagementSection(sectionName3, Map.of("test", "3"), 3, ProjectManagementPageSectionType.VERSION_CONTENT)
    ));
    when(templateRenderingService.render(any(), any(), anyBoolean())).thenReturn(stubRender);

    var projectManagementView = projectManagementViewService.getProjectManagementView(projectDetail, projectDetail, authenticatedUser);
    assertThat(projectManagementView.getStaticContentHtml()).isEqualTo(stubRender);
    assertThat(projectManagementView.getVersionContentHtml()).isEqualTo(stubRender + stubRender);
  }

  @Test
  public void getViewableVersionsMap() {
    var projectVersionDto1 = new ProjectVersionDto(1, Instant.now().minus(7, ChronoUnit.DAYS), false);
    var projectVersionDto2 = new ProjectVersionDto(2, Instant.now().minus(1, ChronoUnit.DAYS), true);

    when(projectVersionService.getSubmittedProjectVersionDtos(project)).thenReturn(List.of(
        projectVersionDto1,
        projectVersionDto2
    ));

    var viewableVersions = projectManagementViewService.getViewableVersionsMap(project);

    assertThat(viewableVersions).containsExactly(
        entry("1", projectManagementViewService.getViewableVersionDescription(projectVersionDto1)),
        entry("2", projectManagementViewService.getViewableVersionDescription(projectVersionDto2))
    );
  }

  @Test
  public void getViewableVersionDescription_noChange() {
    var now = Instant.now();
    var dto = new ProjectVersionDto(2, now, true);
    var desc = projectManagementViewService.getViewableVersionDescription(dto);
    assertThat(desc).isEqualTo(String.format("(%s) Submitted: %s %s", dto.getVersion(), DateUtil.formatInstant(dto.getSubmittedInstant()), " (No change)"));
  }

  @Test
  public void getViewableVersionDescription_regularUpdate() {
    var now = Instant.now();
    var dto = new ProjectVersionDto(2, now, false);
    var desc = projectManagementViewService.getViewableVersionDescription(dto);
    assertThat(desc).isEqualTo(String.format("(%s) Submitted: %s %s", dto.getVersion(), DateUtil.formatInstant(dto.getSubmittedInstant()), ""));
  }
}
