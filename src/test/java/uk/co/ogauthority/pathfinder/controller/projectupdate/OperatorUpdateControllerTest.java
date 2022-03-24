package uk.co.ogauthority.pathfinder.controller.projectupdate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import java.util.Map;
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
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.OperatorProjectUpdateContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.exception.AccessDeniedException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.ProvideNoUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateService;
import uk.co.ogauthority.pathfinder.service.projectupdate.OperatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = OperatorUpdateController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class, OperatorProjectUpdateContextService.class})
)
public class OperatorUpdateControllerTest extends OperatorProjectUpdateContextAbstractControllerTest {

  private static final Integer QA_PROJECT_ID = 1;
  private static final Integer DRAFT_PROJECT_ID = 2;

  @MockBean
  private OperatorProjectUpdateService operatorProjectUpdateService;

  private final ProjectDetail qaProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  private final ProjectDetail draftProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.PROVIDE_UPDATE.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestDetailOrError(QA_PROJECT_ID)).thenReturn(qaProjectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(qaProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(qaProjectDetail, unauthenticatedUser)).thenReturn(false);

    when(projectService.getLatestDetailOrError(DRAFT_PROJECT_ID)).thenReturn(draftProjectDetail);
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

    verify(operatorProjectUpdateService, times(1)).startUpdate(any(), any());
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

    verify(operatorProjectUpdateService, never()).startUpdate(any(), any());
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

    verify(operatorProjectUpdateService, never()).startUpdate(any(), any());
  }

  @Test
  public void provideNoUpdate_whenAuthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdate(QA_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void provideNoUpdate_whenUnauthenticatedAndQA_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdate(QA_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void provideNoUpdate_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdate(DRAFT_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveNoUpdate_whenAuthenticatedAndQAAndValidForm_thenCreate() throws Exception {
    var bindingResult = new BeanPropertyBindingResult(ProvideNoUpdateForm.class, "form");
    when(operatorProjectUpdateService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .saveNoUpdate(QA_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(operatorProjectUpdateService, times(1)).validate(any(), any());
    verify(operatorProjectUpdateService, times(1)).createNoUpdateNotification(any(), any(), any());
    verify(analyticsService, times(1))
        .sendGoogleAnalyticsEvent(any(), eq(AnalyticsEventCategory.NO_CHANGE_UPDATE_SUBMITTED), eq(Map.of("project_type", qaProjectDetail.getProjectType().name())));
  }

  @Test
  public void saveNoUpdate_whenAuthenticatedAndQAAndInvalidForm_thenNoCreate() throws Exception {
    var bindingResult = new BeanPropertyBindingResult(ProvideNoUpdateForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(operatorProjectUpdateService.validate(any(), any())).thenReturn(bindingResult);

    when(operatorProjectUpdateService.getProjectProvideNoUpdateModelAndView(any(), any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .saveNoUpdate(QA_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(operatorProjectUpdateService, times(1)).validate(any(), any());
    verify(operatorProjectUpdateService, times(0)).createNoUpdateNotification(any(), any(), any());
    verifyNoInteractions(analyticsService);
  }

  @Test
  public void saveNoUpdate_whenUnauthenticatedAndQA_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .saveNoUpdate(QA_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(operatorProjectUpdateService, never()).createNoUpdateNotification(any(), any(), any());
  }

  @Test
  public void saveNoUpdate_whenAuthenticatedAndDraft_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(OperatorUpdateController.class)
            .saveNoUpdate(DRAFT_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(operatorProjectUpdateService, never()).createNoUpdateNotification(any(), any(), any());
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
  public void provideNoUpdateConfirmation_whenNoNoUpdateNotificationExists_thenAccessDeniedException() throws Exception {
    when(operatorProjectUpdateService.getProjectProvideNoUpdateConfirmationModelAndView(qaProjectDetail)).thenThrow(
        new AccessDeniedException("")
    );

    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdateConfirmation(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  public void provideNoUpdateConfirmation_whenNoUpdateNotificationExists_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(OperatorUpdateController.class).provideNoUpdateConfirmation(QA_PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());

    verify(operatorProjectUpdateService, times(1)).confirmNoUpdateExistsForProjectDetail(qaProjectDetail);
  }
}
