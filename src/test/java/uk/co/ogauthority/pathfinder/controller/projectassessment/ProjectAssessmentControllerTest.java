package uk.co.ogauthority.pathfinder.controller.projectassessment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
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
import uk.co.ogauthority.pathfinder.controller.ProjectAssessmentContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.controller.projectmanagement.ManageProjectController;
import uk.co.ogauthority.pathfinder.controller.projectupdate.RegulatorUpdateController;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ProjectAssessmentController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class, ProjectAssessmentContextService.class})
)
public class ProjectAssessmentControllerTest extends ProjectAssessmentContextAbstractControllerTest {

  private static final Integer QA_PROJECT_ID = 1;
  private static final Integer UNSUBMITTED_PROJECT_ID = 2;

  @MockitoBean
  private ProjectAssessmentService projectAssessmentService;

  @MockitoBean
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  private final ProjectDetail qaProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  private final ProjectDetail unsubmittedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.PROVIDE_ASSESSMENT.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestSubmittedDetailOrError(QA_PROJECT_ID)).thenReturn(qaProjectDetail);
    when(projectOperatorService.isUserInProjectTeam(qaProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(qaProjectDetail, unauthenticatedUser)).thenReturn(false);

    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestSubmittedDetailOrError(UNSUBMITTED_PROJECT_ID);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getProjectAssessment_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectAssessmentController.class).getProjectAssessment(QA_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getProjectAssessment_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectAssessmentController.class).getProjectAssessment(QA_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getProjectAssessment_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectAssessmentController.class).getProjectAssessment(UNSUBMITTED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createProjectAssessment_whenValidFormAndReadyToBePublishedAndNoUpdateRequired_thenCreateAndRedirect() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .param("readyToBePublished", "true")
            .param("updateRequired", "false")
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ManageProjectController.class).getProject(QA_PROJECT_ID, null, null, null))));

    verify(projectAssessmentService, times(1)).validate(any(), any(), any());
    verify(projectAssessmentService, times(1)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenValidFormAndReadyToBePublishedAndUpdateRequiredAndCanRequestUpdate_thenCreateAndRedirect() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(regulatorUpdateRequestService.canRequestUpdate(qaProjectDetail)).thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .param("readyToBePublished", "true")
            .param("updateRequired", "true")
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(RegulatorUpdateController.class).getRequestUpdate(QA_PROJECT_ID, null, null))));

    verify(projectAssessmentService, times(1)).validate(any(), any(), any());
    verify(projectAssessmentService, times(1)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenValidFormAndReadyToBePublishedAndUpdateRequiredAndCannotRequestUpdate_thenCreateAndRedirect() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(regulatorUpdateRequestService.canRequestUpdate(qaProjectDetail)).thenReturn(false);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .param("readyToBePublished", "true")
            .param("updateRequired", "true")
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ManageProjectController.class).getProject(QA_PROJECT_ID, null, null, null))));

    verify(projectAssessmentService, times(1)).validate(any(), any(), any());
    verify(projectAssessmentService, times(1)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenValidFormAndNotReadyToBePublishedAndCanRequestUpdate_thenCreateAndRedirect() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(regulatorUpdateRequestService.canRequestUpdate(qaProjectDetail)).thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .param("readyToBePublished", "false")
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(RegulatorUpdateController.class).getRequestUpdate(QA_PROJECT_ID, null, null))));

    verify(projectAssessmentService, times(1)).validate(any(), any(), any());
    verify(projectAssessmentService, times(1)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenValidFormAndNotReadyToBePublishedAndCannotRequestUpdate_thenCreateAndRedirect() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    when(regulatorUpdateRequestService.canRequestUpdate(qaProjectDetail)).thenReturn(false);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .param("readyToBePublished", "false")
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ManageProjectController.class).getProject(QA_PROJECT_ID, null, null, null))));

    verify(projectAssessmentService, times(1)).validate(any(), any(), any());
    verify(projectAssessmentService, times(1)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenInvalidForm_thenNoCreate() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(projectAssessmentService.getProjectAssessmentModelAndView(eq(qaProjectDetail), eq(authenticatedUser), any())).thenReturn(new ModelAndView());
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(projectAssessmentService, times(1)).validate(any(), any(), any());
    verify(projectAssessmentService, times(0)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenUnauthenticated_thenNoAccess() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectAssessmentService, times(0)).validate(any(), any(), any());
    verify(projectAssessmentService, times(0)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(UNSUBMITTED_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(projectAssessmentService, times(0)).validate(any(), any(), any());
    verify(projectAssessmentService, times(0)).createProjectAssessment(any(), any(), any());
  }
}
