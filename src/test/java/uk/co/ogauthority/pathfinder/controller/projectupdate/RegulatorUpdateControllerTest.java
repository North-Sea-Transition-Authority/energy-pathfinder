package uk.co.ogauthority.pathfinder.controller.projectupdate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.analytics.AnalyticsEventCategory;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.RegulatorProjectUpdateContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.exception.PathfinderEntityNotFoundException;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.project.ProjectStatus;
import uk.co.ogauthority.pathfinder.model.form.projectupdate.RequestUpdateForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectPermission;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorProjectUpdateContextService;
import uk.co.ogauthority.pathfinder.service.projectupdate.RegulatorUpdateRequestService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(
    value = RegulatorUpdateController.class,
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProjectContextService.class, RegulatorProjectUpdateContextService.class})
)
public class RegulatorUpdateControllerTest extends RegulatorProjectUpdateContextAbstractControllerTest {

  private static final Integer QA_PROJECT_ID = 1;
  private static final Integer UNSUBMITTED_PROJECT_ID = 2;

  @MockitoBean
  private RegulatorUpdateRequestService regulatorUpdateRequestService;

  private final ProjectDetail qaProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.QA);
  private final ProjectDetail unsubmittedProjectDetail = ProjectUtil.getProjectDetails(ProjectStatus.DRAFT);

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      ProjectPermission.REQUEST_UPDATE.getUserPrivileges());
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setup() {
    when(projectService.getLatestSubmittedDetailOrError(QA_PROJECT_ID)).thenReturn(qaProjectDetail);

    when(projectOperatorService.isUserInProjectTeam(qaProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(qaProjectDetail, unauthenticatedUser)).thenReturn(false);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeam(unsubmittedProjectDetail, unauthenticatedUser)).thenReturn(false);

    doThrow(new PathfinderEntityNotFoundException("test")).when(projectService).getLatestSubmittedDetailOrError(UNSUBMITTED_PROJECT_ID);
  }

  @Test
  public void getRequestUpdate_whenAuthenticatedAndQA_thenAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(RegulatorUpdateController.class).getRequestUpdate(QA_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getRequestUpdate_whenUnauthenticatedAndQA_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(RegulatorUpdateController.class).getRequestUpdate(QA_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void getRequestUpdate_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(RegulatorUpdateController.class).getRequestUpdate(UNSUBMITTED_PROJECT_ID, null, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isNotFound());
  }

  @Test
  public void requestUpdate_whenAuthenticatedAndQAAndValidForm_thenCreate() throws Exception {
    var bindingResult = new BeanPropertyBindingResult(RequestUpdateForm.class, "form");
    when(regulatorUpdateRequestService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(QA_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().is3xxRedirection());

    verify(regulatorUpdateRequestService, times(1)).validate(any(), any());
    verify(regulatorUpdateRequestService, times(1)).requestUpdate(any(), any(), any());
    verify(analyticsService, times(1))
        .sendAnalyticsEvent(any(), eq(AnalyticsEventCategory.UPDATE_REQUESTED), eq(Map.of("project_type", qaProjectDetail.getProjectType().name())));
  }

  @Test
  public void requestUpdate_whenAuthenticatedAndQAAndInvalidForm_thenNoCreate() throws Exception {
    var bindingResult = new BeanPropertyBindingResult(RequestUpdateForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(regulatorUpdateRequestService.validate(any(), any())).thenReturn(bindingResult);

    when(regulatorUpdateRequestService.getRequestUpdateModelAndView(any(), any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(QA_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(regulatorUpdateRequestService, times(1)).validate(any(), any());
    verify(regulatorUpdateRequestService, times(0)).requestUpdate(any(), any(), any());
    verifyNoInteractions(analyticsService);
  }

  @Test
  public void requestUpdate_whenUnauthenticatedAndQA_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(QA_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf()))
        .andExpect(status().isForbidden());

    verify(regulatorUpdateRequestService, never()).requestUpdate(any(), any(), any());
  }

  @Test
  public void requestUpdate_whenAuthenticatedAndUnsubmitted_thenNoAccess() throws Exception {
    mockMvc.perform(
        post(ReverseRouter.route(on(RegulatorUpdateController.class)
            .requestUpdate(UNSUBMITTED_PROJECT_ID, null, null, null, null, Optional.empty())
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf()))
        .andExpect(status().isNotFound());

    verify(regulatorUpdateRequestService, never()).requestUpdate(any(), any(), any());
  }
}
