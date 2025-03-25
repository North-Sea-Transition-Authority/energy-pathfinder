package uk.co.ogauthority.pathfinder.controller.projecttransfer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projecttransfer.ProjectTransferForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projecttransfer.ProjectTransferModelService;
import uk.co.ogauthority.pathfinder.service.projecttransfer.ProjectTransferService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ProjectTransferController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class})
)
public class ProjectTransferControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PUBLISHED_PROJECT_ID = 1;
  private static final Integer UNSUBMITTED_PROJECT_ID = 2;

  @MockitoBean
  private ProjectTransferService projectTransferService;

  @MockitoBean
  private ProjectTransferModelService projectTransferModelService;

  private final ProjectDetail publishedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.PUBLISHED);
  private final ProjectDetail unsubmittedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.TRANSFER.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestSubmittedDetailOrError(PUBLISHED_PROJECT_ID)).thenReturn(publishedProjectDetail);
    when(projectOperatorService.isUserInProjectTeam(publishedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(publishedProjectDetail, unauthenticatedUser)).thenReturn(false);

    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestSubmittedDetailOrError(UNSUBMITTED_PROJECT_ID);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getTransferProject_whenAuthenticatedAndPublished_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectTransferController.class).getTransferProject(PUBLISHED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getTransferProject_whenUnauthenticatedAndPublished_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectTransferController.class).getTransferProject(PUBLISHED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getTransferProject_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectTransferController.class).getTransferProject(UNSUBMITTED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void transferProject_whenValidFormAndPublished_thenTransfer() throws Exception {
    var form = new ProjectTransferForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectTransferService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectTransferController.class)
            .transferProject(PUBLISHED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(projectTransferService, times(1)).validate(any(), any(), any());
    verify(projectTransferService, times(1)).transferProject(any(), any(), any());
  }

  @Test
  public void transferProject_whenInvalidFormAndPublished_thenNoTransfer() throws Exception {
    var form = new ProjectTransferForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(projectTransferModelService.getTransferProjectModelAndView(eq(publishedProjectDetail), eq(authenticatedUser), any())).thenReturn(new ModelAndView());
    when(projectTransferService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectTransferController.class)
            .transferProject(PUBLISHED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(projectTransferService, times(1)).validate(any(), any(), any());
    verify(projectTransferService, never()).transferProject(any(), any(), any());
  }

  @Test
  public void transferProject_whenUnauthenticatedAndPublished_thenNoAccess() throws Exception {
    var form = new ProjectTransferForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectTransferService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectTransferController.class)
            .transferProject(PUBLISHED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectTransferService, never()).validate(any(), any(), any());
    verify(projectTransferService, never()).transferProject(any(), any(), any());
  }

  @Test
  public void transferProject_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    var form = new ProjectTransferForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectTransferService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectTransferController.class)
            .transferProject(UNSUBMITTED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(projectTransferService, never()).validate(any(), any(), any());
    verify(projectTransferService, never()).transferProject(any(), any(), any());
  }
}
