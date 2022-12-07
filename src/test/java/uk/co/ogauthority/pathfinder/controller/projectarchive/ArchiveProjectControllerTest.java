package uk.co.ogauthority.pathfinder.controller.projectarchive;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectarchive.ArchiveProjectForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectarchive.ArchiveProjectService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ArchiveProjectController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class})
)
public class ArchiveProjectControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PUBLISHED_PROJECT_ID = 1;
  private static final Integer ARCHIVED_PROJECT_ID = 2;

  @MockBean
  private ArchiveProjectService archiveProjectService;

  private final ProjectDetail publishedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);
  private final ProjectDetail archivedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.ARCHIVED);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.ARCHIVE.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestSubmittedDetailOrError(PUBLISHED_PROJECT_ID)).thenReturn(publishedProjectDetail);
    when(projectOperatorService.isUserInProjectTeam(publishedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(publishedProjectDetail, unauthenticatedUser)).thenReturn(false);

    when(projectService.getLatestSubmittedDetailOrError(ARCHIVED_PROJECT_ID)).thenReturn(archivedProjectDetail);
    when(projectOperatorService.isUserInProjectTeam(archivedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(archivedProjectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getArchiveProject_whenAuthenticatedAndPublished_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ArchiveProjectController.class).getArchiveProject(PUBLISHED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getArchiveProject_whenUnauthenticatedAndPublished_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ArchiveProjectController.class).getArchiveProject(PUBLISHED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getArchiveProject_whenAuthenticatedAndArchived_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ArchiveProjectController.class).getArchiveProject(ARCHIVED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void archiveProject_whenValidFormAndPublished_thenArchive() throws Exception {
    var form = new ArchiveProjectForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(archiveProjectService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ArchiveProjectController.class)
            .archiveProject(PUBLISHED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(archiveProjectService, times(1)).validate(any(), any());
    verify(archiveProjectService, times(1)).archiveProject(any(), any(), any());
  }

  @Test
  public void archiveProject_whenInvalidFormAndPublished_thenNoArchive() throws Exception {
    var form = new ArchiveProjectForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(archiveProjectService.getArchiveProjectModelAndView(eq(publishedProjectDetail), eq(authenticatedUser), any())).thenReturn(new ModelAndView());
    when(archiveProjectService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ArchiveProjectController.class)
            .archiveProject(PUBLISHED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(archiveProjectService, times(1)).validate(any(), any());
    verify(archiveProjectService, never()).archiveProject(any(), any(), any());
  }

  @Test
  public void archiveProject_whenUnauthenticatedAndPublished_thenNoAccess() throws Exception {
    var form = new ArchiveProjectForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(archiveProjectService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ArchiveProjectController.class)
            .archiveProject(PUBLISHED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(archiveProjectService, never()).validate(any(), any());
    verify(archiveProjectService, never()).archiveProject(any(), any(), any());
  }

  @Test
  public void archiveProject_whenAuthenticatedAndArchived_thenNoAccess() throws Exception {
    var form = new ArchiveProjectForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(archiveProjectService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ArchiveProjectController.class)
            .archiveProject(ARCHIVED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(archiveProjectService, never()).validate(any(), any());
    verify(archiveProjectService, never()).archiveProject(any(), any(), any());
  }
}
