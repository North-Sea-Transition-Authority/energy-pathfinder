package uk.co.ogauthority.pathfinder.controller.projectassessment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Optional;
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
import uk.co.ogauthority.pathfinder.controller.ProjectAssessmentContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectassessment.ProjectAssessmentForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentContextService;
import uk.co.ogauthority.pathfinder.service.projectassessment.ProjectAssessmentService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = ProjectAssessmentController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class, ProjectAssessmentContextService.class})
)
public class ProjectAssessmentControllerTest extends ProjectAssessmentContextAbstractControllerTest {

  private static final Integer QA_PROJECT_ID = 1;
  private static final Integer DRAFT_PROJECT_ID = 2;

  @MockBean
  private ProjectAssessmentService projectAssessmentService;

  private final ProjectDetail qaProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  private final ProjectDetail draftProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.PROVIDE_ASSESSMENT.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestDetail(QA_PROJECT_ID)).thenReturn(Optional.of(qaProjectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(qaProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(qaProjectDetail, unauthenticatedUser)).thenReturn(false);

    when(projectService.getLatestDetail(DRAFT_PROJECT_ID)).thenReturn(Optional.of(draftProjectDetail));
    when(projectOperatorService.isUserInProjectTeamOrRegulator(draftProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(draftProjectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getProjectAssessment_whenAuthenticated_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectAssessmentController.class).getProjectAssessment(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getProjectAssessment_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectAssessmentController.class).getProjectAssessment(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getProjectAssessment_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectAssessmentController.class).getProjectAssessment(DRAFT_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createProjectAssessment_whenValidForm_thenCreate() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(projectAssessmentService, times(1)).validate(any(), any());
    verify(projectAssessmentService, times(1)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenInvalidForm_thenNoCreate() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(projectAssessmentService.getProjectAssessmentModelAndView(eq(QA_PROJECT_ID), any())).thenReturn(new ModelAndView());
    when(projectAssessmentService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(projectAssessmentService, times(1)).validate(any(), any());
    verify(projectAssessmentService, times(0)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenUnauthenticated_thenNoAccess() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectAssessmentService, times(0)).validate(any(), any());
    verify(projectAssessmentService, times(0)).createProjectAssessment(any(), any(), any());
  }

  @Test
  public void createProjectAssessment_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    var form = new ProjectAssessmentForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(projectAssessmentService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectAssessmentController.class)
            .createProjectAssessment(DRAFT_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectAssessmentService, times(0)).validate(any(), any());
    verify(projectAssessmentService, times(0)).createProjectAssessment(any(), any(), any());
  }
}
