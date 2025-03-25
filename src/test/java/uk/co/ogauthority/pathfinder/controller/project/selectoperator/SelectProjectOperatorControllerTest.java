package uk.co.ogauthority.pathfinder.controller.project.selectoperator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.ogauthority.pathfinder.util.TestUserProvider.authenticatedUserAndSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.AbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.form.project.selectoperator.ProjectOperatorForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.service.project.StartProjectService;
import uk.co.ogauthority.pathfinder.service.project.projectoperator.ProjectOperatorModelService;
import uk.co.ogauthority.pathfinder.service.project.selectoperator.SelectOperatorService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(SelectProjectOperatorController.class)
public class SelectProjectOperatorControllerTest extends AbstractControllerTest {

  @MockitoBean
  private StartProjectService startProjectService;

  @MockitoBean
  private SelectOperatorService selectOperatorService;

  @MockitoBean
  private ProjectOperatorModelService projectOperatorModelService;

  private static final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);

  private static final AuthenticatedUserAccount unAuthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();


  @Test
  public void authenticatedUser_hasAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(SelectProjectOperatorController.class).selectOperator(null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void unAuthenticatedUser_cannotAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(SelectProjectOperatorController.class).selectOperator(null)))
        .with(authenticatedUserAndSession(unAuthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void startProject_validForm() throws Exception {

    when(startProjectService.createInfrastructureProject(any(), any())).thenReturn(
        ProjectUtil.getProjectDetails()
    );

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(SelectProjectOperatorController.PRIMARY_BUTTON_TEXT, SelectProjectOperatorController.PRIMARY_BUTTON_TEXT);
      add("operator", "1");
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectOperatorForm.class, "form");
    when(selectOperatorService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SelectProjectOperatorController.class)
            .startProject(null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(startProjectService, times(1)).createInfrastructureProject(any(), any());
  }

  @Test
  public void startProject_inValidForm() throws Exception {
    when(projectOperatorModelService.getProjectOperatorModelAndView(
        any(),
        any(),
        any(),
        any(),
        any()
    )).thenReturn(new ModelAndView("dummy")); //Just return a model and view that needs no params.

    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(SelectProjectOperatorController.PRIMARY_BUTTON_TEXT, SelectProjectOperatorController.PRIMARY_BUTTON_TEXT);
    }};

    var bindingResult = new BeanPropertyBindingResult(ProjectOperatorForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(selectOperatorService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(SelectProjectOperatorController.class)
            .startProject(null, null, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is2xxSuccessful());

    verify(startProjectService, times(0)).createInfrastructureProject(any(), any());
  }
}
