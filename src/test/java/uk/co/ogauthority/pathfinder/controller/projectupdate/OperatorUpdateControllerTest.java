package uk.co.ogauthority.pathfinder.controller.projectupdate;

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
import uk.co.ogauthority.pathfinder.controller.ProjectUpdateContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.enums.projectupdate.ProjectUpdateType;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.ProjectUpdateService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = OperatorUpdateController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class, ProjectUpdateContextService.class})
)
public class OperatorUpdateControllerTest extends ProjectUpdateContextAbstractControllerTest {

  private static final Integer QA_PROJECT_ID = 1;
  private static final Integer DRAFT_PROJECT_ID = 2;

  @MockBean
  private OperatorProjectUpdateService operatorProjectUpdateService;

  @MockBean
  private ProjectUpdateService projectUpdateService;

  private final ProjectDetail qaProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  private final ProjectDetail draftProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.PROVIDE_UPDATE.getUserPrivileges());
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
  public void startPage_whenAuthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).startPage(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void startPage_whenUnauthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).startPage(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void startPage_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).startPage(DRAFT_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void startUpdate_whenAuthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .startUpdate(QA_PROJECT_ID, null, null)
        ))
        .with(authenticatedUserAndSession(authenticatedUser))
        .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(projectUpdateService, times(1)).startUpdate(any(), any(), eq(ProjectUpdateType.OPERATOR_INITIATED));
  }

  @Test
  public void startUpdate_whenUnauthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .startUpdate(QA_PROJECT_ID, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectUpdateService, never()).startUpdate(any(), any(), eq(ProjectUpdateType.OPERATOR_INITIATED));
  }

  @Test
  public void startUpdate_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .startUpdate(DRAFT_PROJECT_ID, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectUpdateService, never()).startUpdate(any(), any(), eq(ProjectUpdateType.OPERATOR_INITIATED));
  }

  @Test
  public void provideNoUpdateConfirmation_whenAuthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdateConfirmation(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void provideNoUpdateConfirmation_whenUnauthenticatedAndQA_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdateConfirmation(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void provideNoUpdateConfirmation_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdateConfirmation(DRAFT_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void provideNoUpdate_whenAuthenticatedAndQAAndValidForm_thenCreate() throws Exception {
    var bindingResult = new BeanPropertyBindingResult(ProvideNoUpdateForm.class, "form");
    when(operatorProjectUpdateService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .provideNoUpdate(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(operatorProjectUpdateService, times(1)).validate(any(), any());
    verify(projectUpdateService, times(1)).createNoUpdateNotification(any(), any(), any());
  }

  @Test
  public void provideNoUpdate_whenAuthenticatedAndQAAndInvalidForm_thenNoCreate() throws Exception {
    var bindingResult = new BeanPropertyBindingResult(ProvideNoUpdateForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(operatorProjectUpdateService.validate(any(), any())).thenReturn(bindingResult);

    when(operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .provideNoUpdate(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(operatorProjectUpdateService, times(1)).validate(any(), any());
    verify(projectUpdateService, times(0)).createNoUpdateNotification(any(), any(), any());
  }

  @Test
  public void provideNoUpdate_whenUnauthenticatedAndQA_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .provideNoUpdate(QA_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectUpdateService, never()).createNoUpdateNotification(any(), any(), any());
  }

  @Test
  public void provideNoUpdate_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .provideNoUpdate(DRAFT_PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(projectUpdateService, never()).createNoUpdateNotification(any(), any(), any());
  }
}
