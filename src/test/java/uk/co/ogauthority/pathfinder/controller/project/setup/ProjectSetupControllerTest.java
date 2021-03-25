package uk.co.ogauthority.pathfinder.controller.project.setup;

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.setup.ProjectSetupForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.service.project.projectinformation.ProjectInformationService;
import uk.co.ogauthority.pathfinder.service.project.setup.ProjectSetupService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProjectSetupController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class ProjectSetupControllerTest extends ProjectContextAbstractControllerTest {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private ProjectSetupService projectSetupService;

  @MockBean
  private ProjectInformationService projectInformationService;

  private final ProjectDetail detail = ProjectUtil.getProjectDetails();


  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(detail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(detail, unAuthenticatedUser)).thenReturn(false);
    when(projectSetupService.getProjectSetupModelAndView(any(), any())).thenCallRealMethod();
  }

  @Test
  public void authenticatedUser_hasAccessToProjectSetup() throws Exception {
    when(projectSetupService.getForm(detail)).thenReturn(new ProjectSetupForm());
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectSetupController.class).getProjectSetup(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccessProjectSetup() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(ProjectSetupController.class).getProjectSetup(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveProjectSetup_partialValidation() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectSetupForm.class, "form");
    when(projectSetupService.validate(any(), any(), any(),any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectSetupController.class)
            .saveProjectSetup(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(projectSetupService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL), any());
    verify(projectSetupService, times(1)).createOrUpdateProjectTaskListSetup(any(), any());
  }

  @Test
  public void saveProjectSetup_partialValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectSetupForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(projectSetupService.validate(any(), any(), any(),any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectSetupController.class)
            .saveProjectSetup(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(projectSetupService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL), any());
    verify(projectSetupService, times(0)).createOrUpdateProjectTaskListSetup(any(), any());
  }

  @Test
  public void saveProjectSetup_fullValidation_invalid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectSetupForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(projectSetupService.validate(any(), any(), any(),any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectSetupController.class)
            .saveProjectSetup(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(projectSetupService, times(1)).validate(any(), any(), eq(ValidationType.FULL), any());
    verify(projectSetupService, times(0)).createOrUpdateProjectTaskListSetup(any(), any());
  }

  @Test
  public void saveProjectSetup_fullValidation_valid() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectSetupForm.class, "form");
    when(projectSetupService.validate(any(), any(), any(),any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(ProjectSetupController.class)
            .saveProjectSetup(PROJECT_ID, null, null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(projectSetupService, times(1)).validate(any(), any(), eq(ValidationType.FULL), any());
    verify(projectSetupService, times(1)).createOrUpdateProjectTaskListSetup(any(), any());
  }

}
