package uk.co.ogauthority.pathfinder.controller.project.decommissioningschedule;

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
import org.springframework.web.servlet.ModelAndView;
import uk.co.ogauthority.pathfinder.auth.AuthenticatedUserAccount;
import uk.co.ogauthority.pathfinder.controller.ProjectContextAbstractControllerTest;
import uk.co.ogauthority.pathfinder.energyportal.service.SystemAccessService;
import uk.co.ogauthority.pathfinder.model.entity.project.ProjectDetail;
import uk.co.ogauthority.pathfinder.model.enums.ValidationType;
import uk.co.ogauthority.pathfinder.model.form.project.decommissioningschedule.DecommissioningScheduleForm;
import uk.co.ogauthority.pathfinder.mvc.ReverseRouter;
import uk.co.ogauthority.pathfinder.mvc.argumentresolver.ValidationTypeArgumentResolver;
import uk.co.ogauthority.pathfinder.service.project.decommissioningschedule.DecommissioningScheduleService;
import uk.co.ogauthority.pathfinder.service.project.projectcontext.ProjectContextService;
import uk.co.ogauthority.pathfinder.testutil.ProjectUtil;
import uk.co.ogauthority.pathfinder.testutil.UserTestingUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DecommissioningScheduleController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ProjectContextService.class))
public class DecommissioningScheduleControllerTest extends ProjectContextAbstractControllerTest  {

  private static final Integer PROJECT_ID = 1;

  @MockBean
  private DecommissioningScheduleService decommissioningScheduleService;

  private final ProjectDetail projectDetail = ProjectUtil.getProjectDetails();

  private final AuthenticatedUserAccount authenticatedUser = UserTestingUtil.getAuthenticatedUserAccount(
      SystemAccessService.CREATE_PROJECT_PRIVILEGES);
  private final AuthenticatedUserAccount unauthenticatedUser = UserTestingUtil.getAuthenticatedUserAccount();

  @Before
  public void setUp() throws Exception {
    when(projectService.getLatestDetailOrError(PROJECT_ID)).thenReturn(projectDetail);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, authenticatedUser)).thenReturn(true);
    when(projectOperatorService.isUserInProjectTeamOrRegulator(projectDetail, unauthenticatedUser)).thenReturn(false);
  }

  @Test
  public void getDecommissioningSchedule_whenAuthenticated_thenAccess() throws Exception {
    when(decommissioningScheduleService.getForm(projectDetail))
        .thenReturn(new DecommissioningScheduleForm());

    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissioningScheduleController.class).getDecommissioningSchedule(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(authenticatedUser)))
        .andExpect(status().isOk());
  }

  @Test
  public void getDecommissioningSchedule_whenUnauthenticated_thenNoAccess() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(
        on(DecommissioningScheduleController.class).getDecommissioningSchedule(PROJECT_ID, null)))
        .with(authenticatedUserAndSession(unauthenticatedUser)))
        .andExpect(status().isForbidden());
  }

  @Test
  public void saveDecommissioningSchedule_whenUnauthenticatedPartialSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissioningScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissioningScheduleService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissioningScheduleController.class)
            .saveDecommissioningSchedule(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isForbidden());

    verify(decommissioningScheduleService, times(0)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissioningScheduleService, times(0)).createOrUpdate(any(), any());
  }

  @Test
  public void saveDecommissioningSchedule_whenUnauthenticatedFullSave_thenNoAccess() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissioningScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissioningScheduleService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissioningScheduleController.class)
            .saveDecommissioningSchedule(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(unauthenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isForbidden());

    verify(decommissioningScheduleService, times(0)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissioningScheduleService, times(0)).createOrUpdate(any(), any());
  }

  @Test
  public void saveDecommissioningSchedule_whenValidFormAndPartialSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissioningScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissioningScheduleService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissioningScheduleController.class)
            .saveDecommissioningSchedule(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissioningScheduleService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissioningScheduleService, times(1)).createOrUpdate(any(), any());
  }

  @Test
  public void saveDecommissioningSchedule_whenValidFormAndFullSave_thenCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissioningScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(decommissioningScheduleService.validate(any(), any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissioningScheduleController.class)
            .saveDecommissioningSchedule(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().is3xxRedirection());

    verify(decommissioningScheduleService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissioningScheduleService, times(1)).createOrUpdate(any(), any());
  }

  @Test
  public void saveDecommissioningSchedule_whenInvalidFormAndFullSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.COMPLETE, ValidationTypeArgumentResolver.COMPLETE);
    }};

    var form = new DecommissioningScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(decommissioningScheduleService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(decommissioningScheduleService.getDecommissioningScheduleModelAndView(any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissioningScheduleController.class)
            .saveDecommissioningSchedule(PROJECT_ID, form, bindingResult, ValidationType.FULL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeParams))
        .andExpect(status().isOk());

    verify(decommissioningScheduleService, times(1)).validate(any(), any(), eq(ValidationType.FULL));
    verify(decommissioningScheduleService, times(0)).createOrUpdate(any(), any());
  }

  @Test
  public void saveDecommissioningSchedule_whenInvalidFormAndPartialSave_thenNoCreate() throws Exception {
    MultiValueMap<String, String> completeLaterParams = new LinkedMultiValueMap<>() {{
      add(ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER, ValidationTypeArgumentResolver.SAVE_AND_COMPLETE_LATER);
    }};

    var form = new DecommissioningScheduleForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(decommissioningScheduleService.validate(any(), any(), any())).thenReturn(bindingResult);
    when(decommissioningScheduleService.getDecommissioningScheduleModelAndView(any(), any())).thenReturn(new ModelAndView());

    mockMvc.perform(
        post(ReverseRouter.route(on(DecommissioningScheduleController.class)
            .saveDecommissioningSchedule(PROJECT_ID, form, bindingResult, ValidationType.PARTIAL, null)
        ))
            .with(authenticatedUserAndSession(authenticatedUser))
            .with(csrf())
            .params(completeLaterParams))
        .andExpect(status().isOk());

    verify(decommissioningScheduleService, times(1)).validate(any(), any(), eq(ValidationType.PARTIAL));
    verify(decommissioningScheduleService, times(0)).createOrUpdate(any(), any());
  }
}
